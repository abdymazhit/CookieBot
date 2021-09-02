package net.Abdymazhit.CookieBot.listeners.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.enums.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.sql.*;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

/**
 * Команда авторизации
 *
 * @version   02.09.2021
 * @author    Islam Abdymazhit
 */
public class AuthCommandListener extends ListenerAdapter {

    /**
     * Событие отправки команды
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        MessageChannel messageChannel = event.getChannel();
        Member member = event.getMember();

        if(!event.getName().equals("auth")) return;
        if(!messageChannel.equals(CookieBot.getInstance().separateChannels.getAuthChannel().channel)) return;
        if(member == null) return;

        OptionMapping tokenOption = event.getOption("token");
        if(tokenOption == null) {
            event.reply("Ошибка! Токен авторизации не найден!").setEphemeral(true).queue();
            return;
        }

        if(member.getRoles().contains(Rank.PLAYER.getRole()))  {
            event.reply("Ошибка! Вы уже авторизованы!").setEphemeral(true).queue();
            return;
        }

        String token = tokenOption.getAsString().replace("https://api.vime.world/web/token/", "");
        String authInfo = CookieBot.getInstance().utils.sendGetRequest("https://api.vimeworld.ru/misc/token/" + token);
        if(authInfo == null) {
            event.reply("Ошибка! Неверный токен авторизации!").setEphemeral(true).queue();
            return;
        }

        JsonObject authObject = JsonParser.parseString(authInfo).getAsJsonObject();

        JsonElement validElement = authObject.get("valid");
        if(validElement == null) {
            event.reply("Ошибка! Токен авторизации не действителен!").setEphemeral(true).queue();
            return;
        }

        boolean isValid = validElement.getAsBoolean();
        if(!isValid) {
            event.reply("Ошибка! Неверный токен авторизации или время действия токена истекло!").setEphemeral(true).queue();
            return;
        }

        String type = authObject.get("type").getAsString();
        if(!type.equals("AUTH")) {
            event.reply("Ошибка! Тип токена должен быть AUTH!").setEphemeral(true).queue();
            return;
        }

        JsonElement ownerElement = authObject.get("owner");
        if(ownerElement.isJsonNull()) {
            event.reply("Ошибка! Владелец токена не найден!").setEphemeral(true).queue();
            return;
        }

        JsonObject ownerObject = ownerElement.getAsJsonObject();
        String username = ownerObject.get("username").getAsString();
        Rank rank = Rank.valueOf(ownerObject.get("rank").getAsString());

        String[] banInfo = getBanInfo(username);
        System.out.println(banInfo.length);
        if(banInfo.length == 2) {
            event.reply("Вы были заблокированы! Заблокированы до: " + banInfo[0] +
                    ". Причина блокировки: " + banInfo[1]).setEphemeral(true).queue();
            return;
        }

        boolean isAdded = addUser(member.getId(), username);
        if(!isAdded) {
            event.reply("Ошибка! Попробуйте авторизоваться позже!").setEphemeral(true).queue();
            return;
        }

        // Изменить пользователю ник
        if(CookieBot.getInstance().guild.getSelfMember().canInteract(member)) {
            member.modifyNickname(username).queue();
        }

        // Изменить роль пользователя
        if(!rank.equals(Rank.PLAYER)) {
            CookieBot.getInstance().guild.addRoleToMember(member, rank.getRole()).queue();
        }
        CookieBot.getInstance().guild.addRoleToMember(member, Rank.PLAYER.getRole()).queue();

        // Отправить сообщение о успешной авторизации
        event.reply("Вы успешно авторизовались! Ваш ник: " + username + ", ранг: " + rank.getName()).setEphemeral(true).queue();
    }

    /**
     * Получает информацию о блокировке пользователя
     * @param username Ник пользователя
     * @return Информация о блокировке пользователя
     */
    private String[] getBanInfo(String username) {
        String[] result = new String[0];

        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT end_time, reason FROM bans WHERE username = ?;");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            if(resultSet.next()) {
                Timestamp endTime = resultSet.getTimestamp("end_time");
                String reason = resultSet.getString("reason");

                Timestamp nowTime = Timestamp.from(Instant.now());

                // Снять блокировку, если время конца блокировки прошло
                if(nowTime.after(endTime)) {
                    PreparedStatement deleteStatement = connection.prepareStatement(
                            "DELETE FROM bans WHERE username = ?;");
                    deleteStatement.setString(1, username);
                    deleteStatement.executeUpdate();
                    deleteStatement.close();
                } else {
                    result = new String[] { endTime.toString(), reason };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Добавляет пользователя в базу данных
     * @param memberId Id пользователя
     * @param username Ник пользователя
     * @return Значение, добавлен ли пользователь
     */
    private boolean addUser(String memberId, String username) {
        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT member_id FROM users WHERE username = ?;");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            if(resultSet.next()) {
                String member_id = resultSet.getString("member_id");
                Member member = CookieBot.getInstance().guild.retrieveMemberById(member_id).submit().get();

                if(member != null) {
                    // Удалить роли старого пользователя
                    for(Role role : member.getRoles()) {
                        if(!role.equals(Rank.OWNER.getRole())) {
                            CookieBot.getInstance().guild.removeRoleFromMember(member, role).queue();
                        }
                    }

                    // Изменить ник старого пользователя
                    if(CookieBot.getInstance().guild.getSelfMember().canInteract(member)) {
                        member.modifyNickname(member.getId()).queue();
                    }
                }

                PreparedStatement statement = connection.prepareStatement("UPDATE users SET member_id = ? WHERE username = ?;");
                statement.setString(1, memberId);
                statement.setString(2, username);
                statement.executeUpdate();
                statement.close();
            } else {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO users (member_id, username) VALUES (?, ?);");
                statement.setString(1, memberId);
                statement.setString(2, username);
                statement.executeUpdate();
                statement.close();
            }

            PreparedStatement statement = connection.prepareStatement("INSERT INTO users_history (member_id, username, authorized_in) VALUES (?, ?, ?);");
            statement.setString(1, memberId);
            statement.setString(2, username);
            statement.setTimestamp(3, Timestamp.from(Instant.now()));
            statement.executeUpdate();
            statement.close();

            // Вернуть значение, что пользователь добавлен
            return true;
        } catch (SQLException | ExecutionException | InterruptedException e) {
            e.printStackTrace();

            // Вернуть значение, что произошла ошибка
            return false;
        }
    }
}
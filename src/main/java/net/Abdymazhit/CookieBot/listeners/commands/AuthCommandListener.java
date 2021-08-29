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
 * @version   29.08.2021
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

        if(!messageChannel.getName().equals("авторизация")) return;
        if(!event.getName().equals("auth")) return;
        if(member == null) return;

        OptionMapping tokenOption = event.getOption("token");
        if(tokenOption == null) return;

        if(member.getRoles().contains(Rank.PLAYER.getRole()))  {
            event.reply("Ошибка! Вы уже авторизованы!").setEphemeral(true).submit();
            return;
        }

        String token = tokenOption.getAsString().replace("https://api.vime.world/web/token/", "");
        String authInfo = CookieBot.getInstance().utils.sendGetRequest("https://api.vimeworld.ru/misc/token/" + token);
        if(authInfo == null) {
            event.reply("Ошибка! Неверный токен авторизации!").setEphemeral(true).submit();
            return;
        }

        JsonObject authObject = JsonParser.parseString(authInfo).getAsJsonObject();

        JsonElement validElement = authObject.get("valid");
        if(validElement == null) {
            event.reply("Ошибка! Неверный токен авторизации!").setEphemeral(true).submit();
            return;
        }

        boolean isValid = validElement.getAsBoolean();
        if(!isValid) {
            event.reply("Ошибка! Неверный токен авторизации или время действия токена истекло!").setEphemeral(true).submit();
            return;
        }

        String type = authObject.get("type").getAsString();
        if(!type.equals("AUTH")) {
            event.reply("Ошибка! Тип токена должен быть AUTH!").setEphemeral(true).submit();
            return;
        }

        JsonElement ownerElement = authObject.get("owner");
        if(ownerElement.isJsonNull()) {
            event.reply("Ошибка! Владелец токена не найден!").setEphemeral(true).submit();
            return;
        }

        JsonObject ownerObject = ownerElement.getAsJsonObject();
        String username = ownerObject.get("username").getAsString();
        Rank rank = Rank.valueOf(ownerObject.get("rank").getAsString());

        boolean isAdded = addUser(member.getId(), username);
        if(isAdded) {
            // Изменить пользователю ник
            if(!member.isOwner()) {
                member.modifyNickname(username).submit();
            }

            // Изменить роль пользователя
            if(!rank.equals(Rank.PLAYER)) {
                CookieBot.getInstance().jda.getGuilds().get(0).addRoleToMember(member, rank.getRole()).submit();
            }
            CookieBot.getInstance().jda.getGuilds().get(0).addRoleToMember(member, Rank.PLAYER.getRole()).submit();

            // Отправить сообщение о успешной авторизации
            event.reply("Вы успешно авторизовались! Ваш ник: " + username + ", ранг: " + rank.getName()).setEphemeral(true).submit();
        } else {
            event.reply("Ошибка! Попробуйте авторизоваться позже.").setEphemeral(true).submit();
        }
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
                Member member = CookieBot.getInstance().jda.getGuilds().get(0).retrieveMemberById(member_id).submit().get();

                if(member != null) {
                    // Удалить роли старого пользователя
                    for(Role role : member.getRoles()) {
                        CookieBot.getInstance().jda.getGuilds().get(0).removeRoleFromMember(member, role).submit();
                    }

                    // Изменить ник старого пользователя
                    if(!member.isOwner()) {
                        member.modifyNickname(member.getId()).submit();
                    }
                }

                PreparedStatement statement = connection.prepareStatement("UPDATE users SET member_id = ? WHERE username = ?;");
                statement.setString(1, memberId);
                statement.setString(2, username);
                statement.executeUpdate();
                statement.close();
            } else {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO users (member_id, username, authorized_in) VALUES (?, ?, ?);");
                statement.setString(1, memberId);
                statement.setString(2, username);
                statement.setTimestamp(3, Timestamp.from(Instant.now()));
                statement.executeUpdate();
                statement.close();
            }

            // Вернуть значение, что пользователь добавлен
            return true;
        } catch (SQLException | ExecutionException | InterruptedException e) {
            e.printStackTrace();

            // Вернуть значение, что пользователь не добавлен
            return false;
        }
    }
}
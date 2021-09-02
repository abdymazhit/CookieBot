package net.Abdymazhit.CookieBot.listeners.commands;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.TicketChannel;
import net.Abdymazhit.CookieBot.enums.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

/**
 * Команда блокировки
 *
 * @version   02.09.2021
 * @author    Islam Abdymazhit
 */
public class BanCommandListener extends ListenerAdapter {

    /**
     * Событие отправки команды
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        Member member = event.getMember();

        if (!event.getName().equals("ban")) return;
        if (member == null) return;

        if(member.getRoles().contains(Rank.OWNER.getRole())) {
            event.reply("У вас нет прав для этого действия!").setEphemeral(true).queue();
            return;
        }

        OptionMapping usernameOption = event.getOption("username");
        if (usernameOption == null) {
            event.reply("Ошибка! Укажите ник пользователя!").setEphemeral(true).queue();
            return;
        }
        String username = usernameOption.getAsString();

        OptionMapping timeOption = event.getOption("time");
        if (timeOption == null) {
            event.reply("Ошибка! Укажите время блокировки в минутах!").setEphemeral(true).queue();
            return;
        }
        int time = Integer.parseInt(timeOption.getAsString());

        OptionMapping reasonOption = event.getOption("reason");
        if (reasonOption == null) {
            event.reply("Ошибка! Укажите причину блокировки!").setEphemeral(true).queue();
            return;
        }
        String reason = reasonOption.getAsString();

        Member user = getUser(usernameOption.getAsString());
        if(user == null) {
            event.reply("Ошибка! Пользователь не найден!").setEphemeral(true).queue();
            return;
        }

        boolean isAdded;
        if(member.getNickname() == null) {
            isAdded = addBanEntry(username, time, reason, member.getEffectiveName());
        } else {
            isAdded = addBanEntry(username, time, reason, member.getNickname());
        }

        if(!isAdded) {
            event.reply("Ошибка! Произошла ошибка при добавлении записи о блокировке пользователя!").setEphemeral(true).queue();
            return;
        }

        // Удалить роли пользователя
        for(Role role : user.getRoles()) {
            if(!role.equals(Rank.OWNER.getRole())) {
                CookieBot.getInstance().guild.removeRoleFromMember(user, role).queue();
            }
        }

        // Изменить ник пользователя
        if(CookieBot.getInstance().guild.getSelfMember().canInteract(user)) {
            user.modifyNickname(user.getId()).queue();
        }

        // Удалить каналы тикетов пользователя
        TicketChannel ticketChannel = CookieBot.getInstance().ticketsCategory.getTicketChannel(member.getId());
        if(ticketChannel != null) {
            ticketChannel.channel.delete().queue();
            CookieBot.getInstance().ticketsCategory.removeTicketChannel(ticketChannel, member);
        }

        event.reply("Пользователь " + username + " успешно заблокирован! Время блокировки в минутах: "
                + time + ". Причина блокировки: " + reason).setEphemeral(true).queue();
    }

    /**
     * Получает пользователя
     * @param username Ник пользователя
     * @return Пользователь
     */
    private Member getUser(String username) {
        Member user = null;

        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT member_id FROM users WHERE username = ?;");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            if(resultSet.next()) {
                String memberId = resultSet.getString("member_id");
                user = CookieBot.getInstance().guild.retrieveMemberById(memberId).submit().get();
            }
        } catch (SQLException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // Вернуть пользователя
        return user;
    }

    /**
     * Добавляет запись о блокировке пользователя
     * @param username Ник пользователя
     * @param time Время в минутах
     * @param reason Причина блокировки
     * @param admin Блокирующий
     * @return Значение, добавлена ли запись о блокировке пользователя
     */
    private boolean addBanEntry(String username, int time, String reason, String admin) {
        Timestamp endTime = Timestamp.from(Instant.now().plus(time, ChronoUnit.MINUTES));

        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM bans WHERE username = ?;");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            if(resultSet.next()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE bans SET end_time = ?, reason = ? WHERE username = ?;");
                statement.setTimestamp(1, endTime);
                statement.setString(2, reason);
                statement.setString(3, username);
                statement.executeUpdate();
                statement.close();
            } else {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO bans (username, end_time, reason) VALUES (?, ?, ?);");
                statement.setString(1, username);
                statement.setTimestamp(2, endTime);
                statement.setString(3, reason);
                statement.executeUpdate();
                statement.close();
            }

            PreparedStatement statement = connection.prepareStatement("INSERT INTO bans_history (username, time, ban_time, reason, admin) VALUES (?, ?, ?, ?, ?);");
            statement.setString(1, username);
            statement.setTimestamp(2, Timestamp.from(Instant.now()));
            statement.setInt(3, time);
            statement.setString(4, reason);
            statement.setString(5, admin);
            statement.executeUpdate();
            statement.close();

            // Вернуть значение, что запись о блокировке пользователя добавлена
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            // Вернуть значение, что произошла ошибка
            return false;
        }
    }
}
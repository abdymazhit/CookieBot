package net.Abdymazhit.CookieBot.tickets;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.customs.TicketChannel;
import net.Abdymazhit.CookieBot.enums.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.sql.*;
import java.time.Instant;

/**
 * Представляет собой канал просмотра тикета
 *
 * @version   02.09.2021
 * @author    Islam Abdymazhit
 */
public class TicketViewingChannel extends TicketChannel {

    /** Значение, удаляется ли канал */
    private boolean isDeleting;

    /**
     * Инициализирует канал просмотра тикета
     * @param ticketNumber Номер канала тикета
     * @param ticket Тикет
     * @param member Пользователь
     */
    public TicketViewingChannel(int ticketNumber, Ticket ticket, Member member) {
        super(ticketNumber, ticket, member);
        sendChannelMessage();

        isDeleting = false;

        // Отправить информацию о тикете
        MessageEmbed ticketMessageEmbed = CookieBot.getInstance().utils.getTicketMessageEmbed(ticket, "Тикет " + ticket.getId());
        channel.sendMessageEmbeds(ticketMessageEmbed).queue();
    }

    /**
     * Отправляет сообщение канала тикета
     */
    private void sendChannelMessage() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Просмотр тикета");
        embedBuilder.setColor(0xFF58B9FF);
        if(member.getRoles().contains(Rank.MODER.getRole()) ||
                member.getRoles().contains(Rank.WARDEN.getRole()) ||
                member.getRoles().contains(Rank.CHIEF.getRole()) ||
                member.getRoles().contains(Rank.ADMIN.getRole()) ||
                member.getRoles().contains(Rank.OWNER.getRole())) {
            embedBuilder.addField("Удаление",
                    "Удаляйте только если тикет не правильный или не подлежит исправлению (фича). " +
                            "Восстановление удаленных тикетов не возможно! Введите команду `!delete` для удаления",
                    false);
            embedBuilder.addField("Исправление",
                    "Если тикет исправлен введите команду `!fix`",
                    false);
        }
        embedBuilder.addField("Отмена",
                "Для отмены просмотра тикета введите команду `!cancel`",
                false);
        embedBuilder.setDescription("Обратите внимание, у вас есть 60 минут для просмотра тикета");
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
        embedBuilder.clear();
    }

    /**
     * Событие получения сообщения
     *
     * @param message Сообщение
     */
    public void onMessageReceived(String message) {
        if(isDeleting) {
            channel.sendMessage("Канал удаляется!").queue();
            return;
        }

        switch (message) {
            case "!delete":
                if(!member.getRoles().contains(Rank.MODER.getRole()) &&
                        !member.getRoles().contains(Rank.WARDEN.getRole()) &&
                        !member.getRoles().contains(Rank.CHIEF.getRole()) &&
                        !member.getRoles().contains(Rank.ADMIN.getRole()) &&
                        !member.getRoles().contains(Rank.OWNER.getRole())) {
                    channel.sendMessage("У вас нет прав для этого действия!").queue();
                    return;
                }

                boolean isDeleted = deleteTicket();
                if(!isDeleted) {
                    channel.sendMessage("Ошибка! Произошла ошибка при удалении тикета!").queue();
                    return;
                }

                // Обновить список доступных тикетов продуктов
                CookieBot.getInstance().productsCategory.updateProductsAvailableTicketsList();

                channel.sendMessage("Тикет успешно удален!").queue();
                CookieBot.getInstance().ticketsCategory.deleteTicketViewingChannel(this);
                isDeleting = true;
                deleteChannel();
                return;
            case "!fix":
                if(!member.getRoles().contains(Rank.MODER.getRole()) &&
                        !member.getRoles().contains(Rank.WARDEN.getRole()) &&
                        !member.getRoles().contains(Rank.CHIEF.getRole()) &&
                        !member.getRoles().contains(Rank.ADMIN.getRole()) &&
                        !member.getRoles().contains(Rank.OWNER.getRole())) {
                    channel.sendMessage("У вас нет прав для этого действия!").queue();
                    return;
                }

                boolean isFixed = fixTicket();
                if(!isFixed) {
                    channel.sendMessage("Ошибка! Произошла ошибка при исправлении тикета!").queue();
                    return;
                }

                // Обновить список доступных тикетов продуктов
                CookieBot.getInstance().productsCategory.updateProductsAvailableTicketsList();

                channel.sendMessage("Статус тикета изменился на исправлен!").queue();
                CookieBot.getInstance().ticketsCategory.deleteTicketViewingChannel(this);
                isDeleting = true;
                deleteChannel();
                return;
            case "!cancel":
                channel.sendMessage("Отмена...").queue();
                isDeleting = true;
                deleteChannel();
                return;
             default:
                channel.sendMessage("Такой команды не существует!").queue();
        }
    }

    /**
     * Удаляет тикет
     * @return Значение, удален ли тикет
     */
    private boolean deleteTicket() {
        try {
            Connection connection = CookieBot.getInstance().database.getConnection();

            // Удалить тикет из таблицы доступных тикетов
            PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM available_tickets WHERE ticket_id = ?;");
            deleteStatement.setInt(1, ticket.getId());
            deleteStatement.executeUpdate();
            deleteStatement.close();

            // Добавить тикет в таблицу удаленных тикетов
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO deleted_tickets (ticket_id, deleter, deleted_at) " +
                    "SELECT ?, ?, ? WHERE NOT EXISTS (SELECT ticket_id FROM deleted_tickets WHERE ticket_id = ?);");
            insertStatement.setInt(1, ticket.getId());
            if(member.getNickname() != null) {
                insertStatement.setString(2, member.getNickname());
            } else {
                insertStatement.setString(2, member.getEffectiveName());
            }
            insertStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            insertStatement.setInt(4, ticket.getId());
            insertStatement.executeUpdate();
            insertStatement.close();

            // Вернуть значение, что тикет удален
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            // Вернуть значение, что произошла ошибка
            return false;
        }
    }

    /**
     * Исправляет тикет
     * @return Значение, исправлен ли тикет
     */
    private boolean fixTicket() {
        try {
            Connection connection = CookieBot.getInstance().database.getConnection();

            // Удалить тикет из таблицы доступных тикетов
            PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM available_tickets WHERE ticket_id = ?;");
            deleteStatement.setInt(1, ticket.getId());
            deleteStatement.executeUpdate();
            deleteStatement.close();

            // Добавить тикет в таблицу исправленных тикетов
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO fixed_tickets (ticket_id, fixer, fixed_on) " +
                    "SELECT ?, ?, ? WHERE NOT EXISTS (SELECT ticket_id FROM fixed_tickets WHERE ticket_id = ?);");
            insertStatement.setInt(1, ticket.getId());
            if(member.getNickname() != null) {
                insertStatement.setString(2, member.getNickname());
            } else {
                insertStatement.setString(2, member.getEffectiveName());
            }
            insertStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            insertStatement.setInt(4, ticket.getId());
            insertStatement.executeUpdate();
            insertStatement.close();

            // Вернуть значение, что тикет исправлен
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            // Вернуть значение, что произошла ошибка
            return false;
        }
    }
}
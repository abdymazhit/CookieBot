package net.Abdymazhit.CookieBot.tickets;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.customs.TicketChannel;
import net.Abdymazhit.CookieBot.enums.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Представляет собой канал верификации тикета
 *
 * @version   02.09.2021
 * @author    Islam Abdymazhit
 */
public class TicketVerificationChannel extends TicketChannel {

    /** Значение, удаляется ли канал */
    private boolean isDeleting;

    /**
     * Инициализирует канал верификации тикета
     * @param ticketNumber Номер канала тикета
     * @param ticket Тикет
     * @param member Пользователь
     */
    public TicketVerificationChannel(int ticketNumber, Ticket ticket, Member member) {
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
        embedBuilder.setTitle("Верификация тикета");
        embedBuilder.setColor(0xFF58B9FF);
        embedBuilder.addField("Правила",
                "1. Все пункты тикета должны быть строго заполнены\n" +
                        "2. Верифицируя данный тикет ответственность за неё будете нести вы\n" +
                        "3. Незнание данных правил не освобождает вас от ответственности",
                false
        );
        embedBuilder.addField("Не верифицировать",
                "Если тикет не правильно заполнен или есть ошибки, вы вправе " +
                        "не верифицировать тикет. Введите команду `!unverify` для не верификации тикета",
                false);
        embedBuilder.addField("Верифицировать",
                "При правильности заполнения тикета и соблюдения всех правил введите команду `!verify` " +
                        "для верификации тикета",
                false);
        embedBuilder.addField("Отмена",
                "В случае, если вы не уверены или есть сомнения по поводу правильности тикета введите команду `!cancel` " +
                        "для отмены действия верификации",
                false);
        embedBuilder.setDescription("Обратите внимание, у вас есть 60 минут для верификации тикета");
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
        embedBuilder.clear();
    }

    /**
     * Событие получения сообщения
     * @param message Сообщение
     */
    public void onMessageReceived(String message) {
        if(isDeleting) {
            channel.sendMessage("Канал удаляется!").queue();
            return;
        }

        switch (message) {
            case "!verify":
                if(!member.getRoles().contains(Rank.MODER.getRole()) &&
                        !member.getRoles().contains(Rank.WARDEN.getRole()) &&
                        !member.getRoles().contains(Rank.CHIEF.getRole()) &&
                        !member.getRoles().contains(Rank.ADMIN.getRole()) &&
                        !member.getRoles().contains(Rank.OWNER.getRole()) &&
                        !member.getRoles().contains(Rank.MODER_DISCORD.getRole())) {
                    channel.sendMessage("У вас нет прав для этого действия!").queue();
                    return;
                }

                boolean isVerified = verifyTicket();
                if(!isVerified) {
                    channel.sendMessage("Ошибка! Произошла ошибка при верификации тикета!").queue();
                    return;
                }

                // Обновить список ожидающих верификации тикетов
                CookieBot.getInstance().separateChannels.getVerificationChannel().updatePendingVerificationTicketsList();

                // Обновить список доступных тикетов продуктов
                CookieBot.getInstance().productsCategory.updateProductsAvailableTicketsList();

                channel.sendMessage("Тикет верифицирован!").queue();
                isDeleting = true;
                deleteChannel();
                return;
            case "!unverify":
                if(!member.getRoles().contains(Rank.MODER.getRole()) &&
                        !member.getRoles().contains(Rank.WARDEN.getRole()) &&
                        !member.getRoles().contains(Rank.CHIEF.getRole()) &&
                        !member.getRoles().contains(Rank.ADMIN.getRole()) &&
                        !member.getRoles().contains(Rank.OWNER.getRole()) &&
                        !member.getRoles().contains(Rank.MODER_DISCORD.getRole())) {
                    channel.sendMessage("У вас нет прав для этого действия!").queue();
                    return;
                }

                boolean isUnverified = unverifyTicket();
                if(!isUnverified) {
                    channel.sendMessage("Ошибка! Произошла ошибка при не верификации тикета!").queue();
                    return;
                }

                // Обновить список ожидающих верификации тикетов
                CookieBot.getInstance().separateChannels.getVerificationChannel().updatePendingVerificationTicketsList();

                channel.sendMessage("Тикет не верифицирован!").queue();
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
     * Верифицирует тикет
     * @return Значение, верифицирован ли тикет
     */
    private boolean verifyTicket() {
        String sqlDelete = "DELETE FROM pending_verification_tickets WHERE ticket_id = " + ticket.getId() + ";";

        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    sqlDelete +
                            " INSERT INTO verified_tickets (ticket_id, checker, checked_on) " +
                            "SELECT ?, ?, ? WHERE NOT EXISTS (SELECT ticket_id FROM verified_tickets WHERE ticket_id = ?); " +
                            "INSERT INTO available_tickets (ticket_id) " +
                            "SELECT ? WHERE NOT EXISTS (SELECT ticket_id FROM available_tickets WHERE ticket_id = ?); ");
            preparedStatement.setInt(1, ticket.getId());
            if(member.getNickname() != null) {
                preparedStatement.setString(2, member.getNickname());
            } else {
                preparedStatement.setString(2, member.getEffectiveName());
            }
            preparedStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            preparedStatement.setInt(4, ticket.getId());
            preparedStatement.setInt(5, ticket.getId());
            preparedStatement.setInt(6, ticket.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();

            // Вернуть значение, что тикет верифицирован
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            // Вернуть значение, что произошла ошибка
            return false;
        }
    }

    /**
     * Не верифицирует тикет
     * @return Значение, не верифицирован ли тикет
     */
    private boolean unverifyTicket() {
        String sqlDelete = "DELETE FROM pending_verification_tickets WHERE ticket_id = " + ticket.getId() + ";";

        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    sqlDelete +
                            " INSERT INTO unverified_tickets (ticket_id, checker, checked_on) " +
                            "SELECT ?, ?, ? WHERE NOT EXISTS (SELECT ticket_id FROM verified_tickets WHERE ticket_id = ?);");
            preparedStatement.setInt(1, ticket.getId());
            if(member.getNickname() != null) {
                preparedStatement.setString(2, member.getNickname());
            } else {
                preparedStatement.setString(2, member.getEffectiveName());
            }
            preparedStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            preparedStatement.setInt(4, ticket.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();

            // Вернуть значение, что тикет не верифицирован
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            // Вернуть значение, что произошла ошибка
            return false;
        }
    }
}
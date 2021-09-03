package net.Abdymazhit.CookieBot.seperateChannels;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Channel;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Message;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Отвечает за создание канала верификации тикетов
 *
 * @version   03.09.2021
 * @author    Islam Abdymazhit
 */
public class VerificationChannel extends Channel {

    /** Список верифицируемых тикетов */
    private List<Integer> ticketsInVerification;

    /** Сообщение ожидающих верификации тикетов */
    private Message pendingVerificationTicketsMessage;

    /**
     * Инициализирует канал верификации
     */
    public VerificationChannel() {
        List<Category> categories = CookieBot.getInstance().guild.getCategoriesByName("модерация", true);
        if(!categories.isEmpty()) {
            Category category = categories.get(0);
            deleteChannel(category, "верификация");
            createChannel(category, "верификация", 0);
            ticketsInVerification = new ArrayList<>();
            updatePendingVerificationTicketsList();
        }
    }

    /**
     * Обновляет список ожидающих верификации тикетов
     */
    public void updatePendingVerificationTicketsList() {
        List<Ticket> pendingVerificationTickets = getPendingVerificationTickets();

        StringBuilder ticketsString = new StringBuilder();
        for(Ticket ticket : pendingVerificationTickets) {
            if(!ticketsInVerification.contains(ticket.getId())) {
                ticketsString.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ожидающие верификации тикеты");
        embedBuilder.setColor(0xFF58B9FF);
        embedBuilder.addField("Количество тикетов: " + pendingVerificationTickets.size(), ticketsString.toString(), false);
        embedBuilder.addField("Верификация тикета", "Для верификации тикета введите команду `/verify id`", false);

        if(pendingVerificationTicketsMessage != null) {
            pendingVerificationTicketsMessage.editMessageEmbeds(embedBuilder.build()).queue();
        } else {
            try {
                pendingVerificationTicketsMessage = channel.sendMessageEmbeds(embedBuilder.build()).submit().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        embedBuilder.clear();
    }

    /**
     * Получает список ожидающих верификации тикетов
     * @return Список ожидающих верификации тикетов
     */
    private List<Ticket> getPendingVerificationTickets() {
        List<Ticket> tickets = new ArrayList<>();

        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT T1.id, T1.title, T1.created_on " +
                    "FROM tickets T1 JOIN pending_verification_tickets T2 ON T1.id = T2.ticket_id;");
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                Timestamp createdOn = resultSet.getTimestamp("created_on");

                Ticket ticket = new Ticket();
                ticket.setId(id);
                ticket.setTitle(title);
                ticket.setCreatedOn(createdOn);
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    /**
     * Получает список верифицируемых тикетов
     * @return Список верифицируемых тикетов
     */
    public List<Integer> getTicketsInVerification() {
        return ticketsInVerification;
    }
}
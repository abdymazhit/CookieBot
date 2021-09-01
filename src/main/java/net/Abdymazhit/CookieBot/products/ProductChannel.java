package net.Abdymazhit.CookieBot.products;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Channel;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.enums.Priority;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Представляет собой канал продукта
 *
 * @version   01.09.2021
 * @author    Islam Abdymazhit
 */
public class ProductChannel extends Channel {

    /** Сообщение о доступных тикетах продукта */
    private Message availableTicketsMessage;

    /**
     * Инициализирует канал продукта
     * @param category Категория продуктов
     * @param channelName Название канала продукта
     */
    public ProductChannel(Category category, String channelName) {
        createChannel(category.getName(), channelName);
    }

    /**
     * Отправляет сообщение канала продукта
     * @param messageEmbed Сообщение канала продукта
     */
    public void sendChannelMessage(MessageEmbed messageEmbed) {
        try {
            channel.sendMessageEmbeds(messageEmbed).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обновляет список доступных тикетов продукта
     */
    public void updateAvailableTicketsList() {
        List<Ticket> tickets = getProductAvailableTickets();

        StringBuilder trivialTickets = new StringBuilder();
        StringBuilder minorTickets = new StringBuilder();
        StringBuilder majorTickets = new StringBuilder();
        StringBuilder criticalTickets = new StringBuilder();
        StringBuilder blockerTickets = new StringBuilder();

        for(Ticket ticket : tickets) {
            if(ticket.getPriority().equals(Priority.TRIVIAL)) {
                trivialTickets.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            } else if(ticket.getPriority().equals(Priority.MINOR)) {
                minorTickets.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            } else if(ticket.getPriority().equals(Priority.MAJOR)) {
                majorTickets.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            } else if(ticket.getPriority().equals(Priority.CRITICAL)) {
                criticalTickets.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            } else if(ticket.getPriority().equals(Priority.BLOCKER)) {
                blockerTickets.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Известные баги");
        embedBuilder.setColor(0xFF58B9FF);
        embedBuilder.addField("Тривиальные", trivialTickets.toString(), false);
        embedBuilder.addField("Незначительные", minorTickets.toString(), false);
        embedBuilder.addField("Значительные", majorTickets.toString(), false);
        embedBuilder.addField("Критические", criticalTickets.toString(), false);
        embedBuilder.addField("Блокирующие", blockerTickets.toString(), false);
        embedBuilder.addField("Просмотр тикета", "Для просмотра тикета введите команду `/view id`", false);

        if(availableTicketsMessage != null) {
            availableTicketsMessage.editMessageEmbeds(embedBuilder.build()).queue();
        } else {
            try {
                availableTicketsMessage = channel.sendMessageEmbeds(embedBuilder.build()).submit().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }

        embedBuilder.clear();
    }

    /**
     * Получает список доступных тикетов продукта
     * @return Список доступных тикетов продукта
     */
    private List<Ticket> getProductAvailableTickets() {
        List<Ticket> tickets = new ArrayList<>();

        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT T1.id, T1.priority, T1.title, T1.created_on " +
                    "FROM tickets T1 JOIN available_tickets T2 ON T1.id = T2.ticket_id;");
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                int priority = resultSet.getInt("priority");
                String title = resultSet.getString("title");
                Timestamp createdOn = resultSet.getTimestamp("created_on");

                Ticket ticket = new Ticket();
                ticket.setId(id);
                ticket.setPriority(Priority.getPriority(priority));
                ticket.setTitle(title);
                ticket.setCreatedOn(createdOn);
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }
}
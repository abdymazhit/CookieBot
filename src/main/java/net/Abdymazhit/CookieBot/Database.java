package net.Abdymazhit.CookieBot;

import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.enums.Priority;
import net.Abdymazhit.CookieBot.tickets.TicketChannel;
import net.dv8tion.jda.api.entities.Message;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Отвечает за работу с базой данных
 *
 * @version   24.08.2021
 * @author    Islam Abdymazhit
 */
public class Database {

    /** URL базы данных */
    private static final String url = "jdbc:postgresql://host:port/database";

    /** Имя пользователя базы данных */
    private static final String username = "username";

    /** Пароль пользователя базы данных */
    private static final String password = "password";

    /** Подключение к базе данных */
    private Connection connection;

    /**
     * Подключается к базе данных
     */
    public Database() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if(connection == null) {
            throw new IllegalArgumentException("Не удалось подключиться к базе данных");
        }
    }

    /**
     * Добавляет запись о тикете
     * @param ticketChannel Канал тикета
     */
    public void addTicket(TicketChannel ticketChannel) {
        ticketChannel.getChannel().sendMessage("Тикет отправляется...").submit();

        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO tickets " +
                    "(product, priority, title, description, steps, result, should_be, materials, created_on) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            st.setString(1, ticketChannel.getTicket().getProductName());
            st.setInt(2, ticketChannel.getTicket().getPriority().getId());
            st.setString(3, ticketChannel.getTicket().getTitle());
            st.setString(4, ticketChannel.getTicket().getDescription());
            st.setString(5, ticketChannel.getTicket().getSteps());
            st.setString(6, ticketChannel.getTicket().getResult());
            st.setString(7, ticketChannel.getTicket().getShouldBe());
            st.setString(8, ticketChannel.getTicket().getMaterials());
            st.setTimestamp(9, Timestamp.from(Instant.now()));
            st.executeUpdate();
            st.close();

            ticketChannel.getChannel().sendMessage("Тикет успешно отправлен! Этот канал будет удален через 10 секунд!")
                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
            ticketChannel.getChannel().delete().submitAfter(10, TimeUnit.SECONDS);
        } catch (SQLException e) {
            e.printStackTrace();

            ticketChannel.getChannel().sendMessage("Произошла ошибка! Тикет не отправлен! " +
                    "Свяжитесь с разработчиком бота! Этот канал будет удален через 60 секунд!")
                    .delay(60, TimeUnit.SECONDS).flatMap(Message::delete).submit();
            ticketChannel.getChannel().delete().submitAfter(60, TimeUnit.SECONDS);
        }

        CookieBot.ticketsCategory.removeTicket(ticketChannel);
    }

    /**
     * Получает список тикетов продукта
     * @param productName Название продукта
     * @return Список тикетов продукта
     */
    public List<Ticket> getProductTickets(String productName) {
        List<Ticket> tickets = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, priority, title, created_on FROM tickets WHERE fixed_on is null");
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                int priority = resultSet.getInt("priority");
                String title = resultSet.getString("title");
                Timestamp createdOn = resultSet.getTimestamp("created_on");

                Ticket ticket = new Ticket(productName);
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
package net.Abdymazhit.CookieBot;

import net.Abdymazhit.CookieBot.tickets.Ticket;
import net.dv8tion.jda.api.entities.Message;

import java.sql.*;
import java.time.Instant;
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

        if (connection != null) {
            System.out.println("Успешное подключение к базе данных");
        } else {
            System.out.println("Не удалось подключиться к базе данных");
        }
    }

    /**
     * Добавляет запись о тикете
     * @param ticket Тикет
     */
    public void addTicket(Ticket ticket) {
        ticket.getChannel().sendMessage("Тикет отправляется...").submit();

        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO tickets " +
                    "(product, priority, title, description, steps, result, should_be, materials, created_on) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            st.setString(1, ticket.getProductName());
            st.setInt(2, ticket.getPriority().getId());
            st.setString(3, ticket.getTitle());
            st.setObject(4, ticket.getDescription());
            st.setObject(5, ticket.getSteps());
            st.setObject(6, ticket.getResult());
            st.setObject(7, ticket.getShouldBe());
            st.setObject(8, ticket.getMaterials());
            st.setObject(9, Timestamp.from(Instant.now()));
            st.executeUpdate();
            st.close();

            ticket.getChannel().sendMessage("Тикет успешно отправлен! Этот канал будет удален через 10 секунд!")
                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).submit();
            ticket.getChannel().delete().submitAfter(10, TimeUnit.SECONDS);
        } catch (SQLException e) {
            e.printStackTrace();

            ticket.getChannel().sendMessage("Произошла ошибка! Тикет не отправлен! " +
                    "Свяжитесь с разработчиком бота! Этот канал будет удален через 60 секунд!")
                    .delay(60, TimeUnit.SECONDS).flatMap(Message::delete).submit();
            ticket.getChannel().delete().submitAfter(60, TimeUnit.SECONDS);
        }

        CookieBot.tickets.removeTicket(ticket);
    }
}
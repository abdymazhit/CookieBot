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
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class Database {

    /** Подключение к базе данных */
    private Connection connection;

    /**
     * Подключается к базе данных
     */
    public Database() {
        Config.PostgreSQL config = CookieBot.getInstance().config.postgreSQL;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager.getConnection(config.url, config.username, config.password);
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
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tickets " +
                    "(creator, product, priority, title, description, steps, result, should_be, materials, created_on) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, ticketChannel.getTicket().getCreatorId());
            preparedStatement.setString(2, ticketChannel.getTicket().getProductName());
            preparedStatement.setInt(3, ticketChannel.getTicket().getPriority().getId());
            preparedStatement.setString(4, ticketChannel.getTicket().getTitle());
            preparedStatement.setString(5, ticketChannel.getTicket().getDescription());
            preparedStatement.setString(6, ticketChannel.getTicket().getSteps());
            preparedStatement.setString(7, ticketChannel.getTicket().getResult());
            preparedStatement.setString(8, ticketChannel.getTicket().getShouldBe());
            preparedStatement.setString(9, ticketChannel.getTicket().getMaterials());
            preparedStatement.setTimestamp(10, Timestamp.from(Instant.now()));
            preparedStatement.executeUpdate();
            preparedStatement.close();

            ticketChannel.getChannel().sendMessage("Тикет успешно отправлен! Этот канал будет удален через 10 секунд!").submit();
            ticketChannel.getChannel().delete().submitAfter(10, TimeUnit.SECONDS);

            // Обновить тикеты продуктов
            CookieBot.getInstance().productsCategory.updateProductsTickets();
        } catch (SQLException e) {
            e.printStackTrace();

            ticketChannel.getChannel().sendMessage("Произошла ошибка! Тикет не отправлен! " +
                    "Свяжитесь с разработчиком бота! Этот канал будет удален через 60 секунд!").submit();
            ticketChannel.getChannel().delete().submitAfter(60, TimeUnit.SECONDS);
        }

        CookieBot.getInstance().ticketsCategory.removeTicket(ticketChannel);
    }

    /**
     * Удаляет запись о тикете
     * @param message Сообщение события ButtonClickEvent
     * @return Значение, удален ли тикет
     */
    public boolean deleteTicket(Message message) {
        int ticketId = CookieBot.getInstance().utils.getIntByMessage(message);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE tickets SET created_on = null WHERE id = " + ticketId);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            // Вернуть значение, что тикет удален
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            // Вернуть значение, что тикет не удален
            return false;
        }
    }

    /**
     * Изменяет статус тикета на исправлен
     * @param message Сообщение события ButtonClickEvent
     * @return Значение, исправлен ли тикет
     */
    public boolean fixTicket(Message message) {
        int ticketId = CookieBot.getInstance().utils.getIntByMessage(message);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE tickets SET fixed_on = '" + Timestamp.from(Instant.now()) + "' WHERE id = " + ticketId);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            // Вернуть значение, что тикет не исправлен
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            // Вернуть значение, что тикет не исправлен
            return false;
        }
    }

    /**
     * Получает тикет по id
     * @param ticketId Id тикета
     * @return Тикет
     */
    public Ticket getTicket(int ticketId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM tickets WHERE id = " + ticketId);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String creatorId = resultSet.getString("creator");
                String productName = resultSet.getString("product");
                int priority = resultSet.getInt("priority");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String steps = resultSet.getString("steps");
                String result = resultSet.getString("result");
                String shouldBe = resultSet.getString("should_be");
                String materials = resultSet.getString("materials");
                Timestamp createdOn = resultSet.getTimestamp("created_on");

                Ticket ticket = new Ticket(productName);
                ticket.setId(id);
                ticket.setCreatorId(creatorId);
                ticket.setPriority(Priority.getPriority(priority));
                ticket.setTitle(title);
                ticket.setDescription(description);
                ticket.setSteps(steps);
                ticket.setResult(result);
                ticket.setShouldBe(shouldBe);
                ticket.setMaterials(materials);
                ticket.setCreatedOn(createdOn);

                return ticket;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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
                    "SELECT id, priority, title, created_on FROM tickets WHERE fixed_on IS NULL AND created_on IS NOT NULL");
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

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
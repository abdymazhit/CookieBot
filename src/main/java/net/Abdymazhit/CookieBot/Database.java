package net.Abdymazhit.CookieBot;

import net.Abdymazhit.CookieBot.customs.Config;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.enums.Priority;

import java.sql.*;

/**
 * Отвечает за работу с базой данных
 *
 * @version   01.09.2021
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

//        Создать таблицы, только при необходимости
//        createTables();
    }

    /**
     * Создает таблицы
     */
    private void createTables() {
        createUsersTable();
        createTicketsTable();
        createPendingVerificationTicketsTable();
        createUnverifiedTicketsTable();
        createVerifiedTicketsTable();
        createAvailableTicketsTable();
        createDeletedTicketsTable();
        createFixedTicketsTable();
    }

    /**
     * Создает таблицу пользователей
     */
    private void createUsersTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (" +
                    "id serial not null constraint users_pk primary key, " +
                    "member_id varchar(50) not null, " +
                    "username varchar(50) not null, " +
                    "authorized_in timestamp not null);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает таблицу тикетов
     */
    private void createTicketsTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS tickets (" +
                    "id serial not null constraint tickets_pk primary key, " +
                    "creator varchar(50) not null, " +
                    "product varchar(50) not null, " +
                    "priority smallint not null, " +
                    "title text not null, " +
                    "description text not null, " +
                    "steps text not null, " +
                    "result text not null, " +
                    "should_be text not null, " +
                    "materials text not null, " +
                    "created_on timestamp not null);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает таблицу ожидающих верификации тикетов
     */
    private void createPendingVerificationTicketsTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS pending_verification_tickets (" +
                    "id serial not null constraint pending_verification_tickets_pk primary key, " +
                    "ticket_id int not null);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает таблицу не верифицированных тикетов
     */
    private void createUnverifiedTicketsTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS unverified_tickets (" +
                    "id serial not null constraint unverified_tickets_pk primary key, " +
                    "ticket_id int not null, " +
                    "checker varchar(50) not null, " +
                    "checked_on timestamp not null);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает таблицу верифицированных тикетов
     */
    private void createVerifiedTicketsTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS verified_tickets (" +
                    "id serial not null constraint verified_tickets_pk primary key, " +
                    "ticket_id int not null, " +
                    "checker varchar(50) not null, " +
                    "checked_on timestamp not null);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает таблицу доступных тикетов
     */
    private void createAvailableTicketsTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS available_tickets (" +
                    "id serial not null constraint available_tickets_pk primary key, " +
                    "ticket_id int not null);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает таблицу удаленных тикетов
     */
    private void createDeletedTicketsTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS deleted_tickets (" +
                    "id serial not null constraint deleted_tickets_pk primary key, " +
                    "ticket_id int not null, " +
                    "deleter varchar(50) not null, " +
                    "deleted_at timestamp not null);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает таблицу исправленных тикетов
     */
    private void createFixedTicketsTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS fixed_tickets (" +
                    "id serial not null constraint fixed_tickets_pk primary key, " +
                    "ticket_id int not null, " +
                    "fixer varchar(50) not null, " +
                    "fixed_on timestamp not null);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает тикет
     * @param sql SQL запрос
     * @return Тикет
     */
    public Ticket getTicket(String sql) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            if(resultSet.next()) {
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

                Ticket ticket = new Ticket();
                ticket.setId(id);
                ticket.setCreator(creatorId);
                ticket.setProductName(productName);
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
     * Получает подключение к базе данных
     * @return Подключение к базе данных
     */
    public Connection getConnection() {
        return connection;
    }
}
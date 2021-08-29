package net.Abdymazhit.CookieBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Отвечает за работу с базой данных
 *
 * @version   29.08.2021
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
        } else {
            createUsersTable();
            createTicketsTable();
            createUncheckedTicketsTable();
            createCheckedTicketsTable();
            createAvailableTicketsTable();
            createDeletedTicketsTable();
            createFixedTicketsTable();
        }
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
     * Создает таблицу непроверенных тикетов
     */
    private void createUncheckedTicketsTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS unchecked_tickets (" +
                    "id serial not null constraint unchecked_tickets_pk primary key, " +
                    "ticket_id int not null);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает таблицу проверенных тикетов
     */
    private void createCheckedTicketsTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS checked_tickets (" +
                    "id serial not null constraint checked_tickets_pk primary key, " +
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
     * Получает подключение к базе данных
     * @return Подключение к базе данных
     */
    public Connection getConnection() {
        return connection;
    }
}
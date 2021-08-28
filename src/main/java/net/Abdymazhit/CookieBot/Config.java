package net.Abdymazhit.CookieBot;

/**
 * Файл конфигурации
 *
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class Config {

    /** Токен бота */
    public String token = "BOT-TOKEN";

    /** Параметры базы данных */
    public PostgreSQL postgreSQL = new PostgreSQL();

    /** Параметры базы данных */
    public static class PostgreSQL {
        public String url = "jdbc:postgresql://host:port/database";
        public String username = "username";
        public String password = "password";
    }
}
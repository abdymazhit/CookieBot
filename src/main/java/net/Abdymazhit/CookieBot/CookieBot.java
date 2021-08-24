package net.Abdymazhit.CookieBot;

import net.Abdymazhit.CookieBot.products.ProductsCategory;
import net.Abdymazhit.CookieBot.tickets.TicketsCategory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

/**
 * Главный класс, отвечает за инициализацию бота
 *
 * @version   24.08.2021
 * @author    Islam Abdymazhit
 */
public class CookieBot {

    /** Токен бота */
    public static String token = "BOT-TOKEN";

    /** Главный объект для работы с Discord API */
    public static JDA jda;

    /** База данных */
    public static Database database;

    /** Категория продуктов */
    public static ProductsCategory productsCategory;

    /** Категория тикетов */
    public static TicketsCategory ticketsCategory;

    /**
     * Инициализирует бота
     * @throws LoginException Ошибка входа
     * @throws InterruptedException Ошибка работы Discord API
     */
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.ZLIB);
        jda = builder.build().awaitReady();

        database = new Database();
        productsCategory = new ProductsCategory();
        ticketsCategory = new TicketsCategory();

        jda.addEventListener(new EventsListener()) ;
    }
}
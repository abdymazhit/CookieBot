package net.Abdymazhit.CookieBot;

import com.google.gson.Gson;
import net.Abdymazhit.CookieBot.listeners.MessageReceivedListener;
import net.Abdymazhit.CookieBot.listeners.UserUpdateOnlineStatusListener;
import net.Abdymazhit.CookieBot.listeners.buttons.CancelButtonListener;
import net.Abdymazhit.CookieBot.listeners.buttons.DeleteButtonListener;
import net.Abdymazhit.CookieBot.listeners.buttons.FixButtonListener;
import net.Abdymazhit.CookieBot.listeners.commands.AuthCommandListener;
import net.Abdymazhit.CookieBot.listeners.commands.TicketCommandListener;
import net.Abdymazhit.CookieBot.listeners.commands.UpdateCommandListener;
import net.Abdymazhit.CookieBot.listeners.commands.ViewCommandListener;
import net.Abdymazhit.CookieBot.products.ProductsCategory;
import net.Abdymazhit.CookieBot.tickets.TicketsCategory;
import net.Abdymazhit.CookieBot.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;

/**
 * Главный класс, отвечает за инициализацию бота
 *
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class CookieBot {

    /** Объект главного класса */
    private static CookieBot instance;

    /** Config файл */
    public Config config;

    /** Главный объект для работы с Discord API */
    public final JDA jda;

    /** База данных */
    public final Database database;

    /** Категория продуктов */
    public final ProductsCategory productsCategory;

    /** Категория тикетов */
    public final TicketsCategory ticketsCategory;

    /** Инструменты для упрощения работы */
    public final Utils utils;

    /**
     * Создает бота
     */
    public static void main(String[] args) throws LoginException, IOException, InterruptedException {
        new CookieBot();
    }

    /**
     * Инициализирует бота
     * @throws LoginException Ошибка входа
     * @throws InterruptedException Ошибка работы Discord API
     */
    public CookieBot() throws IOException, LoginException, InterruptedException {
        instance = this;
        readConfig();

        JDABuilder builder = JDABuilder.createDefault(config.token);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);
        builder.enableCache(CacheFlag.CLIENT_STATUS);
        builder.disableCache(CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.ZLIB);
        jda = builder.build().awaitReady();

        database = new Database();
        productsCategory = new ProductsCategory();
        ticketsCategory = new TicketsCategory();
        utils = new Utils();

        jda.getGuilds().get(0).upsertCommand("auth", "Авторизация")
                .addOption(OptionType.STRING, "token", "Токен авторизации").submit();

        jda.getGuilds().get(0).upsertCommand("view", "Просмотр тикета")
                .addOption(OptionType.NUMBER, "id", "Id тикета").submit();

        jda.getGuilds().get(0).upsertCommand("update", "Обновить все тикеты продуктов").submit();

        jda.addEventListener(new CancelButtonListener());
        jda.addEventListener(new DeleteButtonListener());
        jda.addEventListener(new FixButtonListener());

        jda.addEventListener(new AuthCommandListener());
        jda.addEventListener(new TicketCommandListener());
        jda.addEventListener(new UpdateCommandListener());
        jda.addEventListener(new ViewCommandListener());

        jda.addEventListener(new MessageReceivedListener());
        jda.addEventListener(new UserUpdateOnlineStatusListener());
    }

    /**
     * Получает данные с файла конфигурации
     */
    private void readConfig() throws IOException {
        Gson gson = new Gson();

        File configFile = new File("config.json");
        if (!configFile.exists()) {
            config = new Config();
            Writer writer = Files.newBufferedWriter(configFile.toPath());
            gson.toJson(config, writer);
            writer.close();
            System.exit(0);
        } else {
            Reader reader = Files.newBufferedReader(configFile.toPath());
            config = gson.fromJson(reader, Config.class);
            reader.close();
        }
    }

    /**
     * Получает объект главного класса
     * @return Объект главного класса
     */
    public static CookieBot getInstance() {
        return instance;
    }
}
package net.Abdymazhit.CookieBot;

import com.google.gson.Gson;
import net.Abdymazhit.CookieBot.customs.Config;
import net.Abdymazhit.CookieBot.listeners.MessageReceivedListener;
import net.Abdymazhit.CookieBot.listeners.UserUpdateOnlineStatusListener;
import net.Abdymazhit.CookieBot.listeners.commands.*;
import net.Abdymazhit.CookieBot.products.ProductsCategory;
import net.Abdymazhit.CookieBot.seperateChannels.SeparateChannels;
import net.Abdymazhit.CookieBot.tickets.TicketsCategory;
import net.Abdymazhit.CookieBot.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
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
 * @version   02.09.2021
 * @author    Islam Abdymazhit
 */
public class CookieBot {

    /** Объект главного класса */
    private static CookieBot instance;

    /** Config файл */
    public Config config;

    /** Текущий сервер */
    public final Guild guild;

    /** База данных */
    public final Database database;

    /** Отдельные каналы */
    public final SeparateChannels separateChannels;

    /** Категория продуктов */
    public final ProductsCategory productsCategory;

    /** Категория тикетов */
    public final TicketsCategory ticketsCategory;

    /** Инструменты для упрощения работы */
    public final Utils utils;

    /**
     * Создает бота
     * @throws IOException Ошибка получения данных с конфиг файла
     * @throws LoginException Ошибка входа
     * @throws InterruptedException Ошибка работы Discord API
     */
    public static void main(String[] args) throws IOException, LoginException, InterruptedException {
        new CookieBot();
    }

    /**
     * Инициализирует бота
     * @throws IOException Ошибка получения данных с конфиг файла
     * @throws LoginException Ошибка входа
     * @throws InterruptedException Ошибка работы Discord API
     */
    public CookieBot() throws IOException, LoginException, InterruptedException {
        instance = this;
        config = getConfig();

        JDABuilder builder = JDABuilder.createDefault(config.token);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);
        builder.enableCache(CacheFlag.CLIENT_STATUS);
        builder.disableCache(CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.ZLIB);
        JDA jda = builder.build().awaitReady();
        guild = jda.getGuilds().get(0);

        database = new Database();
        separateChannels = new SeparateChannels();
        productsCategory = new ProductsCategory();
        ticketsCategory = new TicketsCategory();
        utils = new Utils();

//        Обновить команды, только при изменении/добавлении команды
//        updateCommands();

        addEventListeners(jda);
    }

    /**
     * Получает файл конфигурации
     * @return Файл конфигурации
     * @throws IOException Ошибка чтения файла конфигурации
     */
    private Config getConfig() throws IOException {
        Gson gson = new Gson();
        File configFile = new File("config.json");

        Config config;
        if(!configFile.exists()) {
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

        return config;
    }

    /**
     * Обновляет команды
     */
    private void updateCommands() {
        CommandListUpdateAction commandsAction = guild.updateCommands();

        commandsAction = commandsAction.addCommands(new CommandData("auth", "Авторизация")
                .addOption(OptionType.STRING, "token", "Токен авторизации", true));

        commandsAction = commandsAction.addCommands(new CommandData("ban", "Блокировка пользователя")
                .addOption(OptionType.STRING, "username", "Ник пользователя", true)
                .addOption(OptionType.INTEGER, "time", "Время в минутах", true)
                .addOption(OptionType.STRING, "reason", "Причина блокировки", true));

        commandsAction = commandsAction.addCommands(new CommandData("ticket", "Создать новый тикет"));

        commandsAction = commandsAction.addCommands(new CommandData("verify", "Верифицировать тикет")
                .addOption(OptionType.STRING, "id", "Id тикета", true));

        commandsAction = commandsAction.addCommands(new CommandData("view", "Просмотр тикета")
                .addOption(OptionType.STRING, "id", "Id тикета", true));

        commandsAction = commandsAction.addCommands(new CommandData("update", "Обновить все тикеты продуктов"));

        commandsAction.queue();
    }

    /**
     * Добавляет слушатели событий
     * @param jda Объект для работы с Discord API
     */
    private void addEventListeners(JDA jda) {
        jda.addEventListener(new AuthCommandListener());
        jda.addEventListener(new BanCommandListener());
        jda.addEventListener(new TicketCommandListener());
        jda.addEventListener(new UpdateCommandListener());
        jda.addEventListener(new VerifyCommandListener());
        jda.addEventListener(new ViewCommandListener());

        jda.addEventListener(new MessageReceivedListener());
        jda.addEventListener(new UserUpdateOnlineStatusListener());
    }

    /**
     * Получает объект главного класса
     * @return Объект главного класса
     */
    public static CookieBot getInstance() {
        return instance;
    }
}
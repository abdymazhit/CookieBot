package net.Abdymazhit.CookieBot.channels;

import net.Abdymazhit.CookieBot.CookieBot;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.ExecutionException;

/**
 * Отвечает за создание канала авторизации
 *
 * @version   29.08.2021
 * @author    Islam Abdymazhit
 */
public class AuthChannel {

    /** Канал авторизации */
    private TextChannel channel;

    /**
     * Инициализирует канал авторизации
     */
    public AuthChannel() {
        deleteChannel();
        createChannel();
        sendChannelMessage();
    }

    /**
     * Удаляет канал
     */
    private void deleteChannel() {
        CookieBot.getInstance().jda.getGuilds().get(0).getTextChannelsByName("авторизация", true).get(0).delete().submit();
    }

    /**
     * Создает канал
     */
    private void createChannel() {
        try {
            channel = CookieBot.getInstance().jda.getGuilds().get(0).createTextChannel("авторизация").setPosition(0).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправляет сообщение канала
     */
    private void sendChannelMessage() {
        channel.sendMessage("Для защиты от ботов, рейдов и прочих неприятных для сервера вещей, у нас включена принудительная привязка к аккаунту VimeWorld.ru.\n" +
                "Ваш ник на сервере будет соответствовать нику в игре.\n" +
                "\n" +
                "После привязки вам станут доступны все текстовые и голосовые каналы, где вы уже сможете сообщить о багах.\n" +
                "\n" +
                "Как авторизоваться?\n" +
                "1. Получите ваш токен авторизации на сервере VimeWorld MiniGames. Для получения токена авторизации введите команду /api auth на сервере, Вы получите ссылку, которая понадобится в следующем шаге.\n" +
                "2. Введите здесь команду /auth token для привязки Вашего аккаунта VimeWorld.").submit();
    }
}
package net.Abdymazhit.CookieBot.minigames;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.ExecutionException;

/**
 * Представляет собой канал мини-игры
 *
 * @version   23.08.2021
 * @author    Islam Abdymazhit
 */
public class MiniGame {

    /** Название канала мини-игры */
    private final String channelName;

    /** Канал мини-игры */
    private TextChannel channel;

    /**
     * Инициализирует канал мини-игры
     * @param category Категория мини-игр
     * @param channelName Название канала мини-игры
     */
    public MiniGame(Category category, String channelName) {
        this.channelName = channelName;
        createChannel(category);
    }

    /**
     * Создает канал мини-игр
     * @param category Категория мини-игр
     */
    public void createChannel(Category category) {
        try {
            channel = category.createTextChannel(channelName).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправляет приветственное сообщение мини-игры
     * @param message Приветственное сообщение
     */
    public void sendWelcomeMessage(MessageEmbed message) {
        channel.sendMessageEmbeds(message).submit();
    }

    /**
     * Получает название канала мини-игры
     * @return Название канала мини-игры
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Получает канал мини-игры
     * @return Канал мини-игры
     */
    public TextChannel getChannel() {
        return channel;
    }
}
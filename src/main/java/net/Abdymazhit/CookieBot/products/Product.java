package net.Abdymazhit.CookieBot.products;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.ExecutionException;

/**
 * Представляет собой канал продукта
 *
 * @version   24.08.2021
 * @author    Islam Abdymazhit
 */
public class Product {

    /** Название канала продукта */
    private final String channelName;

    /** Канал продукта */
    private TextChannel channel;

    /**
     * Инициализирует канал продукта
     * @param category Категория продуктов
     * @param channelName Название канала продукта
     */
    public Product(Category category, String channelName) {
        this.channelName = channelName;
        createChannel(category);
    }

    /**
     * Создает канал продукта
     * @param category Категория продуктов
     */
    private void createChannel(Category category) {
        try {
            channel = category.createTextChannel(channelName).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправляет приветственное сообщение продукта
     * @param message Приветственное сообщение
     */
    public void sendWelcomeMessage(MessageEmbed message) {
        channel.sendMessageEmbeds(message).submit();
    }

    /**
     * Получает название канала продукта
     * @return Название канала продукта
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Получает канал продукта
     * @return Канал продукта
     */
    public TextChannel getChannel() {
        return channel;
    }
}
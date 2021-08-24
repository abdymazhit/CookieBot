package net.Abdymazhit.CookieBot.products.channels;

import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;

/**
 * Представляет собой канал продукта мини-игры Hide And Seek
 *
 * @version   24.08.2021
 * @author    Islam Abdymazhit
 */
public class HideAndSeek extends ProductChannel {

    /**
     * Инициализирует канал продукта мини-игры Hide And Seek
     * @param category Категория продуктов
     */
    public HideAndSeek(Category category) {
        super(category, "hide-and-seek");

        // Отправить приветственное сообщение
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Hide And Seek");
        embedBuilder.setColor(0xFF58B9FF);
        embedBuilder.addField("Описание",
                "В игре есть две роли: Искатели и Прячущиеся. Прячущиеся должны выжить, а искатели " +
                        "попытаться найти прячущихся и убить их",
                false);

        embedBuilder.addField("Обратите внимание",
                "Обязательно просматривайте список багов, прежде чем создавать тикет: вполне возможно " +
                        "баг уже описан ранее",
                false);

        embedBuilder.addField("Создание тикета",
                "Для создания тикета о баге введите команду **!ticket**",
                false);
        sendWelcomeMessage(embedBuilder.build());
        embedBuilder.clear();
    }
}
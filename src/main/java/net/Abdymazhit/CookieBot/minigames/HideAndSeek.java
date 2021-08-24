package net.Abdymazhit.CookieBot.minigames;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;

/**
 * Представляет собой канал мини-игры
 *
 * @version   23.08.2021
 * @author    Islam Abdymazhit
 */
public class HideAndSeek extends MiniGame {

    /**
     * Инициализирует канал мини-игры
     * @param category Категория мини-игр
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
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
        embedBuilder.setDescription(
                "В игре есть две роли: Искатели и Прячущиеся. \n" +
                        "Прячущиеся должны выжить, а искатели \n" +
                        "попытаться найти прячущихся и убить их. \n" +
                        "\n" +
                        "Для подачи отчета о баге напишите команду **!ticket**"
        );
        sendWelcomeMessage(embedBuilder.build());
        embedBuilder.clear();
    }
}
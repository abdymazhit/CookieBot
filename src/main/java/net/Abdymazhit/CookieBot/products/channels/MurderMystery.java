package net.Abdymazhit.CookieBot.products.channels;

import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;

/**
 * Представляет собой канал продукта мини-игры Murder Mystery
 *
 * @version   02.09.2021
 * @author    Islam Abdymazhit
 */
public class MurderMystery extends ProductChannel {

    /**
     * Инициализирует канал продукта мини-игры Murder Mystery
     * @param category Категория продуктов
     */
    public MurderMystery(Category category) {
        super(category, "murder-mystery");

        // Отправить приветственное сообщение
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Murder Mystery");
        embedBuilder.setColor(0xFF58B9FF);
        embedBuilder.addField("Описание",
                "Знаменитая игра, где обычным смертным нужно выяснить кто же на самом деле маньяк. " +
                        "Маньяк должен убить всех игроков и при этом остаться в живых.",
                false);

        embedBuilder.addField("Обратите внимание",
                "Обязательно просматривайте список багов, прежде чем создавать тикет: вполне возможно " +
                        "баг уже описан ранее",
                false);

        embedBuilder.addField("Создание тикета",
                "Для создания тикета о баге введите команду `/ticket`",
                false);
        sendChannelMessage(embedBuilder.build());
        embedBuilder.clear();
    }
}
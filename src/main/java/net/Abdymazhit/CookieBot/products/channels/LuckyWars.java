package net.Abdymazhit.CookieBot.products.channels;

import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;

/**
 * Представляет собой канал продукта мини-игры Lucky Wars
 *
 * @version   02.09.2021
 * @author    Islam Abdymazhit
 */
public class LuckyWars extends ProductChannel {

    /**
     * Инициализирует канал продукта мини-игры Lucky Wars
     * @param category Категория продуктов
     */
    public LuckyWars(Category category) {
        super(category, "lucky-wars");

        // Отправить приветственное сообщение
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Lucky Wars");
        embedBuilder.setColor(0xFF58B9FF);
        embedBuilder.addField("Описание",
                "Игра похожа на SkyWars, только вместо сундуков по карте разбросаны лаки блоки. При ломании " +
                        "можете получить крутой предмет или оказаться в ловушке. ???? - ??! ?????? ?? ???????????.",
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
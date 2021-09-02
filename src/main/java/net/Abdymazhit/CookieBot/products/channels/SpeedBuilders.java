package net.Abdymazhit.CookieBot.products.channels;

import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;

/**
 * Представляет собой канал продукта мини-игры Speed Builders
 *
 * @version   02.09.2021
 * @author    Islam Abdymazhit
 */
public class SpeedBuilders extends ProductChannel {

    /**
     * Инициализирует канал продукта мини-игры Speed Builders
     * @param category Категория продуктов
     */
    public SpeedBuilders(Category category) {
        super(category, "speed-builders");

        // Отправить приветственное сообщение
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Speed Builders");
        embedBuilder.setColor(0xFF58B9FF);
        embedBuilder.addField("Описание",
                "В этом режиме вы должны запомнить постройку за считанные секунды, а затем быть первым, " +
                        "кто её завершит! Тот, чья постройка меньше всех похожа на оригинал, выбывает из игры :(",
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
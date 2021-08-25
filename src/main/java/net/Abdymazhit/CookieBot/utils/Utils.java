package net.Abdymazhit.CookieBot.utils;

import net.Abdymazhit.CookieBot.customs.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Представляет собой инструменты для упрощения работы
 *
 * @version   25.08.2021
 * @author    Islam Abdymazhit
 */
public class Utils {

    /** Объект, отвечает за создание информационных сообщений */
    private final EmbedBuilder embedBuilder;

    /**
     * Инициализирует инструменты
     */
    public Utils() {
        embedBuilder = new EmbedBuilder();
    }

    /**
     * Создает информационное сообщение тикета
     * @param ticket Тикет
     * @param title Заговолок
     * @return Информационное сообщение тикета
     */
    public MessageEmbed getTicketMessageEmbed(Ticket ticket, String title) {
        embedBuilder.setTitle(title);
        embedBuilder.setColor(0xFF58B9FF);
        embedBuilder.addField("Создатель", ticket.getCreatorId(), false);
        embedBuilder.addField("Продукт", ticket.getProductName(), true);
        embedBuilder.addField("Приоритет", ticket.getPriority().getName(), true);
        embedBuilder.addField("Заговолок", ticket.getTitle(), false);
        embedBuilder.addField("Описание проблемы", ticket.getDescription(), false);
        embedBuilder.addField("Шаги для воспроизведения проблемы", ticket.getSteps(), false);
        embedBuilder.addField("Что происходит в результате", ticket.getResult(), false);
        embedBuilder.addField("Что должно происходить", ticket.getShouldBe(), false);
        embedBuilder.addField("Приложенные материалы", ticket.getMaterials(), false);
        embedBuilder.setTimestamp(LocalDateTime.ofInstant(ticket.getCreatedOn().toInstant(), ZoneId.of("Europe/Moscow")));

        MessageEmbed messageEmbed = embedBuilder.build();

        embedBuilder.clear();

        return messageEmbed;
    }

    /**
     * Получает числа из сообщения
     * @param message Сообщение
     * @return Числа
     */
    public int getIntByMessage(Message message) {
        if(message != null) {
            String title = message.getEmbeds().get(0).getTitle();

            if(title != null) {
                return Integer.parseInt(title.replaceAll("[\\D]", ""));
            }
        }
        return -1;
    }
}
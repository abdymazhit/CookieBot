package net.Abdymazhit.CookieBot.utils;

import net.Abdymazhit.CookieBot.customs.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Представляет собой инструменты для упрощения работы
 *
 * @version   01.09.2021
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
        embedBuilder.addField("Создатель", ticket.getCreator(), false);
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
     * Отправляет GET запрос по URL
     * @param url URL
     * @return Результат запроса в типе String
     */
    public String sendGetRequest(String url) {
        HttpGet request = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
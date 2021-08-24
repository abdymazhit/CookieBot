package net.Abdymazhit.CookieBot.products;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.enums.Priority;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Представляет собой канал продукта
 *
 * @version   24.08.2021
 * @author    Islam Abdymazhit
 */
public class ProductChannel {

    /** Канал продукта */
    private TextChannel channel;

    /** Приветственное сообщение канала продукта */
    private Message welcomeMessage;

    /** Сообщение о тикетах продукта */
    private Message ticketsMessage;

    /**
     * Инициализирует канал продукта
     * @param category Категория продуктов
     * @param channelName Название канала продукта
     */
    public ProductChannel(Category category, String channelName) {
        createChannel(category, channelName);
    }

    /**
     * Создает канал продукта
     * @param category Категория продуктов
     * @param channelName Название канала продукта
     */
    private void createChannel(Category category, String channelName) {
        try {
            channel = category.createTextChannel(channelName).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправляет приветственное сообщение продукта
     * @param messageEmbed Приветственное сообщение
     */
    public void sendWelcomeMessage(MessageEmbed messageEmbed) {
        try {
            welcomeMessage = channel.sendMessageEmbeds(messageEmbed).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обновляет все тикеты продукта
     */
    public void updateTickets() {
        List<Ticket> tickets = CookieBot.database.getProductTickets(channel.getName());

        StringBuilder trivialTickets = new StringBuilder();
        StringBuilder minorTickets = new StringBuilder();
        StringBuilder majorTickets = new StringBuilder();
        StringBuilder criticalTickets = new StringBuilder();
        StringBuilder blockerTickets = new StringBuilder();

        for(Ticket ticket : tickets) {
            if(ticket.getPriority().equals(Priority.TRIVIAL)) {
                trivialTickets.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            } else if(ticket.getPriority().equals(Priority.MINOR)) {
                minorTickets.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            } else if(ticket.getPriority().equals(Priority.MAJOR)) {
                majorTickets.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            } else if(ticket.getPriority().equals(Priority.CRITICAL)) {
                criticalTickets.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            } else if(ticket.getPriority().equals(Priority.BLOCKER)) {
                blockerTickets.append("`[").append(ticket.getId()).append("] ").append(ticket.getTitle())
                        .append(" [").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ticket.getCreatedOn())).append("]`\n");
            }
        }

        if(ticketsMessage != null) {
            channel.deleteMessageById(ticketsMessage.getId()).submit();
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Известные баги");
        embedBuilder.setColor(0xFF58B9FF);
        embedBuilder.addField("Тривиальные", trivialTickets.toString(), false);
        embedBuilder.addField("Незначительные", minorTickets.toString(), false);
        embedBuilder.addField("Значительные", majorTickets.toString(), false);
        embedBuilder.addField("Критические", criticalTickets.toString(), false);
        embedBuilder.addField("Блокирующие", blockerTickets.toString(), false);

        embedBuilder.addField("Управление тикетом",
                "Для управления тикетом введите команду **/manage <id>**",
                false);

        try {
            ticketsMessage = channel.sendMessageEmbeds(embedBuilder.build()).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        embedBuilder.clear();
    }

    /**
     * Получает приветственное сообщение канала продукта
     * @return Приветственное сообщение канала продукта
     */
    public Message getWelcomeMessage() {
        return welcomeMessage;
    }

    /**
     * Получает сообщение о тикетах продукта
     * @return Сообщение о тикетах продукта
     */
    public Message getTicketsMessage() {
        return ticketsMessage;
    }

    /**
     * Получает канал продукта
     * @return Канал продукта
     */
    public TextChannel getChannel() {
        return channel;
    }
}
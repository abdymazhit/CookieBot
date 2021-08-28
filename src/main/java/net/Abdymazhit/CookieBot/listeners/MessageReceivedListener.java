package net.Abdymazhit.CookieBot.listeners;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.Abdymazhit.CookieBot.tickets.TicketChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Обработчик событий получения сообщений
 *
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class MessageReceivedListener extends ListenerAdapter {

    /**
     * Событие получения сообщений
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel messageChannel = event.getChannel();

        // Проверка сообщения на команду
        if(event.isWebhookMessage()) {
            // Проверка команды на ошибку
            if(message.getEmbeds().isEmpty()) {
                message.delete().submitAfter(5, TimeUnit.SECONDS);
            } else {
                // Удалить команду через 5 минут, если взаимодействия не было
                message.delete().submitAfter(5, TimeUnit.MINUTES);
            }
        } else {
            // Проверка, является ли канал каналом продукта
            for(ProductChannel productChannel : CookieBot.getInstance().productsCategory.getProductChannels()) {
                if(productChannel.getChannel().equals(messageChannel)) {
                    // Если сообщение не является сообщением продукта, удалить сообщение через 3 секунды
                    if(!message.equals(productChannel.getWelcomeMessage()) && !message.equals(productChannel.getTicketsMessage())) {
                        message.delete().submitAfter(3, TimeUnit.SECONDS);
                    }

                    break;
                }
            }

            // Проверка, является ли канал каналом тикета
            for(TicketChannel ticketChannel : CookieBot.getInstance().ticketsCategory.getTickets()) {
                if(ticketChannel.getChannel().equals(messageChannel)) {
                    // Проверка автора сообщений на бота
                    if(!event.getAuthor().isBot()) {
                        ticketChannel.onMessageReceived(message.getContentRaw());
                    }

                    break;
                }
            }
        }
    }
}
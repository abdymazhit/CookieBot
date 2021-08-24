package net.Abdymazhit.CookieBot;

import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.Abdymazhit.CookieBot.tickets.TicketChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Представляет собой слушатель событий
 *
 * @version   24.08.2021
 * @author    Islam Abdymazhit
 */
public class EventsListener extends ListenerAdapter {

    /**
     * Событие получения сообщений
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel messageChannel = event.getChannel();

        // Проверка, является ли канал каналом продукта
        for(ProductChannel productChannel : CookieBot.productsCategory.getProductChannels()) {
            if(productChannel.getChannel().equals(messageChannel)) {
                // Если сообщение не является сообщением продукта, удалить сообщение через 3 секунды
                if(!message.equals(productChannel.getWelcomeMessage()) && !message.equals(productChannel.getTicketsMessage())) {
                    message.delete().submitAfter(3, TimeUnit.SECONDS);
                }

                // Проверка на команду !ticket
                if(message.getContentRaw().equals("!ticket")) {
                    // Создать новый тикет
                    messageChannel.sendMessage("Создание тикета...").delay(3, TimeUnit.SECONDS).flatMap(Message::delete).submit();
                    CookieBot.ticketsCategory.createTicket(productChannel.getChannel().getName(), event.getMember());
                }

                break;
            }
        }

        // Проверка, является ли канал каналом тикета
        for(TicketChannel ticketChannel : CookieBot.ticketsCategory.getTickets()) {
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
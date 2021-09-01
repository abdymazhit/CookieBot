package net.Abdymazhit.CookieBot.listeners;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.TicketChannel;
import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Обработчик событий получения сообщений
 *
 * @version   01.09.2021
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

        if(messageChannel.equals(CookieBot.getInstance().separateChannels.getAuthChannel().channel)) {
            if(!event.getAuthor().isBot()) {
                message.delete().queue();
                return;
            }
        } else if(messageChannel.equals(CookieBot.getInstance().separateChannels.getVerificationChannel().channel)) {
            if(!event.getAuthor().isBot()) {
                message.delete().queue();
                return;
            }
        }

        // Проверка, является ли канал каналом продукта
        for(ProductChannel productChannel : CookieBot.getInstance().productsCategory.getProductChannels()) {
            if(productChannel.channel.equals(messageChannel)) {
                if(!event.getAuthor().isBot()) {
                    message.delete().queue();
                    return;
                }
            }
        }

        // Проверка, является ли канал каналом тикета
        for(TicketChannel ticketChannel : CookieBot.getInstance().ticketsCategory.getTicketsChannels()) {
            if(ticketChannel.channel.equals(messageChannel)) {
                // Проверка автора сообщений на бота
                if(!event.getAuthor().isBot()) {
                    ticketChannel.onMessageReceived(message.getContentRaw());
                    return;
                }
            }
        }
    }
}
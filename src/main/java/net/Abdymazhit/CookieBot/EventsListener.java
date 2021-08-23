package net.Abdymazhit.CookieBot;

import net.Abdymazhit.CookieBot.minigames.MiniGame;
import net.Abdymazhit.CookieBot.tickets.Ticket;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Представляет собой слушатель событий
 *
 * @version   23.08.2021
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

        // Проверка, является ли канал каналом мини-игры
        for(MiniGame miniGame : CookieBot.miniGames.getMiniGames()) {
            if(miniGame.getChannel().equals(messageChannel)) {
                // Удалить сообщение через 3 секунды, так как каналы мини-игры должны быть всегда пусты
                message.delete().submitAfter(3, TimeUnit.SECONDS);

                // Проверка на команду !ticket
                if(message.getContentRaw().equals("!ticket")) {
                    // Создать новый тикет
                    messageChannel.sendMessage("Создание тикета...").delay(3, TimeUnit.SECONDS).flatMap(Message::delete).submit();
                    CookieBot.tickets.createTicket(miniGame.getChannelName(), event.getMember());
                }

                break;
            }
        }

        // Проверка, является ли канал каналом тикета
        for(Ticket ticket : CookieBot.tickets.getTickets()) {
            if(ticket.getChannel().equals(messageChannel)) {
                // Проверка автора сообщений на бота
                if(!event.getAuthor().isBot()) {
                    ticket.onMessageReceived(message.getContentRaw());
                }

                break;
            }
        }
    }
}
package net.Abdymazhit.CookieBot.listeners.commands;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Команда создания тикета
 *
 * @version   01.09.2021
 * @author    Islam Abdymazhit
 */
public class TicketCommandListener extends ListenerAdapter {

    /**
     * Событие отправки команды
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        MessageChannel messageChannel = event.getChannel();
        Member member = event.getMember();

        if(!event.getName().equals("ticket")) return;
        if(member == null) return;

        // Проверка, является ли канал каналом продукта
        for(ProductChannel productChannel : CookieBot.getInstance().productsCategory.getProductChannels()) {
            if(productChannel.channel.equals(messageChannel)) {
                Ticket ticket = new Ticket();
                ticket.setProductName(messageChannel.getName());

                // Создать канал для создания тикета
                CookieBot.getInstance().ticketsCategory.createTicketCreationChannel(event, ticket, member);
                return;
            }
        }
    }
}
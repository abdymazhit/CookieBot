package net.Abdymazhit.CookieBot.listeners.commands;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Команда просмотра тикета
 *
 * @version   01.09.2021
 * @author    Islam Abdymazhit
 */
public class ViewCommandListener extends ListenerAdapter {

    /**
     * Событие отправки команды
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        MessageChannel messageChannel = event.getChannel();
        Member member = event.getMember();

        if(!event.getName().equals("view")) return;
        if(member == null) return;

        OptionMapping idOption = event.getOption("id");
        if(idOption == null) {
            event.reply("Ошибка! Неверный id тикета!").queue();
            return;
        }

        Ticket ticket = CookieBot.getInstance().database.getTicket("SELECT * FROM tickets WHERE id = (SELECT ticket_id " +
                "FROM verified_tickets WHERE ticket_id = " + Integer.parseInt(idOption.getAsString()) + ");");
        if(ticket == null) {
            event.reply("Ошибка! Тикет не найден!").queue();
            return;
        }

        // Проверка, является ли канал каналом продукта
        for(ProductChannel productChannel : CookieBot.getInstance().productsCategory.getProductChannels()) {
            if(productChannel.channel.equals(messageChannel)) {
                // Создать канал для просмотра тикета
                CookieBot.getInstance().ticketsCategory.createTicketViewingChannel(event, ticket, member);
                return;
            }
        }
    }
}
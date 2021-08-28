package net.Abdymazhit.CookieBot.listeners.commands;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.concurrent.TimeUnit;

/**
 * Команда создания тикета
 *
 * @version   28.08.2021
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
            if(productChannel.getChannel().equals(messageChannel)) {
                // Создать новый тикет
                event.reply("Создание тикета...").delay(3, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).submit();
                CookieBot.getInstance().ticketsCategory.createTicket(productChannel.getChannel().getName(), event.getMember());

                break;
            }
        }
    }
}
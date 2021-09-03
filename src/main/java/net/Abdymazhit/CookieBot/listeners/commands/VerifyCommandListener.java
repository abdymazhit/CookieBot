package net.Abdymazhit.CookieBot.listeners.commands;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.enums.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Команда верификации тикета
 *
 * @version   03.09.2021
 * @author    Islam Abdymazhit
 */
public class VerifyCommandListener extends ListenerAdapter {

    /**
     * Событие отправки команды
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        MessageChannel messageChannel = event.getChannel();
        Member member = event.getMember();

        if(!event.getName().equals("verify")) return;
        if(!messageChannel.equals(CookieBot.getInstance().separateChannels.getVerificationChannel().channel)) return;
        if(member == null) return;

        OptionMapping idOption = event.getOption("id");
        if(idOption == null) {
            event.reply("Ошибка! Неверный id ожидающего верификации тикета!").queue();
            return;
        }

        if(!member.getRoles().contains(Rank.MODER.getRole()) &&
                !member.getRoles().contains(Rank.WARDEN.getRole()) &&
                !member.getRoles().contains(Rank.CHIEF.getRole()) &&
                !member.getRoles().contains(Rank.ADMIN.getRole()) &&
                !member.getRoles().contains(Rank.OWNER.getRole()) &&
                !member.getRoles().contains(Rank.MODER_DISCORD.getRole())) {
            event.reply("У вас нет прав для этого действия!").queue();
            return;
        }

        Ticket ticket = CookieBot.getInstance().database.getTicket("SELECT * FROM tickets WHERE id = " +
                "(SELECT ticket_id FROM pending_verification_tickets WHERE ticket_id = " + Integer.parseInt(idOption.getAsString()) + ");");
        if(ticket == null) {
            event.reply("Ошибка! Ожидающий верификации тикет не найден!").queue();
            return;
        }

        // Создать канал для верификации тикета
        CookieBot.getInstance().ticketsCategory.createTicketVerificationChannel(event, ticket, member);
    }
}
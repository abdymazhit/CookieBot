package net.Abdymazhit.CookieBot.listeners.commands;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.enums.Rank;
import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Команда обновления тикетов продуктов
 *
 * @version   01.09.2021
 * @author    Islam Abdymazhit
 */
public class UpdateCommandListener extends ListenerAdapter {

    /**
     * Событие отправки команды
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        MessageChannel messageChannel = event.getChannel();
        Member member = event.getMember();

        if(!event.getName().equals("update")) return;
        if(member == null) return;

        // Проверка, является ли канал каналом продукта
        for(ProductChannel productChannel : CookieBot.getInstance().productsCategory.getProductChannels()) {
            if(productChannel.channel.equals(messageChannel)) {
                if(member.getRoles().contains(Rank.OWNER.getRole())) {
                    // Обновить список тикетов
                    CookieBot.getInstance().productsCategory.updateProductsAvailableTicketsList();
                    CookieBot.getInstance().separateChannels.getVerificationChannel().updatePendingVerificationTicketsList();

                    event.reply("Все списки тикетов обновлены!").setEphemeral(true).queue();
                } else {
                    event.reply("У вас нет прав для этого действия!").setEphemeral(true).queue();
                }
                return;
            }
        }
    }
}
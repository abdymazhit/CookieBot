package net.Abdymazhit.CookieBot.listeners.buttons;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.concurrent.TimeUnit;

/**
 * Кнопка отмены
 *
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class CancelButtonListener extends ListenerAdapter {

    /**
     * Событие клика кнопки
     */
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        Message message = event.getMessage();
        Member member = event.getMember();

        if(!event.getComponentId().equals("cancel")) return;
        if(message == null) return;
        if(member == null) return;

        message.delete().submitAfter(3, TimeUnit.SECONDS);
        event.reply("Команда отменяется...").delay(3, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).submit();
    }
}
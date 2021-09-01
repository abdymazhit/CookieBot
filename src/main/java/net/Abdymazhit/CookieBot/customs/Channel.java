package net.Abdymazhit.CookieBot.customs;

import net.Abdymazhit.CookieBot.CookieBot;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.ExecutionException;

/**
 * Представляет собой канал
 *
 * @version   01.09.2021
 * @author    Islam Abdymazhit
 */
public class Channel {

    /** Канал */
    public TextChannel channel;

    /**
     * Удаляет канал
     * @param categoryName Название категории
     * @param channelName Название канала
     */
    public void deleteChannel(String categoryName, String channelName) {
        Category category = CookieBot.getInstance().guild.getCategoriesByName(categoryName, true).get(0);
        for(TextChannel textChannel : category.getTextChannels()) {
            if(textChannel.getName().equals(channelName)) {
                textChannel.delete().queue();
                return;
            }
        }
    }

    /**
     * Удаляет канал
     * @param channelName Название канала
     */
    public void deleteChannel(String channelName) {
        CookieBot.getInstance().guild.getTextChannelsByName(channelName, true).get(0).delete().queue();
    }

    /**
     * Создает канал
     * @param categoryName Название категории
     * @param channelName Название канала
     */
    public void createChannel(String categoryName, String channelName) {
        Category category = CookieBot.getInstance().guild.getCategoriesByName(categoryName, true).get(0);
        try {
            channel = category.createTextChannel(channelName).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает канал
     * @param channelName Название канала
     */
    public void createChannel(String channelName) {
        try {
            channel = CookieBot.getInstance().guild.createTextChannel(channelName).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
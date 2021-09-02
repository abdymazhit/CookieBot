package net.Abdymazhit.CookieBot.customs;

import net.Abdymazhit.CookieBot.CookieBot;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Представляет собой канал
 *
 * @version   02.09.2021
 * @author    Islam Abdymazhit
 */
public class Channel {

    /** Канал */
    public TextChannel channel;

    /**
     * Удаляет канал
     * @param category Категория
     * @param channelName Название канала
     */
    public void deleteChannel(Category category, String channelName) {
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
        List<TextChannel> textChannels = CookieBot.getInstance().guild.getTextChannelsByName(channelName, true);
        if(!textChannels.isEmpty()) {
            textChannels.get(0).delete().queue();
        }
    }

    /**
     * Создает канал
     * @param category Категория
     * @param channelName Название канала
     * @param position Позиция канала
     */
    public void createChannel(Category category, String channelName, @Nullable Integer position) {
        try {
            channel = category.createTextChannel(channelName).setPosition(position).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает канал
     * @param channelName Название канала
     * @param position Позиция канала
     */
    public void createChannel(String channelName, @Nullable Integer position) {
        try {
            channel = CookieBot.getInstance().guild.createTextChannel(channelName).setPosition(position).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
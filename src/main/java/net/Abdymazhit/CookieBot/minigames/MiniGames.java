package net.Abdymazhit.CookieBot.minigames;

import net.Abdymazhit.CookieBot.CookieBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Представляет собой категорию мини-игр
 *
 * @version   23.08.2021
 * @author    Islam Abdymazhit
 */
public class MiniGames {

    /** Категория мини-игр */
    private Category category;

    /** Список мини-игр */
    private List<MiniGame> miniGames;

    /**
     * Инициализирует категорию мини-игр
     */
    public MiniGames() {
        deleteCategory();
        createCategory();
        createMiniGames();
    }

    /**
     * Удаляет категорию мини-игр
     */
    private void deleteCategory() {
        for(Category category : CookieBot.jda.getCategories()) {
            if(category.getName().equals("Мини-игры")) {
                for(TextChannel textChannel : category.getTextChannels()) {
                    textChannel.delete().submit();
                }
                category.delete().submit();
            }
        }
    }

    /**
     * Создает категорию мини-игр
     */
    private void createCategory() {
        try {
            category = CookieBot.jda.getGuilds().get(0).createCategory("Мини-игры")
                    .addPermissionOverride(CookieBot.jda.getRolesByName("Тестер", true).get(0), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(CookieBot.jda.getGuilds().get(0).getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает список мини-игр
     */
    private void createMiniGames() {
        miniGames = new ArrayList<>();
        miniGames.add(new HideAndSeek(category));
    }

    /**
     * Получает список мини-игр
     * @return Список мини-игр
     */
    public List<MiniGame> getMiniGames() {
        return miniGames;
    }
}
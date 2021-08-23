package net.Abdymazhit.CookieBot.tickets;

import net.Abdymazhit.CookieBot.CookieBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Представляет собой категорию тикетов
 *
 * @version   23.08.2021
 * @author    Islam Abdymazhit
 */
public class Tickets {

    /** Категория тикетов */
    private Category category;

    /** Список тикетов */
    private final List<Ticket> tickets;

    /**
     * Инициализирует категорию тикетов
     */
    public Tickets() {
        deleteCategory();
        createCategory();
        tickets = new ArrayList<>();
    }

    /**
     * Удаляет категорию тикетов
     */
    private void deleteCategory() {
        for(Category category : CookieBot.jda.getCategories()) {
            if(category.getName().equals("Тикеты")) {
                for(TextChannel textChannel : category.getTextChannels()) {
                    textChannel.delete().submit();
                }
                category.delete().submit();
            }
        }
    }

    /**
     * Создает категорию тикетов
     */
    private void createCategory() {
        try {
            category = CookieBot.jda.getGuilds().get(0).createCategory("Тикеты")
                    .addPermissionOverride(CookieBot.jda.getGuilds().get(0).getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает новый тикет
     * @param miniGame Название мини-игры
     * @param member Тестер
     */
    public void createTicket(String miniGame, Member member) {
        tickets.add(new Ticket(miniGame, tickets.size(), member));
    }

    /**
     * Получает категорию тикетов
     * @return Категория тикетов
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Получает список тикетов
     * @return Список тикетов
     */
    public List<Ticket> getTickets() {
        return tickets;
    }
}
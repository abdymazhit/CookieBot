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
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class TicketsCategory {

    /** Категория тикетов */
    private Category category;

    /** Список каналов тикетов */
    private final List<TicketChannel> ticketChannels;

    /** Последний id тикета */
    private int lastId;

    /**
     * Инициализирует категорию тикетов
     */
    public TicketsCategory() {
        deleteCategory();
        createCategory();
        ticketChannels = new ArrayList<>();
        lastId = 0;
    }

    /**
     * Удаляет категорию тикетов
     */
    private void deleteCategory() {
        for(Category category : CookieBot.getInstance().jda.getCategories()) {
            if(category.getName().equals("Тикеты")) {
                for(TextChannel textChannel : category.getTextChannels()) {
                    textChannel.delete().submit();
                }
                category.delete().submit();

                break;
            }
        }
    }

    /**
     * Создает категорию тикетов
     */
    private void createCategory() {
        try {
            category = CookieBot.getInstance().jda.getGuilds().get(0).createCategory("Тикеты")
                    .addPermissionOverride(CookieBot.getInstance().jda.getGuilds().get(0).getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает новый тикет
     * @param productName Название продукта
     * @param member Пользователь
     */
    public void createTicket(String productName, Member member) {
        ticketChannels.add(new TicketChannel(productName, lastId, member));
        lastId++;
    }

    /**
     * Удаляет канал тикета из списка каналов тикетов
     * @param ticketChannel Канал тикета
     */
    public void removeTicket(TicketChannel ticketChannel) {
        ticketChannels.remove(ticketChannel);
    }

    /**
     * Получает список каналов тикетов
     * @return Список каналов тикетов
     */
    public List<TicketChannel> getTickets() {
        return ticketChannels;
    }

    /**
     * Получает категорию тикетов
     * @return Категория тикетов
     */
    public Category getCategory() {
        return category;
    }
}
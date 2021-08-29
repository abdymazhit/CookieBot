package net.Abdymazhit.CookieBot.tickets;

import net.Abdymazhit.CookieBot.CookieBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Представляет собой категорию тикетов
 *
 * @version   29.08.2021
 * @author    Islam Abdymazhit
 */
public class TicketsCategory {

    /** Категория тикетов */
    private Category category;

    /** Список каналов тикетов */
    private final List<TicketChannel> ticketChannels;

    /** Текущие создатели тикетов */
    private final List<Member> currentTicketCreators;

    /** Последний id тикета */
    private int lastId;

    /**
     * Инициализирует категорию тикетов
     */
    public TicketsCategory() {
        deleteCategory();
        createCategory();
        ticketChannels = new ArrayList<>();
        currentTicketCreators = new ArrayList<>();
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
     * @param event Событие отправки команды
     * @param productName Название продукта
     * @param member Пользователь
     */
    public void createTicket(SlashCommandEvent event, String productName, Member member) {
        if(!currentTicketCreators.contains(member)) {
            event.reply("Создание тикета...").delay(3, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).submit();
            ticketChannels.add(new TicketChannel(productName, lastId, member));
            currentTicketCreators.add(member);
            lastId++;
        } else {
            event.reply("Вы уже создаете тикет!").delay(3, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).submit();
        }
    }

    /**
     * Удаляет канал тикета из списка каналов тикетов
     * @param ticketChannel Канал тикета
     */
    public void removeTicket(TicketChannel ticketChannel) {
        ticketChannels.remove(ticketChannel);
        currentTicketCreators.remove(ticketChannel.getMember());
    }

    /**
     * Получает список каналов тикетов
     * @return Список каналов тикетов
     */
    public List<TicketChannel> getTicketsChannels() {
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
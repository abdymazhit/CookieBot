package net.Abdymazhit.CookieBot.tickets;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.customs.TicketChannel;
import net.Abdymazhit.CookieBot.seperateChannels.VerificationChannel;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Представляет собой категорию тикетов
 *
 * @version   02.09.2021
 * @author    Islam Abdymazhit
 */
public class TicketsCategory {

    /** Категория тикетов */
    private Category category;

    /** Список каналов тикетов */
    private final List<TicketChannel> ticketChannels;

    /** Последний id тикета */
    private int lastId;

    /** Хранит канал тикета по id пользователя */
    private final Map<String, TicketChannel> ticketChannelMap;

    /** Хранит канал для просмотра тикета по id тикета */
    private final Map<TicketViewingChannel, Integer> ticketViewingChannelMap;

    /**
     * Инициализирует категорию тикетов
     */
    public TicketsCategory() {
        deleteCategory();
        createCategory();
        ticketChannels = new ArrayList<>();
        lastId = 0;
        ticketChannelMap = new HashMap<>();
        ticketViewingChannelMap = new HashMap<>();
    }

    /**
     * Удаляет категорию тикетов
     */
    private void deleteCategory() {
        List<Category> categories = CookieBot.getInstance().guild.getCategoriesByName("тикеты", true);
        if(!categories.isEmpty()) {
            Category category = categories.get(0);
            for(TextChannel textChannel : category.getTextChannels()) {
                textChannel.delete().queue();
            }
            category.delete().queue();
        }
    }

    /**
     * Создает категорию тикетов
     */
    private void createCategory() {
        try {
            category = CookieBot.getInstance().guild.createCategory("тикеты")
                    .addPermissionOverride(CookieBot.getInstance().guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает канал для создания тикета
     * @param event Вызвавшее событие
     * @param ticket Тикет
     * @param member Пользователь
     */
    public void createTicketCreationChannel(SlashCommandEvent event, Ticket ticket, Member member) {
        if(!ticketChannelMap.containsKey(member.getId())) {
            TicketCreationChannel ticketCreationChannel = new TicketCreationChannel(lastId, ticket, member);
            ticketChannels.add(ticketCreationChannel);
            event.reply("Канал для создания тикета успешно создан! Номер канала тикета: " + lastId + ". Переместитесь в категорию `Тикеты`.")
                    .delay(10, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).queue();

            lastId++;
            ticketChannelMap.put(member.getId(), ticketCreationChannel);
        } else {
            event.reply("У вас уже имеется открытый канал тикета! Переместитесь в категорию `Тикеты`.")
                    .delay(10, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).queue();
        }
    }

    /**
     * Создает канал для верификации тикета
     * @param event Вызвавшее событие
     * @param ticket Тикет
     * @param member Пользователь
     */
    public void createTicketVerificationChannel(SlashCommandEvent event, Ticket ticket, Member member) {
        List<Integer> ticketsInVerification = CookieBot.getInstance().separateChannels.getVerificationChannel().getTicketsInVerification();
        if(!ticketsInVerification.contains(ticket.getId())) {
            if(!ticketChannelMap.containsKey(member.getId())) {
                TicketVerificationChannel ticketVerificationChannel = new TicketVerificationChannel(lastId, ticket, member);
                ticketChannels.add(ticketVerificationChannel);
                event.reply("Канал для верификации тикета успешно создан! Номер канала тикета: " + lastId + ". Переместитесь в категорию `Тикеты`.")
                        .delay(10, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).queue();

                lastId++;
                ticketChannelMap.put(member.getId(), ticketVerificationChannel);
                ticketsInVerification.add(ticket.getId());
                CookieBot.getInstance().separateChannels.getVerificationChannel().updatePendingVerificationTicketsList();
            } else {
                event.reply("У вас уже имеется открытый канал тикета! Переместитесь в категорию `Тикеты`.")
                        .delay(10, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).queue();
            }
        } else {
            event.reply("Ваш коллега уже занимается верификацией этого тикета!")
                    .delay(10, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).queue();
        }
    }

    /**
     * Создает канал для просмотра тикета
     * @param event Вызвавшее событие
     * @param ticket Тикет
     * @param member Пользователь
     */
    public void createTicketViewingChannel(SlashCommandEvent event, Ticket ticket, Member member) {
        if(!ticketChannelMap.containsKey(member.getId())) {
            TicketViewingChannel ticketViewingChannel = new TicketViewingChannel(lastId, ticket, member);

            ticketChannels.add(ticketViewingChannel);
            event.reply("Канал для просмотра тикета успешно создан! Номер канала тикета: " + lastId + ". Переместитесь в категорию `Тикеты`.")
                    .delay(10, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).queue();

            lastId++;
            ticketChannelMap.put(member.getId(), ticketViewingChannel);
            ticketViewingChannelMap.put(ticketViewingChannel, ticket.getId());
        } else {
            event.reply("У вас уже имеется открытый канал тикета! Переместитесь в категорию `Тикеты`.")
                    .delay(10, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).queue();
        }
    }

    /**
     * Удаляет канал тикета
     * @param ticketChannel Канал тикета
     * @param member Пользователь
     */
    public void removeTicketChannel(TicketChannel ticketChannel, Member member) {
        ticketChannels.remove(ticketChannel);
        ticketChannelMap.remove(member.getId());

        if(ticketChannel instanceof TicketVerificationChannel) {
            VerificationChannel verificationChannel = CookieBot.getInstance().separateChannels.getVerificationChannel();
            verificationChannel.getTicketsInVerification().remove(Integer.valueOf(ticketChannel.ticket.getId()));
            verificationChannel.updatePendingVerificationTicketsList();
        } else if(ticketChannel instanceof TicketViewingChannel) {
            ticketViewingChannelMap.remove(ticketChannel);
        }
    }

    /**
     * Удаляет другие каналы для просмотра тикета
     * @param currentTicketViewingChannel Текущий канал просмотра тикета
     */
    public void deleteTicketViewingChannel(TicketViewingChannel currentTicketViewingChannel) {
        for(TicketViewingChannel ticketViewingChannel : ticketViewingChannelMap.keySet()) {
            if(!ticketViewingChannel.equals(currentTicketViewingChannel)) {
                if(ticketViewingChannel.ticket.getId() == currentTicketViewingChannel.ticket.getId()) {
                    ticketViewingChannel.channel.sendMessage("Канал удаляется! Тикет был удален или исправлен администрацией!").submit();
                    ticketViewingChannel.deleteChannel();
                    ticketViewingChannelMap.remove(ticketViewingChannel);
                }
            }
        }
    }

    /**
     * Получает канал тикета
     * @param memberId Id пользователя
     * @return Канал тикета
     */
    public TicketChannel getTicketChannel(String memberId) {
        return ticketChannelMap.get(memberId);
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
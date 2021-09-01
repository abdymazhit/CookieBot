package net.Abdymazhit.CookieBot.customs;

import net.Abdymazhit.CookieBot.CookieBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.EnumSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Представляет собой канал тикета
 *
 * @version   01.09.2021
 * @author    Islam Abdymazhit
 */
public class TicketChannel extends Channel {

    /** Тикет */
    public final Ticket ticket;

    /** Пользователь */
    public final Member member;

    /**
     * Инициализирует канал тикета
     * @param ticketNumber Номер канала тикета
     * @param ticket Тикет
     * @param member Пользователь
     */
    public TicketChannel(int ticketNumber, Ticket ticket, Member member) {
        this.ticket = ticket;
        this.member = member;
        createChannel(ticketNumber);
    }

    /**
     * Создает канал тикета
     * @param ticketNumber Номер канала тикета
     */
    public void createChannel(int ticketNumber) {
        try {
            channel = CookieBot.getInstance().ticketsCategory.getCategory().createTextChannel("тикет-" + ticketNumber)
                    .addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .submit().get();

            // Удалить канал через 60 минут
            channel.delete().queueAfter(60, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Удаляет канал тикета
     */
    public void deleteChannel() {
        channel.delete().queueAfter(10, TimeUnit.SECONDS);
        CookieBot.getInstance().ticketsCategory.removeTicketChannel(this, member);
    }

    /**
     * Событие получения сообщения
     * @param message Сообщение
     */
    public void onMessageReceived(String message) {

    }
}
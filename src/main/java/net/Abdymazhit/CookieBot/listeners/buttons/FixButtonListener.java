package net.Abdymazhit.CookieBot.listeners.buttons;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.enums.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Кнопка исправления
 *
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class FixButtonListener extends ListenerAdapter {

    /**
     * Событие клика кнопки
     */
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        Message message = event.getMessage();
        Member member = event.getMember();

        if (!event.getComponentId().equals("fix")) return;
        if (message == null) return;
        if (member == null) return;

        if(member.getRoles().contains(Rank.OWNER.getRole())) {
            boolean isFixed = fixTicket(event.getMessage());

            if(isFixed) {
                // Удалить сообщение команды
                message.delete().submit();

                // Обновить тикеты продуктов
                CookieBot.getInstance().productsCategory.updateProductsTickets();

                event.reply("Статус тикета изменился на исправлен!").delay(3, TimeUnit.SECONDS)
                        .flatMap(InteractionHook::deleteOriginal).submit();
            } else {
                message.delete().submitAfter(3, TimeUnit.SECONDS);
                event.reply("Ошибка! Произошла ошибка при изменении статуса тикета!").delay(3, TimeUnit.SECONDS)
                        .flatMap(InteractionHook::deleteOriginal).submit();
            }
        } else {
            message.delete().submitAfter(3, TimeUnit.SECONDS);
            event.reply("У вас нет прав для этого действия!").delay(3, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).submit();
        }
    }

    /**
     * Изменяет статус тикета на исправлен
     * @param message Сообщение события ButtonClickEvent
     * @return Значение, исправлен ли тикет
     */
    private boolean fixTicket(Message message) {
        int ticketId = CookieBot.getInstance().utils.getIntByMessage(message);

        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE tickets SET fixed_on = '" + Timestamp.from(Instant.now()) + "' WHERE id = " + ticketId);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            // Вернуть значение, что тикет не исправлен
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            // Вернуть значение, что тикет не исправлен
            return false;
        }
    }
}
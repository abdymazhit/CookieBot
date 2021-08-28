package net.Abdymazhit.CookieBot;

import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.Abdymazhit.CookieBot.tickets.TicketChannel;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.concurrent.TimeUnit;

/**
 * Представляет собой слушатель событий
 *
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class EventsListener extends ListenerAdapter {

    /**
     * Событие получения сообщений
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel messageChannel = event.getChannel();

        // Проверка сообщения на команду
        if(event.isWebhookMessage()) {
            // Проверка команды на ошибку
            if(message.getEmbeds().isEmpty()) {
                message.delete().submitAfter(5, TimeUnit.SECONDS);
            } else {
                // Удалить команду через 5 минут, если взаимодействия не было
                message.delete().submitAfter(5, TimeUnit.MINUTES);
            }
        } else {
            // Проверка, является ли канал каналом продукта
            for(ProductChannel productChannel : CookieBot.getInstance().productsCategory.getProductChannels()) {
                if(productChannel.getChannel().equals(messageChannel)) {
                    // Если сообщение не является сообщением продукта, удалить сообщение через 3 секунды
                    if(!message.equals(productChannel.getWelcomeMessage()) && !message.equals(productChannel.getTicketsMessage())) {
                        message.delete().submitAfter(3, TimeUnit.SECONDS);
                    }

                    // Проверка на команду !ticket
                    if(message.getContentRaw().equals("!ticket")) {
                        // Создать новый тикет
                        messageChannel.sendMessage("Создание тикета...").delay(3, TimeUnit.SECONDS).flatMap(Message::delete).submit();
                        CookieBot.getInstance().ticketsCategory.createTicket(productChannel.getChannel().getName(), event.getMember());
                    }

                    break;
                }
            }

            // Проверка, является ли канал каналом тикета
            for(TicketChannel ticketChannel : CookieBot.getInstance().ticketsCategory.getTickets()) {
                if(ticketChannel.getChannel().equals(messageChannel)) {
                    // Проверка автора сообщений на бота
                    if(!event.getAuthor().isBot()) {
                        ticketChannel.onMessageReceived(message.getContentRaw());
                    }

                    break;
                }
            }
        }
    }

    /**
     * Событие отправки команды
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        MessageChannel messageChannel = event.getChannel();

        // Проверка, является ли канал каналом продукта
        for(ProductChannel productChannel : CookieBot.getInstance().productsCategory.getProductChannels()) {
            if(productChannel.getChannel().equals(messageChannel)) {
                // Проверка команды на /view
                if(event.getName().equals("view")) {
                    OptionMapping idOption = event.getOption("id");

                    if(idOption != null) {
                        int id = Integer.parseInt(idOption.getAsString());

                        // Отправить информацию о тикете
                        Ticket ticket = CookieBot.getInstance().database.getTicket(id);
                        if(ticket != null) {
                            MessageEmbed ticketMessageEmbed = CookieBot.getInstance().utils.getTicketMessageEmbed(ticket, "Тикет " + ticket.getId());

                            event.replyEmbeds(ticketMessageEmbed)
                                    .addActionRow(
                                            Button.primary("cancel", "Отмена"),
                                            Button.danger("delete", "Удалить тикет"),
                                            Button.success("fix", "Тикет исправлен"))
                                    .submit();
                        } else {
                            event.reply("Ошибка! Тикет не найден!").submit();
                        }
                    }
                }
                // Проверка команды на /update
                else if(event.getName().equals("update")) {
                    Member member = event.getMember();
                    if(member != null) {
                        Role developerRole = CookieBot.getInstance().jda.getRolesByName("Разработчик", true).get(0);
                        if(member.getRoles().contains(developerRole)) {
                            // Обновить все тикеты продуктов
                            CookieBot.getInstance().productsCategory.updateProductsTickets();
                            event.reply("Все тикеты продуктов обновлены!").submit();
                        } else {
                            event.reply("У вас нет прав для этого действия!").submit();
                        }
                    } else {
                        event.reply("Ошибка! Участник не найден!").submit();
                    }
                }

                break;
            }
        }
    }

    /**
     * Событие клика кнопки
     */
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        Message message = event.getMessage();
        Member member = event.getMember();

        if(message != null && member != null) {
            if(event.getComponentId().equals("cancel")) {
                message.delete().submitAfter(3, TimeUnit.SECONDS);
                event.reply("Команда отменяется...").delay(3, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).submit();
            } else if(event.getComponentId().equals("delete")) {
                Role developerRole = CookieBot.getInstance().jda.getRolesByName("Разработчик", true).get(0);
                if(member.getRoles().contains(developerRole)) {
                    boolean isDeleted = CookieBot.getInstance().database.deleteTicket(event.getMessage());

                    if(isDeleted) {
                        // Удалить сообщение команды
                        message.delete().submit();

                        // Обновить тикеты продуктов
                        CookieBot.getInstance().productsCategory.updateProductsTickets();

                        event.reply("Тикет успешно удален!").delay(3, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).submit();
                    } else {
                        message.delete().submitAfter(3, TimeUnit.SECONDS);
                        event.reply("Ошибка! Произошла ошибка при удалении тикета!")
                                .delay(3, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).submit();
                    }
                } else {
                    message.delete().submitAfter(3, TimeUnit.SECONDS);
                    event.reply("У вас нет прав для этого действия!").delay(3, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).submit();
                }
            } else if(event.getComponentId().equals("fix")) {
                Role developerRole = CookieBot.getInstance().jda.getRolesByName("Разработчик", true).get(0);
                if(member.getRoles().contains(developerRole)) {
                    boolean isFixed = CookieBot.getInstance().database.fixTicket(event.getMessage());

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
        } else {
            event.reply("Произошла ошибка! Оригинальное сообщение не найдено!")
                    .delay(3, TimeUnit.SECONDS).flatMap(InteractionHook::deleteOriginal).submit();
        }
    }
}
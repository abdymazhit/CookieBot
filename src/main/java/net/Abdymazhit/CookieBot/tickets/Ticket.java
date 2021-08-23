package net.Abdymazhit.CookieBot.tickets;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.enums.Priority;
import net.Abdymazhit.CookieBot.enums.TicketState;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Представляет собой тикет
 *
 * @version   23.08.2021
 * @author    Islam Abdymazhit
 */
public class Ticket {

    /** Канал тикета */
    private TextChannel channel;

    /** Стадия тикета */
    private TicketState ticketState;

    /** Название мини-игры */
    private final String miniGame;

    /** Заговолок */
    private String title;

    /** Описание */
    private String description;

    /** Шаги для воспроизведения проблемы */
    private String steps;

    /** Что происходит в результате */
    private String result;

    /** Что должно происходить */
    private String shouldBe;

    /** Приложенные материалы */
    private String materials;

    /**
     * Инициализирует тикет
     * @param miniGame Название мини-игры
     * @param id Id тикета
     * @param member Тестер
     */
    public Ticket(String miniGame, int id, Member member) {
        this.miniGame = miniGame;
        ticketState = TicketState.FILLING;
        createChannel(id, member);
    }

    /**
     * Создает новый канал тикету
     * @param id Id тикета
     * @param member Тестер
     */
    private void createChannel(int id, Member member) {
        try {
            channel = CookieBot.tickets.getCategory().createTextChannel("Тикет-" + id)
                    .addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .submit().get();
            // Удалить канал через 60 минут
            channel.delete().submitAfter(60, TimeUnit.MINUTES);

            // Отправить информационное сообщение
            sendInformationMessage();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправляет информационное сообщение
     */
    private void sendInformationMessage() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Новый тикет");
        embedBuilder.setColor(0xFF58B9FF);
        embedBuilder.addField("Мини-игра", miniGame, false);
        embedBuilder.addField("Инструкция по заполнению тикета",
                "1. Скопируйте бланк снизу\n" +
                        "2. Правильно заполните бланк\n" +
                        "3. Отправьте заполненный бланк",
                false
        );
        embedBuilder.addField("Правила",
                "1. Вы должны изменять только **--XXX--**\n" +
                        "2. Приложенные материалы должны быть загружены в **другие ресурсы**\n" +
                        "3. Cкриншоты желательно загружать в **imgur.com**\n" +
                        "4. Видеоматериалы желательно загружать в **youtube.com**",
                false);
        embedBuilder.addField("В случае ошибки",
                "Введите команду **!cancel** для отмены тикета",
                false);
        embedBuilder.setDescription("Обратите внимание, у вас есть 60 минут для отправки тикета");
        channel.sendMessageEmbeds(embedBuilder.build()).submit();
        embedBuilder.clear();

        channel.sendMessage("```Заговолок: \n" +
                "--XXXXXXXXXXXXXXXXXXXXXXXXX--\n" +
                "\n" +
                "Описание проблемы: \n" +
                "--XXXXXXXXXXXXXXXXXXXXXXXXX--\n" +
                "\n" +
                "Шаги для воспроизведения проблемы: \n" +
                "--XXXXXXXXXXXXXXXXXXXXXXXXX--\n" +
                "\n" +
                "Что происходит в результате: \n" +
                "--XXXXXXXXXXXXXXXXXXXXXXXXX--\n" +
                "\n" +
                "Что должно происходить: \n" +
                "--XXXXXXXXXXXXXXXXXXXXXXXXX--\n" +
                "\n" +
                "Приложенные материалы: \n" +
                "--XXXXXXXXXXXXXXXXXXXXXXXXX--```"
        ).submit();
    }

    /**
     * Событие получения сообщения
     * @param message Сообщение
     */
    public void onMessageReceived(String message) {
        // Проверка на команду !cancel
        if(message.equals("!cancel")) {
            if(ticketState.equals(TicketState.CANCELLING)) {
                channel.sendMessage("Ошибка! Тикет уже отменяется!").submit();
            } else if(ticketState.equals(TicketState.SUCCESS)) {
                channel.sendMessage("Ошибка! Нельзя отменить уже отправленный тикет!").submit();
            } else {
                cancelTicket();
            }
        }
        // Проверка, стадии тикета на заполнение
        else if(ticketState.equals(TicketState.FILLING)) {
            // Получить заполненный бланк по строкам
            String[] strings = message.split("\n");

            // Проверка, является ли длина строк 17
            // 17, так как это длина бланка
            if(strings.length != 17) {
                channel.sendMessage("Ошибка! Вы неправильно заполнили бланк!").submit();
            } else {
                // Получить заполненные параметры
                title = strings[1];
                description = strings[4];
                steps = strings[7];
                result = strings[10];
                shouldBe = strings[13];
                materials = strings[16];

                // Отправить сообщение о необходимости выбора приоритета тикета
                channel.sendMessage(
                        "Бланк успешно заполнен! Теперь вы должны написать приоритет тикета. Например: Низкий\n" +
                                "\n" +
                                "**Приоритеты:** \n" +
                                "Низкий: Обычные ошибки, которые никак не влияют на игру\n" +
                                "Средний: Ошибки, которые вредят игре\n" +
                                "Высокий : Серьезная ошибка, которая сильно вредит игре\n" +
                                "Критический : Серверная ошибка, которая приводит к крашу\n"
                ).submit();

                // Установить стадию тикета на выбор приоритета
                ticketState = TicketState.SELECTING_PRIORITY;
            }
        }
        // Проверка, стадии тикета на выбор приоритета
        else if(ticketState.equals(TicketState.SELECTING_PRIORITY)) {
            // Получить приоритет тикета
            Priority priority = Priority.getPriority(message);

            if(priority == null) {
                channel.sendMessage("Ошибка! Вы указали неправильный приоритет тикета!").submit();
            } else {
                // Отправить информацию о тикете
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Новый тикет");
                embedBuilder.setColor(0xFF58B9FF);
                embedBuilder.addField("Мини-игра", miniGame, true);
                embedBuilder.addField("Приоритет", priority.getName(), true);
                embedBuilder.addField("Заговолок", title, false);
                embedBuilder.addField("Описание проблемы", description, false);
                embedBuilder.addField("Шаги для воспроизведения проблемы", steps, false);
                embedBuilder.addField("Что происходит в результате", result, false);
                embedBuilder.addField("Что должно происходить", shouldBe, false);
                embedBuilder.addField("Приложенные материалы", materials, false);
                embedBuilder.setTimestamp(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
                channel.sendMessageEmbeds(embedBuilder.build()).submit();
                embedBuilder.clear();

                channel.sendMessage("Проверьте правильность заполнения. Если всё правильно, введите команду **!send**. Если вы обнаружили ошибку, введите команду **!cancel** для отмены тикета").submit();

                // Установить стадию тикета на отправку
                ticketState = TicketState.SENDING;
            }
        }
        // Проверка, стадии тикета на отправку
        else if(ticketState.equals(TicketState.SENDING)) {
            // Проверка на команду !send
            if(message.equals("!send")) {
                sendTicket();
            } else {
                channel.sendMessage("Ошибка! Такой команды не существует!").submit();
            }
        }
    }

    /**
     * Отправляет тикет
     */
    private void sendTicket() {
        ticketState = TicketState.SUCCESS;
    }

    /**
     * Отменяет тикет
     */
    private void cancelTicket() {
        channel.sendMessage("Тикет отменяется...").delay(3, TimeUnit.SECONDS).flatMap(Message::delete).submit();
        channel.delete().submitAfter(3, TimeUnit.SECONDS);
        CookieBot.tickets.removeTicket(this);
        ticketState = TicketState.CANCELLING;
    }

    /**
     * Получает канал тикета
     * @return Канал тикета
     */
    public TextChannel getChannel() {
        return channel;
    }
}
package net.Abdymazhit.CookieBot.tickets;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.enums.Priority;
import net.Abdymazhit.CookieBot.enums.TicketState;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Представляет собой канал тикета
 *
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class TicketChannel {

    /** Канал тикета */
    private TextChannel channel;

    /** Тикет */
    private final Ticket ticket;

    /** Стадия тикета */
    private TicketState ticketState;

    /**
     * Инициализирует канал тикета
     * @param productName Название продукта
     * @param id Id тикета
     * @param member Тестер
     */
    public TicketChannel(String productName, int id, Member member) {
        ticket = new Ticket(productName);
        ticket.setCreatorId(member.getId());
        ticketState = TicketState.FILLING;
        createChannel(id, member);
    }

    /**
     * Создает канал тикету
     * @param id Id тикета
     * @param member Тестер
     */
    private void createChannel(int id, Member member) {
        try {
            channel = CookieBot.getInstance().ticketsCategory.getCategory().createTextChannel("Тикет-" + id)
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
        embedBuilder.addField("Продукт", ticket.getProductName(), false);
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
                ticket.setTitle(strings[1]);
                ticket.setDescription(strings[4]);
                ticket.setSteps(strings[7]);
                ticket.setResult(strings[10]);
                ticket.setShouldBe(strings[13]);
                ticket.setMaterials(strings[16]);

                // Отправить сообщение о необходимости выбора приоритета тикета
                channel.sendMessage(
                        "Бланк успешно заполнен! Теперь вы должны написать приоритет тикета. Например: Значительный\n" +
                                "\n" +
                                "**Приоритеты:** \n" +
                                "Тривиальный: тикеты подобного рода будут выполняться в последнюю очередь\n" +
                                "Незначительный: вопрос не требует срочного вмешательства, к нему приступят после решения большей части прочих задач\n" +
                                "Значительный: значение серьёзности по умолчанию. Тикетов с таким приоритетом в проекте большинство\n" +
                                "Критический: ошибку исправят оперативно, назначив лучших специалистов\n" +
                                "Блокирующий: приступать стоит немедленно, без устранения дефекта всё остальное не имеет смысла"
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
                // Установить параметры тикеты
                ticket.setPriority(priority);
                ticket.setCreatedOn(Timestamp.from(Instant.now()));

                // Отправить информацию о тикете
                MessageEmbed ticketMessageEmbed = CookieBot.getInstance().utils.getTicketMessageEmbed(ticket, "Новый тикет");
                channel.sendMessageEmbeds(ticketMessageEmbed).submit();

                // Отправить сообщение о необходимости проверки
                channel.sendMessage("Проверьте правильность заполнения. Если всё правильно, введите команду **!send**. " +
                        "Если вы обнаружили ошибку, введите команду **!cancel** для отмены тикета").submit();

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
        CookieBot.getInstance().database.addTicket(this);
        ticketState = TicketState.SUCCESS;
    }

    /**
     * Отменяет тикет
     */
    private void cancelTicket() {
        channel.sendMessage("Тикет отменяется...").submit();
        channel.delete().submitAfter(3, TimeUnit.SECONDS);
        CookieBot.getInstance().ticketsCategory.removeTicket(this);
        ticketState = TicketState.CANCELLING;
    }

    /**
     * Получает тикет
     * @return Тикет
     */
    public Ticket getTicket() {
        return ticket;
    }

    /**
     * Получает канал тикета
     * @return Канал тикета
     */
    public TextChannel getChannel() {
        return channel;
    }
}
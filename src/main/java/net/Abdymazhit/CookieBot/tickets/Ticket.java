package net.Abdymazhit.CookieBot.tickets;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.enums.Priority;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;

/**
 * Представляет собой тикет
 *
 * @version   23.08.2021
 * @author    Islam Abdymazhit
 */
public class Ticket {

    /** Канал тикета */
    private TextChannel channel;

    /** Значение, заполнен ли бланк тикета */
    private boolean isFilled;

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
        isFilled = false;
        createChannel(id, member);
    }

    /**
     * Создает новый канал тикету
     * @param id Id тикета
     * @param member Тестер
     */
    public void createChannel(int id, Member member) {
        try {
            channel = CookieBot.tickets.getCategory().createTextChannel("Тикет-" + id)
                    .addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .submit().get();
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
                true);
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
        // Проверка, заполнен ли бланк тикета
        if(!isFilled) {
            // Получить заполненный бланк по строкам
            String[] strings = message.split("\n");

            // Проверка, является ли длина строк 17
            // 17, так как это длина бланка
            if(strings.length != 17) {
                channel.sendMessage("Вы неправильно заполнили бланк! Попросите помощи у коллег! Тикет закрывается...").submit();
                closeTicket();
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

                // Установить значение заполненности тикета
                isFilled = true;
            }
        } else {
            // Получить приоритет тикета
            Priority priority = Priority.getPriority(message);

            if(priority != null) {
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

                channel.sendMessage("Проверьте правильность заполнения. Если всё правильно, напишите команду **/send**. Если вы обнаружили ошибку, напишите команду **/cancel**.").submit();
            } else {
                channel.sendMessage("Неправильный приоритет тикета! Попросите помощи у коллег! Тикет закрывается...").submit();
                closeTicket();
            }
        }
    }

    /**
     * Закрывает тикет
     */
    private void closeTicket() {

    }

    /**
     * Получает канал тикета
     * @return Канал тикета
     */
    public TextChannel getChannel() {
        return channel;
    }
}
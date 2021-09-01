package net.Abdymazhit.CookieBot.tickets;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.customs.TicketChannel;
import net.Abdymazhit.CookieBot.enums.Priority;
import net.Abdymazhit.CookieBot.enums.TicketState;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.sql.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Представляет собой канал создания тикета
 *
 * @version   01.09.2021
 * @author    Islam Abdymazhit
 */
public class TicketCreationChannel extends TicketChannel {

    /** Стадия тикета */
    private TicketState ticketState;

    /**
     * Инициализирует канал создания тикета
     * @param ticketNumber Номер канала тикета
     * @param ticket Тикет
     * @param member Пользователь
     */
    public TicketCreationChannel(int ticketNumber, Ticket ticket, Member member) {
        super(ticketNumber, ticket, member);
        sendChannelMessage();
        ticketState = TicketState.FILLING;
    }

    /**
     * Отправляет сообщение канала тикета
     */
    private void sendChannelMessage() {
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
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
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
        ).queue();
    }

    /**
     * Событие получения сообщения
     * @param message Сообщение
     */
    public void onMessageReceived(String message) {
        // Проверка на команду !cancel
        if(message.equals("!cancel")) {
            if(ticketState.equals(TicketState.CANCELLING)) {
                channel.sendMessage("Ошибка! Тикет уже отменяется!").queue();
            } else if(ticketState.equals(TicketState.SUCCESS)) {
                channel.sendMessage("Ошибка! Нельзя отменить уже отправленный тикет!").queue();
            } else {
                channel.sendMessage("Отмена...").queue();
                deleteChannel();
                ticketState = TicketState.CANCELLING;
            }
        }
        // Проверка, стадии тикета на заполнение
        else if(ticketState.equals(TicketState.FILLING)) {
            // Получить заполненный бланк по строкам
            String[] strings = message.split("\n");

            // Проверка, является ли длина строк 17
            // 17, так как это длина бланка
            if(strings.length != 17) {
                channel.sendMessage("Ошибка! Вы неправильно заполнили бланк!").queue();
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
                ).queue();

                // Установить стадию тикета на выбор приоритета
                ticketState = TicketState.SELECTING_PRIORITY;
            }
        }
        // Проверка, стадии тикета на выбор приоритета
        else if(ticketState.equals(TicketState.SELECTING_PRIORITY)) {
            // Получить приоритет тикета
            Priority priority = Priority.getPriority(message);

            if(priority == null) {
                channel.sendMessage("Ошибка! Вы указали неправильный приоритет тикета!").queue();
            } else {
                // Установить параметры тикеты
                if(member.getNickname() != null) {
                    ticket.setCreator(member.getNickname());
                } else {
                    ticket.setCreator(member.getEffectiveName());
                }
                ticket.setPriority(priority);
                ticket.setCreatedOn(Timestamp.from(Instant.now()));

                // Отправить информацию о тикете
                MessageEmbed ticketMessageEmbed = CookieBot.getInstance().utils.getTicketMessageEmbed(ticket, "Новый тикет");
                channel.sendMessageEmbeds(ticketMessageEmbed).queue();

                // Отправить сообщение о необходимости проверки
                channel.sendMessage("Проверьте правильность заполнения. Если всё правильно, введите команду **!send**. " +
                        "Если вы обнаружили ошибку, введите команду **!cancel** для отмены тикета").queue();

                // Установить стадию тикета на отправку
                ticketState = TicketState.SENDING;
            }
        }
        // Проверка, стадии тикета на отправку
        else if(ticketState.equals(TicketState.SENDING)) {
            // Проверка на команду !send
            if(message.equals("!send")) {
                boolean isCreated = createTicket();
                if(!isCreated) {
                    channel.sendMessage("Произошла ошибка при создании тикета! " +
                            "Свяжитесь с владельцем! Этот канал будет удален через 60 секунд!").queue();
                    channel.delete().queueAfter(60, TimeUnit.SECONDS);
                    CookieBot.getInstance().ticketsCategory.removeTicketChannel(this, member);
                    ticketState = TicketState.SUCCESS;
                    return;
                }

                channel.sendMessage("Тикет успешно создан! Этот канал будет удален через 10 секунд!").queue();
                channel.delete().queueAfter(10, TimeUnit.SECONDS);
                CookieBot.getInstance().ticketsCategory.removeTicketChannel(this, member);
                ticketState = TicketState.SUCCESS;

                // Обновить список ожидающих верификации тикетов
                CookieBot.getInstance().separateChannels.getVerificationChannel().updatePendingVerificationTicketsList();
            } else {
                channel.sendMessage("Ошибка! Такой команды не существует!").queue();
            }
        }
    }

    /**
     * Создает тикет
     * @return Значение, создан ли тикет
     */
    private boolean createTicket() {
        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tickets " +
                    "(creator, product, priority, title, description, steps, result, should_be, materials, created_on) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;");

            preparedStatement.setString(1, ticket.getCreator());
            preparedStatement.setString(2, ticket.getProductName());
            preparedStatement.setInt(3, ticket.getPriority().getId());
            preparedStatement.setString(4, ticket.getTitle());
            preparedStatement.setString(5, ticket.getDescription());
            preparedStatement.setString(6, ticket.getSteps());
            preparedStatement.setString(7, ticket.getResult());
            preparedStatement.setString(8, ticket.getShouldBe());
            preparedStatement.setString(9, ticket.getMaterials());
            preparedStatement.setTimestamp(10, Timestamp.from(Instant.now()));
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            if(resultSet.next()) {
                int id = resultSet.getInt("id");
                PreparedStatement statement = connection.prepareStatement("INSERT INTO pending_verification_tickets (ticket_id) VALUES (?);");
                statement.setInt(1, id);
                statement.executeUpdate();
                statement.close();

                // Вернуть значение, что тикет создан
                return true;
            } else {
                // Вернуть значение, что произошла ошибка
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();

            // Вернуть значение, что произошла ошибка
            return false;
        }
    }
}
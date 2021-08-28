package net.Abdymazhit.CookieBot.listeners.commands;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.customs.Ticket;
import net.Abdymazhit.CookieBot.enums.Priority;
import net.Abdymazhit.CookieBot.products.ProductChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.Button;

import java.sql.*;

/**
 * Команда просмотра тикета
 *
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class ViewCommandListener extends ListenerAdapter {

    /**
     * Событие отправки команды
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        MessageChannel messageChannel = event.getChannel();
        Member member = event.getMember();

        if(!event.getName().equals("view")) return;
        if(member == null) return;

        // Проверка, является ли канал каналом продукта
        for(ProductChannel productChannel : CookieBot.getInstance().productsCategory.getProductChannels()) {
            if(productChannel.getChannel().equals(messageChannel)) {
                OptionMapping idOption = event.getOption("id");
                if(idOption == null) break;

                int id = Integer.parseInt(idOption.getAsString());

                // Отправить информацию о тикете
                Ticket ticket = getTicket(id);
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
    }

    /**
     * Получает тикет по id
     * @param ticketId Id тикета
     * @return Тикет
     */
    private Ticket getTicket(int ticketId) {
        try {
            Connection connection = CookieBot.getInstance().database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM tickets WHERE id = " + ticketId);
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String creatorId = resultSet.getString("creator");
                String productName = resultSet.getString("product");
                int priority = resultSet.getInt("priority");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String steps = resultSet.getString("steps");
                String result = resultSet.getString("result");
                String shouldBe = resultSet.getString("should_be");
                String materials = resultSet.getString("materials");
                Timestamp createdOn = resultSet.getTimestamp("created_on");

                Ticket ticket = new Ticket(productName);
                ticket.setId(id);
                ticket.setCreatorId(creatorId);
                ticket.setPriority(Priority.getPriority(priority));
                ticket.setTitle(title);
                ticket.setDescription(description);
                ticket.setSteps(steps);
                ticket.setResult(result);
                ticket.setShouldBe(shouldBe);
                ticket.setMaterials(materials);
                ticket.setCreatedOn(createdOn);

                return ticket;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
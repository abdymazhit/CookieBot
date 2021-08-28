package net.Abdymazhit.CookieBot.listeners.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.enums.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Команда авторизации
 *
 * @version   28.08.2021
 * @author    Islam Abdymazhit
 */
public class AuthCommandListener extends ListenerAdapter {

    /**
     * Событие отправки команды
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        MessageChannel messageChannel = event.getChannel();
        Member member = event.getMember();

        if(!messageChannel.getName().equals("авторизация")) return;
        if(!event.getName().equals("auth")) return;
        if(member == null) return;

        OptionMapping tokenOption = event.getOption("token");
        if(tokenOption == null) return;

        String token = tokenOption.getAsString().replace("https://api.vime.world/web/token/", "");
        String authInfo = CookieBot.getInstance().utils.sendGetRequest("https://api.vimeworld.ru/misc/token/" + token);
        if(authInfo == null) {
            event.reply("Ошибка! Неверный токен авторизации!").submit();
            return;
        }

        JsonObject authObject = new JsonParser().parse(authInfo).getAsJsonObject();

        boolean isValid = authObject.get("valid").getAsBoolean();
        if(!isValid) {
            event.reply("Ошибка! Неверный токен авторизации или время действия токена истекло!").submit();
            return;
        }

        String type = authObject.get("type").getAsString();
        if(!type.equals("AUTH")) {
            event.reply("Ошибка! Тип токена должен быть AUTH!").submit();
            return;
        }

        JsonElement ownerElement = authObject.get("owner");
        if(ownerElement.isJsonNull()) {
            event.reply("Ошибка! Владелец токена не найден!").submit();
            return;
        }

        JsonObject ownerObject = ownerElement.getAsJsonObject();

        // Изменить пользователю ник
        String username = ownerObject.get("username").getAsString();
        member.modifyNickname(username).submit();

        // Выдать пользователю ранг
        Rank rank = Rank.valueOf(ownerObject.get("rank").getAsString());
        if(!rank.equals(Rank.PLAYER)) {
            CookieBot.getInstance().jda.getGuilds().get(0).addRoleToMember(member, rank.getRole()).submit();
        }
        CookieBot.getInstance().jda.getGuilds().get(0).addRoleToMember(member, Rank.PLAYER.getRole()).submit();

        // Отправить сообщение о успешной авторизации
        event.reply("Вы успешно авторизовались! Ваш ник: " + username + ", ранг: " + rank.getName()).submit();
    }
}
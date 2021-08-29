package net.Abdymazhit.CookieBot.listeners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.enums.Rank;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Обработчик событий изменения статуса онлайна пользователя
 *
 * @version   29.08.2021
 * @author    Islam Abdymazhit
 */
public class UserUpdateOnlineStatusListener extends ListenerAdapter {

    /**
     * Событие изменения статуса онлайна пользователя
     */
    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
        Member member = event.getMember();

        if(event.getNewOnlineStatus().equals(OnlineStatus.OFFLINE)) return;
        if(!member.getRoles().contains(Rank.PLAYER.getRole())) return;

        String userInfo = CookieBot.getInstance().utils.sendGetRequest("https://api.vimeworld.ru/user/name/" + member.getNickname()
                + "?token=" + CookieBot.getInstance().config.vimeApiToken);
        if(userInfo == null) return;

        JsonObject infoObject = JsonParser.parseString(userInfo).getAsJsonArray().get(0).getAsJsonObject();

        // Обновить ранг пользователя
        Rank rank = Rank.valueOf(infoObject.get("rank").getAsString());
        if(!member.getRoles().contains(rank.getRole())) {
            for(Role role : member.getRoles()) {
                CookieBot.getInstance().jda.getGuilds().get(0).removeRoleFromMember(member, role).submit();
            }

            if(!rank.equals(Rank.PLAYER)) {
                CookieBot.getInstance().jda.getGuilds().get(0).addRoleToMember(member, rank.getRole()).submit();
            }
            CookieBot.getInstance().jda.getGuilds().get(0).addRoleToMember(member, Rank.PLAYER.getRole()).submit();
        }
    }
}
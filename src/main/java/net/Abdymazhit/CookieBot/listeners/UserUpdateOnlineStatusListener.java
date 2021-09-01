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
 * @version   01.09.2021
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

        String memberName;
        if(member.getNickname() != null) {
            memberName =  member.getNickname();
        } else {
            memberName =  member.getEffectiveName();
        }

        String userInfo = CookieBot.getInstance().utils.sendGetRequest("https://api.vimeworld.ru/user/name/" + memberName
                + "?token=" + CookieBot.getInstance().config.vimeApiToken);
        if(userInfo == null) return;

        JsonObject infoObject = JsonParser.parseString(userInfo).getAsJsonArray().get(0).getAsJsonObject();

        // Обновить ранг пользователя
        Rank rank = Rank.valueOf(infoObject.get("rank").getAsString());
        if(!member.getRoles().contains(rank.getRole())) {
            for(Role role : member.getRoles()) {
                CookieBot.getInstance().guild.removeRoleFromMember(member, role).queue();
            }

            if(!rank.equals(Rank.PLAYER)) {
                CookieBot.getInstance().guild.addRoleToMember(member, rank.getRole()).queue();
            }

            CookieBot.getInstance().guild.addRoleToMember(member, Rank.PLAYER.getRole()).queue();
        } else {
            if(rank.equals(Rank.PLAYER)) {
                for(Role role : member.getRoles()) {
                    if(!role.equals(Rank.PLAYER.getRole())) {
                        CookieBot.getInstance().guild.removeRoleFromMember(member, role).queue();
                    }
                }
            }
        }
    }
}
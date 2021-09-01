package net.Abdymazhit.CookieBot.enums;

import net.Abdymazhit.CookieBot.CookieBot;
import net.dv8tion.jda.api.entities.Role;

/**
 * Представляет собой ранг пользователя
 *
 * @version   01.09.2021
 * @author    Islam Abdymazhit
 */
public enum Rank {
    PLAYER("Проверенный"),
    VIP("VIP"),
    PREMIUM( "Premium"),
    HOLY("Holy"),
    IMMORTAL(  "Immortal"),
    BUILDER("Билдер"),
    SRBUILDER( "Пр. Билдер"),
    MAPLEAD("Гл. Билдер"),
    YOUTUBE( "YouTube"),
    DEV("Разработчик"),
    ORGANIZER( "Организатор"),
    MODER("Модер"),
    WARDEN("Пр. Модер"),
    CHIEF("Гл. Модер"),
    ADMIN("Гл. Админ"),
    OWNER("Владелец");

    private final String name;
    private final Role role;

    /**
     * Инициализирует ранг
     * @param name Название ранга
     */
    Rank(String name) {
        this.name = name;
        this.role = CookieBot.getInstance().guild.getRolesByName(name, true).get(0);
    }

    /**
     * Получает название ранга
     * @return Название ранга
     */
    public String getName() {
        return name;
    }

    /**
     * Получает роль ранга
     * @return Роль ранга
     */
    public Role getRole() {
        return role;
    }
}
package net.Abdymazhit.CookieBot.enums;

/**
 * Представляет собой приоритет тикета
 *
 * @version   23.08.2021
 * @author    Islam Abdymazhit
 */
public enum Priority {
    LOW("Низкий"),
    MEDIUM("Средний"),
    HIGH("Высокий"),
    CRITICAL("Критический");

    /** Название приоритета */
    private final String name;

    /**
     * Инициализирует приоритет
     * @param name Название приоритета
     */
    Priority(String name) {
        this.name = name;
    }

    /**
     * Получает приоритет по названию
     * @param priorityName Название приоритета
     * @return Приоритет
     */
    public static Priority getPriority(String priorityName) {
        for(Priority priority : Priority.values()) {
            if(priority.getName().equals(priorityName)) {
                return priority;
            }
        }

        return null;
    }

    /**
     * Получает название приоритета
     * @return Название приоритета
     */
    public String getName() {
        return name;
    }
}
package net.Abdymazhit.CookieBot.enums;

/**
 * Представляет собой приоритет тикета
 *
 * @version   23.08.2021
 * @author    Islam Abdymazhit
 */
public enum Priority {
    TRIVIAL(0, "Тривиальный"),
    MINOR(1, "Незначительный"),
    MAJOR(2, "Значительный"),
    CRITICAL(3, "Критический"),
    BLOCKER(4, "Блокирующий");

    /** Id приоритета */
    private final int id;

    /** Название приоритета */
    private final String name;

    /**
     * Инициализирует приоритет
     * @param name Название приоритета
     */
    Priority(int id, String name) {
        this.id = id;
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
     * Получает id приоритета
     * @return Id приоритета
     */
    public int getId() {
        return id;
    }

    /**
     * Получает название приоритета
     * @return Название приоритета
     */
    public String getName() {
        return name;
    }
}
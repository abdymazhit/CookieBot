package net.Abdymazhit.CookieBot.customs;

import net.Abdymazhit.CookieBot.enums.Priority;

import java.sql.Timestamp;

/**
 * Представляет собой тикет
 *
 * @version   24.08.2021
 * @author    Islam Abdymazhit
 */
public class Ticket {

    /** Название продукта */
    private final String productName;

    /** Id тикета */
    private int id;

    /** Приоритет тикета */
    private Priority priority;

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

    /** Время создания тикета */
    private Timestamp createdOn;

    /**
     * Инициализирует тикет
     * @param productName Название продукта
     */
    public Ticket(String productName) {
        this.productName = productName;
    }

    /**
     * Получает название продукта
     * @return Название продукта
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Устанавливает id тикету
     * @param id Id тикета
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Получает id тикета
     * @return Id тикета
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает приоритет тикета
     * @param priority Приоритет тикета
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Получает приоритет тикета
     * @return Приоритет тикета
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Устанавливает заговолок
     * @param title Заговолок
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Получает заговолок
     * @return Заговолок
     */
    public String getTitle() {
        return title;
    }

    /**
     * Устанавливает описание
     * @param description Описание
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Получает описание
     * @return Описание
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает шаги для воспроизведения проблемы
     * @param steps Шаги для воспроизведения проблемы
     */
    public void setSteps(String steps) {
        this.steps = steps;
    }

    /**
     * Получает шаги для воспроизведения проблемы
     * @return Шаги для воспроизведения проблемы
     */
    public String getSteps() {
        return steps;
    }

    /**
     * Устанавливает что происходит в результате
     * @param result Что происходит в результате
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Получает что происходит в результате
     * @return Что происходит в результате
     */
    public String getResult() {
        return result;
    }

    /**
     * Устанавливает что должно происходить
     * @param shouldBe Что должно происходить
     */
    public void setShouldBe(String shouldBe) {
        this.shouldBe = shouldBe;
    }

    /**
     * Получает что должно происходить
     * @return Что должно происходить
     */
    public String getShouldBe() {
        return shouldBe;
    }

    /**
     * Устанавливает приложенные материалы
     * @param materials Приложенные материалы
     */
    public void setMaterials(String materials) {
        this.materials = materials;
    }

    /**
     * Получает приложенные материалы
     * @return Приложенные материалы
     */
    public String getMaterials() {
        return materials;
    }

    /**
     * Устанавливает время создания тикета
     * @param createdOn Время создания тикета
     */
    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * Получает время создания тикета
     * @return Время создания тикета
     */
    public Timestamp getCreatedOn() {
        return createdOn;
    }
}

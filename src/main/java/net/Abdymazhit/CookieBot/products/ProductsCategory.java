package net.Abdymazhit.CookieBot.products;

import net.Abdymazhit.CookieBot.CookieBot;
import net.Abdymazhit.CookieBot.enums.Rank;
import net.Abdymazhit.CookieBot.products.channels.HideAndSeek;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Представляет собой категорию продуктов
 *
 * @version   01.09.2021
 * @author    Islam Abdymazhit
 */
public class ProductsCategory {

    /** Категория продуктов */
    private Category category;

    /** Список каналов продуктов */
    private List<ProductChannel> productChannels;

    /**
     * Инициализирует категорию продуктов
     */
    public ProductsCategory() {
        deleteCategory();
        createCategory();
        createProductsChannels();
        updateProductsAvailableTicketsList();
    }

    /**
     * Удаляет категорию продуктов
     */
    private void deleteCategory() {
        Category category = CookieBot.getInstance().guild.getCategoriesByName("продукты", true).get(0);
        for(TextChannel textChannel : category.getTextChannels()) {
            textChannel.delete().queue();
        }
        category.delete().queue();
    }

    /**
     * Создает категорию продуктов
     */
    private void createCategory() {
        try {
            category = CookieBot.getInstance().guild.createCategory("продукты")
                    .addPermissionOverride(Rank.PLAYER.getRole(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(CookieBot.getInstance().guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает каналы продуктов
     */
    private void createProductsChannels() {
        productChannels = new ArrayList<>();
        productChannels.add(new HideAndSeek(category));
    }

    /**
     * Обновляет список доступных тикетов продуктов
     */
    public void updateProductsAvailableTicketsList() {
        for(ProductChannel productChannel : productChannels) {
            productChannel.updateAvailableTicketsList();
        }
    }

    /**
     * Получает список каналов продуктов
     * @return Список каналов продуктов
     */
    public List<ProductChannel> getProductChannels() {
        return productChannels;
    }
}
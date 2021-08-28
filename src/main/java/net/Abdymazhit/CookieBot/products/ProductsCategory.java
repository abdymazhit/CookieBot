package net.Abdymazhit.CookieBot.products;

import net.Abdymazhit.CookieBot.CookieBot;
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
 * @version   28.08.2021
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
        updateProductsTickets();
    }

    /**
     * Удаляет категорию продуктов
     */
    private void deleteCategory() {
        for(Category category : CookieBot.getInstance().jda.getCategories()) {
            if(category.getName().equals("Продукты")) {
                for(TextChannel textChannel : category.getTextChannels()) {
                    textChannel.delete().submit();
                }
                category.delete().submit();

                break;
            }
        }
    }

    /**
     * Создает категорию продуктов
     */
    private void createCategory() {
        try {
            category = CookieBot.getInstance().jda.getGuilds().get(0).createCategory("Продукты")
                    .addPermissionOverride(CookieBot.getInstance().jda.getRolesByName("Тестер", true).get(0), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(CookieBot.getInstance().jda.getGuilds().get(0).getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
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
     * Обновляет все тикеты продуктов
     */
    public void updateProductsTickets() {
        for(ProductChannel productChannel : productChannels) {
            productChannel.updateTickets();
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
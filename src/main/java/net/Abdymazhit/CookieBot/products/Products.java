package net.Abdymazhit.CookieBot.products;

import net.Abdymazhit.CookieBot.CookieBot;
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
 * @version   24.08.2021
 * @author    Islam Abdymazhit
 */
public class Products {

    /** Категория продуктов */
    private Category category;

    /** Список продуктов */
    private List<Product> products;

    /**
     * Инициализирует категорию продуктов
     */
    public Products() {
        deleteCategory();
        createCategory();
        createProducts();
    }

    /**
     * Удаляет категорию продуктов
     */
    private void deleteCategory() {
        for(Category category : CookieBot.jda.getCategories()) {
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
            category = CookieBot.jda.getGuilds().get(0).createCategory("Продукты")
                    .addPermissionOverride(CookieBot.jda.getRolesByName("Тестер", true).get(0), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(CookieBot.jda.getGuilds().get(0).getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .submit().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает список продуктов
     */
    private void createProducts() {
        products = new ArrayList<>();
        products.add(new HideAndSeek(category));
    }

    /**
     * Получает список продуктов
     * @return Список продуктов
     */
    public List<Product> getProducts() {
        return products;
    }
}
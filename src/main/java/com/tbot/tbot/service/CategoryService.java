package com.tbot.tbot.service;

import com.tbot.tbot.model.Category;
import com.tbot.tbot.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления категориями.
 * Предоставляет методы для добавления, удаления и отображения дерева категорий.
 */
@Service
public class CategoryService {

    /**
     * Репозиторий для работы с сущностями Category.
     */
    private final CategoryRepository repo;

    /**
     * Конструктор сервиса категорий.
     * @param repo репозиторий категорий
     */
    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    /**
     * Добавляет новую категорию.
     * @param name имя категории
     * @param parentName имя родительской категории (может быть null)
     * @return сообщение о результате операции
     */
    public String addCategory(String name, String parentName) {
        // Проверка на существование категории с таким именем
        if (repo.findByName(name).isPresent())
            return "Категория уже существует";
        // Создаёт новую категорию и устанавливает имя
        Category category = new Category();
        category.setName(name);
        // Если указан родитель, ищет его по имени
        if (parentName != null) {
            Optional<Category> parentOpt = repo.findByName(parentName);
            // Если родитель не найден — возвращает сообщение об ошибке
            if (parentOpt.isEmpty())
                return "Родительская категория не найдена";
            // Устанавливает родителя для новой категории
            category.setParent(parentOpt.get());
        }
        // Сохраняет категорию в базе
        repo.save(category);
        return "Категория добавлена: " + name;
    }

    /**
     * Возвращает дерево категорий в виде строки.
     * @return строковое представление дерева категорий
     */
    @Transactional(readOnly = true)
    public String getCategoryTree() {
        // Получаем корневые категории (без родителя)
        List<Category> roots = repo.findAll().stream()
                .filter(cat -> cat.getParent() == null)
                .toList();
        if (roots.isEmpty())
            return "Дерево пусто.";
        StringBuilder sb = new StringBuilder();
        for (Category root : roots) {
            // Рекурсивно добавляем строки для дерева категорий
            buildTreeString(root, sb, 0);
        }
        return sb.toString();
    }

    /**
     * Рекурсивно строит строку для дерева категорий.
     * @param category текущая категория
     * @param sb StringBuilder для накопления строки
     * @param depth уровень вложенности
     */
    private void buildTreeString(Category category, StringBuilder sb, int depth) {
        // Добавляет отступы в зависимости от уровня вложенности
        sb.append("  ".repeat(depth)).append("- ").append(category.getName()).append("\n");
        // Для каждого дочернего элемента вызываем метод рекурсивно
        for (Category child : category.getChildren()) {
            buildTreeString(child, sb, depth + 1);
        }
    }

    /**
     * Удаляет категорию по имени.
     * @param name имя категории
     * @return сообщение о результате операции
     */
    public String removeCategory(String name) {
        Optional<Category> opt = repo.findByName(name);
        // Если категория не найдена — возвращаем сообщение об ошибке
        if (opt.isEmpty())
            return "Категория не найдена";
        // Удаляем найденную категорию
        repo.delete(opt.get());
        return "Категория удалена: " + name;
    }
}

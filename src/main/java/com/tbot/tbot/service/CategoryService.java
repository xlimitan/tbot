package com.tbot.tbot.service;

import com.tbot.tbot.model.Category;
import com.tbot.tbot.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    private final CategoryRepository repo;

    public String addCategory(String name, String parentName) {
        if (repo.findByName(name).isPresent()) return "Категория уже существует";

        Category category = new Category();
        category.setName(name);

        if (parentName != null) {
            Optional<Category> parentOpt = repo.findByName(parentName);
            if (parentOpt.isEmpty()) return "Родительская категория не найдена";
            category.setParent(parentOpt.get());
        }

        repo.save(category);
        return "Категория добавлена: " + name;
    }
    @Transactional(readOnly = true)
    public String getCategoryTree() {
        List<Category> roots = repo.findAll().stream()
                .filter(cat -> cat.getParent() == null)
                .toList();

        if (roots.isEmpty()) return "Дерево пусто.";

        StringBuilder sb = new StringBuilder();
        for (Category root : roots) {
            buildTreeString(root, sb, 0);
        }

        return sb.toString();
    }

    private void buildTreeString(Category category, StringBuilder sb, int depth) {
        sb.append("  ".repeat(depth)).append("- ").append(category.getName()).append("\n");
        for (Category child : category.getChildren()) {
            buildTreeString(child, sb, depth + 1);
        }
    }

    public String removeCategory(String name) {
        Optional<Category> opt = repo.findByName(name);
        if (opt.isEmpty()) return "Категория не найдена";

        repo.delete(opt.get());
        return "Категория удалена: " + name;
    }
}

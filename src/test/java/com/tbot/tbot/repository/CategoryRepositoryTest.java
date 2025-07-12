package com.tbot.tbot.repository;

import com.tbot.tbot.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repo;

    @Test
    void saveAndFindByName_shouldWork() {
        Category category = new Category();
        category.setName("Food");
        repo.save(category);

        Optional<Category> found = repo.findByName("Food");
        assertTrue(found.isPresent());
        assertEquals("Food", found.get().getName());
    }

    @Test
    void findByName_shouldReturnEmptyIfNotExists() {
        Optional<Category> found = repo.findByName("NonExistent");
        assertTrue(found.isEmpty());
    }
}

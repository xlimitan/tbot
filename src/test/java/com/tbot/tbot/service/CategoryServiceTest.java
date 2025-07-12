package com.tbot.tbot.service;

import com.tbot.tbot.model.Category;
import com.tbot.tbot.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private CategoryRepository repo;
    private CategoryService service;

    @BeforeEach
    void setUp() {
        repo = mock(CategoryRepository.class);
        service = new CategoryService(repo);
    }

    @Test
    void addCategory_shouldReturnExistsMessage() {
        when(repo.findByName("Food")).thenReturn(Optional.of(new Category()));
        String result = service.addCategory("Food", null);
        assertEquals("Категория уже существует", result);
    }

    @Test
    void addCategory_shouldReturnParentNotFound() {
        when(repo.findByName("New")).thenReturn(Optional.empty());
        when(repo.findByName("Parent")).thenReturn(Optional.empty());
        String result = service.addCategory("New", "Parent");
        assertEquals("Родительская категория не найдена", result);
    }

    @Test
    void addCategory_shouldAddSuccessfully() {
        when(repo.findByName("New")).thenReturn(Optional.empty());
        when(repo.findByName("Parent")).thenReturn(Optional.of(new Category()));
        String result = service.addCategory("New", "Parent");
        assertTrue(result.startsWith("Категория добавлена"));
        verify(repo).save(any(Category.class));
    }

    @Test
    void removeCategory_shouldReturnNotFound() {
        when(repo.findByName("Food")).thenReturn(Optional.empty());
        String result = service.removeCategory("Food");
        assertEquals("Категория не найдена", result);
    }

    @Test
    void removeCategory_shouldRemoveSuccessfully() {
        Category cat = new Category();
        when(repo.findByName("Food")).thenReturn(Optional.of(cat));
        String result = service.removeCategory("Food");
        assertTrue(result.startsWith("Категория удалена"));
        verify(repo).delete(cat);
    }

    @Test
    void getCategoryTree_shouldReturnEmpty() {
        when(repo.findAll()).thenReturn(List.of());
        String result = service.getCategoryTree();
        assertEquals("Дерево пусто.", result);
    }
}

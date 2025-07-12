package com.tbot.tbot.service;

import com.tbot.tbot.model.Category;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryExcelServiceTest {

    private CategoryService categoryService;
    private CategoryExcelService excelService;

    @BeforeEach
    void setUp() {
        categoryService = mock(CategoryService.class);
        excelService = new CategoryExcelService(categoryService);
    }

    @Test
    void exportCategories_shouldReturnExcelBytes() throws IOException {
        Category cat = new Category();
        cat.setName("Food");
        List<Category> categories = List.of(cat);
        ByteArrayOutputStream out = excelService.exportCategories(categories);
        assertNotNull(out);
        assertTrue(out.size() > 0);
    }

    @Test
    void importCategories_shouldSaveCategories() throws IOException {
        Category cat = new Category();
        cat.setName("Food");
        List<Category> categories = List.of(cat);
        ByteArrayOutputStream out = excelService.exportCategories(categories);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        excelService.importCategories(in);
        verify(categoryService, atLeastOnce()).addCategory(eq("Food"), any());
    }

    @Test
    void importCategories_withInvalidData_shouldThrowException() {
        byte[] invalidData = "not an excel".getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(invalidData);
        assertThrows(NotOfficeXmlFileException.class, () -> {
            excelService.importCategories(in);
        });
    }
}

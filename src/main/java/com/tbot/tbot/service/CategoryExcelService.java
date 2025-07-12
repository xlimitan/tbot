package com.tbot.tbot.service;

import com.tbot.tbot.model.Category;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Сервис для экспорта и импорта категорий в формате Excel.
 * <p>
 * Экспорт:
 * - Формирует Excel-файл (.xlsx) с двумя столбцами: название категории и её родитель.
 * <p>
 * Импорт:
 * - Загружает категории из Excel-файла, добавляя их в базу данных через CategoryService.
 * - Корректно обрабатывает файлы, изменённые в Google Документах (пустые строки, разные типы ячеек).
 */
@Service
public class CategoryExcelService {

    private final CategoryService categoryService;

    public CategoryExcelService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Экспортирует список категорий в Excel-файл.
     * @param categories список категорий для экспорта
     * @return поток с данными Excel-файла
     * @throws IOException при ошибке записи файла
     */
    public ByteArrayOutputStream exportCategories(List<Category> categories) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Categories");
        int rowNum = 0;
        // Записываем каждую категорию в отдельную строку
        for (Category category : categories) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(category.getName());
            row.createCell(1).setCellValue(category.getParent() != null ? category.getParent().getName() : "");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out;
    }

    /**
     * Импортирует категории из Excel-файла и сохраняет их в базе данных.
     * <p>
     * Пропускает пустые строки и корректно обрабатывает разные типы ячеек.
     * @param inputStream входящий поток Excel-файла
     * @throws IOException при ошибке чтения файла
     */
    public void importCategories(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            // Пропускаем пустые строки и строки без названия категории
            if (row == null || row.getCell(0) == null) continue;
            String name = getCellString(row.getCell(0));
            if (name == null || name.isBlank()) continue;
            // Получаем имя родителя, если оно указано
            String parentName = row.getCell(1) != null ? getCellString(row.getCell(1)) : null;
            // Добавляем категорию через сервис, если имя не пустое
            categoryService.addCategory(name, (parentName == null || parentName.isBlank()) ? null : parentName);
        }
        workbook.close();
    }

    /**
     * Преобразует значение ячейки Excel в строку, учитывая тип данных.
     * @param cell ячейка Excel
     * @return строковое представление значения ячейки
     */
    private String getCellString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }
}

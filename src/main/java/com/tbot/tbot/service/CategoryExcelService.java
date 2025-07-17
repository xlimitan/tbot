package com.tbot.tbot.service;

import com.tbot.tbot.model.Category;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
     * Экспортирует список категорий в Excel-файл с двумя листами:
     * <ul>
     *   <li>Лист "Категории" — только корневые категории (без родителя).</li>
     *   <li>Лист "Подкатегории" — только подкатегории (с указанием родителя).</li>
     * </ul>
     * В каждом листе ID — это порядковый номер строки в рамках листа.
     * @param categories список всех категорий для экспорта
     * @param out выходной поток для записи Excel-файла
     * @throws IOException при ошибке записи файла
     */
    public void exportCategoriesToExcel(List<Category> categories, OutputStream out) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        // Лист для корневых категорий
        Sheet rootSheet = workbook.createSheet("Категории");
        Row rootHeader = rootSheet.createRow(0);
        rootHeader.createCell(0).setCellValue("ID");
        rootHeader.createCell(1).setCellValue("Название");
        int rootRowNum = 1;
        for (Category category : categories) {
            if (category.getParent() == null) {
                Row row = rootSheet.createRow(rootRowNum++);
                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
            }
        }
        // Лист для подкатегорий
        Sheet subSheet = workbook.createSheet("Подкатегории");
        Row subHeader = subSheet.createRow(0);
        subHeader.createCell(0).setCellValue("ID");
        subHeader.createCell(1).setCellValue("Название");
        subHeader.createCell(2).setCellValue("Категория");
        int subRowNum = 1;
        for (Category category : categories) {
            if (category.getParent() != null) {
                Row row = subSheet.createRow(subRowNum++);
                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
                row.createCell(2).setCellValue(category.getParent().getName());
            }
        }
        workbook.write(out);
        workbook.close();
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
        // Импорт корневых категорий (лист 0)
        Sheet rootSheet = workbook.getSheetAt(0);
        for (Row row : rootSheet) {
            if (row.getRowNum() == 0) continue; // пропустить заголовок
            String name = getCellString(row.getCell(1));
            if (name == null || name.isBlank()) continue;
            categoryService.addCategory(name, null);
        }
        // Импорт подкатегорий (лист 1)
        Sheet subSheet = workbook.getNumberOfSheets() > 1 ? workbook.getSheetAt(1) : null;
        if (subSheet != null) {
            for (Row row : subSheet) {
                if (row.getRowNum() == 0) continue; // пропустить заголовок
                String name = getCellString(row.getCell(1));
                String parentName = getCellString(row.getCell(2));
                if (name == null || name.isBlank() || parentName == null || parentName.isBlank()) continue;
                categoryService.addCategory(name, parentName);
            }
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

    /**
     * Экспортирует список категорий в Excel-файл с двумя листами и возвращает его как массив байт.
     * Используется для передачи Excel-файла, например, через HTTP или Telegram.
     * @param categories список всех категорий для экспорта
     * @return массив байт с данными Excel-файла
     * @throws IOException при ошибке записи файла
     */
    public byte[] exportCategoriesAsBytes(List<Category> categories) throws IOException {
        try (var out = new java.io.ByteArrayOutputStream()) {
            exportCategoriesToExcel(categories, out);
            return out.toByteArray();
        }
    }
}

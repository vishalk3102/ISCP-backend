package com.iscp.backend.components;

import com.iscp.backend.dto.ControlCategoryCreateDTO;
import com.iscp.backend.dto.FrameworkCategoryCreateDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class ExportExcel {

    public <T> void exportToExcel(OutputStream outputStream, List<T> data, String sheetName, List<String> fieldsToInclude, Map<String, String> customHeaders) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        // Add "CONFIDENTIAL DATA" header
        addConfidentialHeader(workbook, sheet);

        // Add gap
        addGap(sheet, 2);

        // Create Header row Style
        CellStyle headerStyle = createHeaderStyle(workbook);

        // Create Header Row
        Row headerRow = sheet.createRow(3); // Start from fourth row (index 3)
        headerRow.setHeightInPoints(25);

        Field[] fields = data.get(0).getClass().getDeclaredFields();

        // Filter the fields based on the list of fields name to include
        List<Field> selectedFields = Stream.of(fields)
                .filter(field -> fieldsToInclude.contains(field.getName()))
                .collect(Collectors.toList());

        for (int i = 0; i < selectedFields.size(); i++) {
            Field field = selectedFields.get(i);
            Cell cell = headerRow.createCell(i);

            // Create Header cells based on selected fields and custom header mapping
            String headerName = customHeaders.getOrDefault(field.getName(), field.getName().toUpperCase());
            cell.setCellValue(headerName);
            cell.setCellStyle(headerStyle);
        }

        // Create Data rows
        int rowNum = 4; // Start from fifth row (index 4)
        for (T item : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < selectedFields.size(); i++) {
                Field field = selectedFields.get(i);
                field.setAccessible(true);
                Cell cell = row.createCell(i);
                try {
                    Object value = field.get(item);
                    log.info("Field: {}, Value: {}", field.getName(), value);

                    if ("controlCategory".equals(field.getName())) {
                        if (value != null) {
                            log.info("Control Category value type: {}", value.getClass().getName());

                            if (value instanceof ControlCategoryCreateDTO) {
                                ControlCategoryCreateDTO category = (ControlCategoryCreateDTO) value;
                                String categoryName = category.getControlCategoryName();
                                cell.setCellValue(categoryName != null ? categoryName : "N/A");
                            } else if (value instanceof String) {
                                // Handle string value
                                cell.setCellValue((String) value);
                            } else {
                                log.warn("Unexpected type for controlCategory: {}", value.getClass().getName());
                                cell.setCellValue("N/A");
                            }
                        } else {
                            cell.setCellValue("N/A");
                        }
                    }
                    else if ("frameworkCategory".equals(field.getName())) {
                        if (value != null) {
                            log.info("Framework Category value type: {}", value.getClass().getName());

                            if (value instanceof FrameworkCategoryCreateDTO) {
                                FrameworkCategoryCreateDTO category = (FrameworkCategoryCreateDTO) value;
                                String categoryName = category.getFrameworkCategoryName();
                                cell.setCellValue(categoryName != null ? categoryName : "N/A");
                            }
                            else if (value instanceof String) {
                                // Handle string value
                                cell.setCellValue((String) value);
                            }
                            else {
                                log.warn("Unexpected type for frameworkCategory: {}", value.getClass().getName());
                                cell.setCellValue("N/A");
                            }
                        } else {
                            cell.setCellValue("N/A");
                        }
                    }
                    else if ("evidenceComplianceStatus".equals(field.getName()) || "status".equals(field.getName())) {
                        if (value != null && value instanceof Boolean) {
                            cell.setCellValue((Boolean) value ? "Active" : "Inactive");
                        } else {
                            cell.setCellValue("N/A");
                        }
                    } else if (value != null) {
                        if (value instanceof Set) {
                            cell.setCellValue(String.join(",", (Set<String>) value));
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } else {
                        cell.setCellValue("N/A");
                    }
                } catch (IllegalAccessException e) {
                    cell.setCellValue("N/A");
                }
            }
        }

        // Auto Size columns of table as per data
        for (int i = 0; i < selectedFields.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to response
        workbook.write(outputStream);
        workbook.close();
    }

    private void addConfidentialHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
        Row confidentialRow = sheet.createRow(0);
        Cell confidentialCell = confidentialRow.createCell(0);
        confidentialCell.setCellValue("CONFIDENTIAL DATA");

        CellStyle confidentialStyle = workbook.createCellStyle();
        Font confidentialFont = workbook.createFont();
        confidentialFont.setFontHeightInPoints((short) 18);
        confidentialFont.setBold(true);
        confidentialFont.setColor(IndexedColors.RED.getIndex());
        confidentialStyle.setFont(confidentialFont);
        confidentialStyle.setAlignment(HorizontalAlignment.CENTER);
        confidentialStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        confidentialCell.setCellStyle(confidentialStyle);

        // Merge cells for the entire first row
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

        // Set row height for better visibility
        confidentialRow.setHeightInPoints(30);
    }

    private void addGap(XSSFSheet sheet, int rowIndex) {
        Row gapRow = sheet.createRow(rowIndex);
        gapRow.setHeightInPoints(20); // Set the height of the gap
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Set font style
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        style.setFont(headerFont);

        // Set alignment
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // Set background color
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Set Border
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }
}
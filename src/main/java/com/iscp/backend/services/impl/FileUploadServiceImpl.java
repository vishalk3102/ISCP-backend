package com.iscp.backend.services.impl;

import com.iscp.backend.dto.ChecklistCreateDTO;
import com.iscp.backend.dto.ControlCategoryCreateDTO;
import com.iscp.backend.dto.ControlCreateDTO;
import com.iscp.backend.services.FileUploadService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;


@Service
@AllArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    //FUNCTION TO CREATE CONTROL CATEGORY ENTITY DTO FROM EXCEL CONTENT
    @Override
    public List<ControlCategoryCreateDTO> parseControlCategoryEntries(MultipartFile file) throws IOException {
        Set<String> uniqueCategories = new HashSet<>();
        List<ControlCategoryCreateDTO> categoryEntries = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Iterate through all sheets in the workbook
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                Iterator<Row> rowIterator = sheet.iterator();

                // Skip the first row
                if (rowIterator.hasNext()) {
                    rowIterator.next();
                }

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    // Check if row is empty or contains no meaningful data
                    if (isEmptyRow(row)) {
                        continue;
                    }

                    String category = getCellValueAsString(row.getCell(0));

                    if (isEmpty(category)) {
                        continue;
                    }

                    // Only create category if it's not already processed
                    if (uniqueCategories.add(category)) {
                        ControlCategoryCreateDTO dto = new ControlCategoryCreateDTO();
                        dto.setControlCategoryId(null);
                        dto.setControlCategoryName(removeQuotes(category));
                        categoryEntries.add(dto);
                    }
                }
            }
        }
        return categoryEntries;
    }

    //FUNCTION TO CREATE CONTROL ENTITY DTO FROM EXCEL CONTENT
    @Override
    public List<ControlCreateDTO> parseControlEntries(MultipartFile file) throws IOException {
        List<ControlCreateDTO> controlEntries = new ArrayList<>();
        String previousControlName = null;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Iterate through all sheets in the workbook
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                Iterator<Row> rowIterator = sheet.iterator();

                // Skip the first row
                if (rowIterator.hasNext()) {
                    rowIterator.next();
                }

                // Iterate through all rows
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    // Check if row is empty or contains no meaningful data
                    if (isEmptyRow(row)) {
                        continue;
                    }

                    ControlCreateDTO dto = new ControlCreateDTO();

                    log.info(String.valueOf(row.getCell(1)));
                    // Get cell values (assuming same structure as CSV)
                    String category = getCellValueAsString(row.getCell(0));
                    String value1 = getCellValueAsString(row.getCell(1));
                    String value2 = getCellValueAsString(row.getCell(2));

                    // Additional validation to ensure required fields are present
                    if (isEmpty(category) || isEmpty(value1) || isEmpty(value2)) {
                        continue;
                    }

                    String controlName = String.valueOf(value1) + ":" + String.valueOf(value2);

                    // Check if the controlName is the same as the previous one
                    if (previousControlName != null && controlName.equals(previousControlName)) {
                        continue;
                    }

                    dto.setControlId(null);
                    dto.setControlName(controlName);
                    dto.setDescription("");
                    dto.setStatus(true);
                    dto.setControlCategoryName(removeQuotes(category));

                    controlEntries.add(dto);
                    previousControlName = controlName;
                }
            }
        }

        return controlEntries;
    }

    //FUNCTION TO CREATE CHECKLIST ENTITY DTO FROM EXCEL CONTENT
    @Override
    public List<ChecklistCreateDTO> parseChecklistEntries(MultipartFile file) throws IOException {
        List<ChecklistCreateDTO> checklistEntries = new ArrayList<>();
        String currentControlName = null;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Iterate through all sheets in the workbook
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                Iterator<Row> rowIterator = sheet.iterator();

                // Skip the first row
                if (rowIterator.hasNext()) {
                    rowIterator.next();
                }

                // Iterate through all rows
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    // Skip empty rows
                    if (isEmptyRow(row)) {
                        continue;
                    }

                    ChecklistCreateDTO dto = new ChecklistCreateDTO();

                    // Get cell values
                    String value1 = getCellValueAsString(row.getCell(1));
                    String value2 = getCellValueAsString(row.getCell(2));
                    String controlChecklist = getCellValueAsString(row.getCell(4));
                    String description = getCellValueAsString(row.getCell(5));

                    // Skip row if required fields are missing
                    if (isEmpty(value1) || isEmpty(value2) || isEmpty(controlChecklist)) {
                        continue;
                    }

                    String controlName = String.valueOf(value1) + ":" + String.valueOf(value2);

                    // If the control name has changed, reset the currentControlName
                    if (!controlName.equals(currentControlName)) {
                        currentControlName = controlName;
                    }

                    dto.setChecklistId(null);
                    dto.setControlChecklist(removeQuotes(controlChecklist));
                    dto.setDescription(removeQuotes(description));
                    dto.setStatus(true);
                    dto.setControlName(controlName);

                    checklistEntries.add(dto);
                }
            }
        }

        return checklistEntries;
    }

    /**
     * Safely converts an Excel cell value to String, handling different data types
     * @param cell The Excel cell to read
     * @return String representation of the cell value, or empty string if cell is null
     */
//    public static String getCellValueAsString(Cell cell) {
//        if (cell == null) {
//            return "";
//        }
//
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue();
//            case NUMERIC:
//                if (DateUtil.isCellDateFormatted(cell)) {
//                    return cell.getDateCellValue().toString();
//                }
//                // Check if the numeric value has a decimal point
//                double numericValue = cell.getNumericCellValue();
//                if (numericValue % 1 == 0) {
//                    // If the numeric value is a whole number, return it as a string
//                    return String.format("%.0f", numericValue);
//                } else {
//                    // If the numeric value has a decimal point, return it as is
//                    return String.valueOf(numericValue);
//                }
//            case BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            case FORMULA:
//                try {
//                    return cell.getStringCellValue();
//                } catch (IllegalStateException e) {
//                    // If formula results in a numeric value
//                    return String.valueOf(cell.getNumericCellValue());
//                }
//            default:
//                return "";
//        }
//    }

    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        log.info("Cell Type: " + cell.getCellType());
        log.info("Cell Value: " + cell.toString());


        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double numericValue = cell.getNumericCellValue();

                // Use DecimalFormat to handle decimal representations consistently
                DecimalFormat df = new DecimalFormat("#.##");
                // Remove any trailing zeros while preserving meaningful decimal places
                df.setDecimalSeparatorAlwaysShown(false);

                return df.format(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    // If formula results in a numeric value
                    double formulaNumericValue = cell.getNumericCellValue();
                    DecimalFormat dfFormula = new DecimalFormat("#.##");
                    dfFormula.setDecimalSeparatorAlwaysShown(false);
                    return dfFormula.format(formulaNumericValue);
                }
            default:
                return "";
        }
    }

    /**
     * Removes quotes from the beginning and end of a string if they exist
     * @param value The string to process
     * @return String with quotes removed
     */
    public static String removeQuotes(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("^\"|\"$", "");
    }

    // Helper method to check if a row is empty
    private boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }

        // Check if all cells in the first few columns are empty
        for (int i = 0; i <= 2; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !isEmpty(getCellValueAsString(cell))) {
                return false;
            }
        }
        return true;
    }

    // Helper method to check if a string is empty or null
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}

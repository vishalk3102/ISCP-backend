package com.iscp.backend.components;

import com.iscp.backend.models.Enum;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;


@Component
public class RecordIdGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy-MM");


    public static String generateRecordId(Enum.Periodicity periodicity, String startDate) {

        LocalDate currentDate = LocalDate.now();
        String currentYyMm = currentDate.format(FORMATTER);

        String xValue = switch (periodicity) {
            case Annually -> "A";
            case Bi_Annually -> "B";
            case Quarterly -> "Q";
            case Monthly -> "M";
            case OnEvent -> "O";
        };

        LocalDate startLocalDate;
        try {
            DateTimeFormatter startDateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

            var parsedDate = startDateFormatter.parse(startDate);

            startLocalDate = LocalDate.of(parsedDate.get(java.time.temporal.ChronoField.YEAR),
                    parsedDate.get(java.time.temporal.ChronoField.MONTH_OF_YEAR),
                    1);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid start date format. Expected format: 'May 2024'.", e);
        }

        String startYYMM = startLocalDate.format(DateTimeFormatter.ofPattern("yy-MM"));

        // Construct the record ID
        return String.format("%s/%s/%s1", currentYyMm, startYYMM, xValue);
    }

    public static String incrementRecordId(String currentRecordId,String startDate, int frequencyIndex) {

        LocalDate currentDate = LocalDate.now();
        String currentYyMm = currentDate.format(FORMATTER);

        // Split the record ID into components
        String[] parts = currentRecordId.split("/");
        String initialDatePart = parts[0]; // "24-09"
        String frequencyPart = parts[2]; // "M1"



        char frequencyType = frequencyPart.charAt(0);

        int incrementMonths = switch (frequencyType) {
            case 'M' ->
                    1;
            case 'B' ->
                    6;
            case 'Q' ->
                    4;
            default -> 0;
        };

        // Increment the month
        LocalDate startLocalDate;
        try {
            DateTimeFormatter startDateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

            var parsedDate = startDateFormatter.parse(startDate);

            startLocalDate = LocalDate.of(parsedDate.get(java.time.temporal.ChronoField.YEAR),
                    parsedDate.get(java.time.temporal.ChronoField.MONTH_OF_YEAR),
                    1);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid start date format. Expected format: 'May 2024'.", e);
        }

        String startYYMM = startLocalDate.format(DateTimeFormatter.ofPattern("yy-MM"));
        String newFrequencyPart = frequencyPart.charAt(0) + String.valueOf(frequencyIndex);

        // Construct the new record ID

        return String.format("%s/%s/%s", initialDatePart, startYYMM, newFrequencyPart);
    }

    public static String incrementRecordIdMonthly(String currentRecordId,int frequencyIndex) {

        // Split the record ID into components
        String[] parts = currentRecordId.split("/");
        String initialDatePart = parts[0]; // "24-09"
        String middleDatePart = parts[1]; // "24-09"
        String frequencyPart = parts[2]; // "M1"

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yy-MM-dd");
        LocalDate currentDate = LocalDate.parse(middleDatePart + "-01", inputFormatter);

        char frequencyType = frequencyPart.charAt(0);

        int incrementMonths = switch (frequencyType) {
            case 'M' ->
                    1;
            case 'B' ->
                    6;
            case 'Q' ->
                    4;
            default -> 0;
        };

        // Increment the month
        currentDate = currentDate.plusMonths(incrementMonths);

        // Create the new middle date part without the day
        String newMiddleDatePart = currentDate.format(FORMATTER);
        String newFrequencyPart = frequencyPart.charAt(0) + String.valueOf(frequencyIndex);

        // Construct the new record ID

        return String.format("%s/%s/%s", initialDatePart, newMiddleDatePart, newFrequencyPart);
    }

}

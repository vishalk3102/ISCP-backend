package com.iscp.backend.components;

import com.iscp.backend.models.Enum.Periodicity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PeriodicityManager {

    private final Map<Periodicity, Integer> periodicityMap;

    public PeriodicityManager() {
        periodicityMap = new HashMap<>();
        initializePeriodicityMap();
    }

    private void initializePeriodicityMap() {
        periodicityMap.put(Periodicity.Bi_Annually, 2);
        periodicityMap.put(Periodicity.Annually, 1);
        periodicityMap.put(Periodicity.Quarterly, 4);
        periodicityMap.put(Periodicity.Monthly, 12);
        periodicityMap.put(Periodicity.OnEvent, 1);
    }

    public boolean isValidPeriodicityChange(String currentPeriodicity, String newPeriodicity) {
        int currentFrequency = getFrequency(Periodicity.valueOf(currentPeriodicity));
        int newFrequency = getFrequency(Periodicity.valueOf(newPeriodicity));

        // Only allow increasing the frequency (e.g., from annually to quarterly)
        return newFrequency > currentFrequency;
    }


    public Integer getFrequency(Periodicity periodicity) {
        return periodicityMap.get(periodicity);
    }
}

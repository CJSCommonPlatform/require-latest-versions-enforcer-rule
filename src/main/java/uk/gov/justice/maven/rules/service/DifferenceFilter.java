package uk.gov.justice.maven.rules.service;

import java.util.Map;
import java.util.stream.Collectors;


public class DifferenceFilter<B> {

    public void filter(Map<String, B> originalMap, String criteria) {
        Map<String, B> filteredMap = originalMap.entrySet().stream()
                .filter(map -> !map.getKey().contains(criteria))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        originalMap.clear();
        originalMap.putAll(filteredMap);
    }
}

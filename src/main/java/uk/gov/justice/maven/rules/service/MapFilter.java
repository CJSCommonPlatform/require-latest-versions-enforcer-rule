package uk.gov.justice.maven.rules.service;

import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Map.Entry;

public class MapFilter<B> {

    private Map<String, B> originalMap;

    public MapFilter(Map<String, B> originalMap) {
        this.originalMap = originalMap;
    }

    public void apply(String filter) {
        Map<String, B> filteredMap = originalMap.entrySet().stream()
                .filter(map -> compile(filter).matcher(map.getKey()).find())
                .collect(toMap(Entry::getKey, Entry::getValue));
        originalMap.clear();
        originalMap.putAll(filteredMap);
    }

}

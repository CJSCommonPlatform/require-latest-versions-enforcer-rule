package uk.gov.justice.maven.rules.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MapFilterTest {

    private static final String JSON_SCHEMA_FILE = "raml/json/schema/file.json";
    private static final String RAML_FILE = "raml/file.raml";

    @Test
    public void filter() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put(JSON_SCHEMA_FILE, JSON_SCHEMA_FILE);
        map.put(RAML_FILE, RAML_FILE);
        map.put("xx.raml", "file3");
        map.put("file4", "file4");

        String filter = "(?=.*json$).*(^raml/json/schema/?)|^raml/.*raml$";

        new MapFilter<>(map).apply(filter);

        assertThat(map.size(), is(2));
        assertThat(map.keySet(), containsInAnyOrder(JSON_SCHEMA_FILE, RAML_FILE));
    }

}
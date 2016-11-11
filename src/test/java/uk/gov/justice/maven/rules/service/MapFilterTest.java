package uk.gov.justice.maven.rules.service;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MapFilterTest {
    @Test
    public void filter() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("file1", "file1");
        map.put("file2", "file2");
        map.put("file3", "file3");
        map.put("file4", "file4");

        List<String> filters = asList("^((?!file2).)*$", "^((?!file3).)*$");

        new MapFilter<>(map).apply(filters);

        assertThat(map.size(), is(2));
        assertThat(map, hasEntry("file1", "file1"));
        assertThat(map, hasEntry("file4", "file4"));
    }

}
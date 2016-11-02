package uk.gov.justice.maven.rules.service;

import uk.gov.justice.maven.rules.domain.ArtifactoryInfo;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.plugin.logging.Log;


public class ArtifactoryParser {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private Log log;

    public ArtifactoryParser(Log log) {
        this.log = log;
    }

    public ArtifactoryInfo parse(String json) {
        try {
            return objectMapper.readValue(json, ArtifactoryInfo.class);
        } catch (IOException e) {
            log.error("Error: " + e.getMessage() + " parsing json: " + json);
        }
        return new ArtifactoryInfo();
    }

}

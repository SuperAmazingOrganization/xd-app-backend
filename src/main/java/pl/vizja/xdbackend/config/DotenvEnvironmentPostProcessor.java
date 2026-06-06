package pl.vizja.xdbackend.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "dotenv";
    private static final String DOTENV_PATH_PROPERTY = "dotenv.path";
    private static final String DEFAULT_DOTENV_LOCATIONS = ".env,.env.local";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (environment.getPropertySources().contains(PROPERTY_SOURCE_NAME)) {
            return;
        }

        String configuredPath = environment.getProperty(DOTENV_PATH_PROPERTY);
        String[] locations = configuredPath != null
                ? new String[]{ configuredPath }
                : DEFAULT_DOTENV_LOCATIONS.split(",");

        for (String location : locations) {
            Path file = Path.of(location.trim());
            if (!Files.isRegularFile(file)) {
                continue;
            }
            environment.getPropertySources()
                    .addLast(new MapPropertySource(PROPERTY_SOURCE_NAME + ":" + file.getFileName(), parse(file)));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    private static java.util.Map<String, Object> parse(Path file) {
        java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq < 0) {
                    continue;
                }
                String key = trimmed.substring(0, eq).trim();
                String value = trimmed.substring(eq + 1).trim();
                if (value.length() >= 2
                        && ((value.startsWith("\"") && value.endsWith("\""))
                            || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }
                map.put(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + file, e);
        }
        return map;
    }
}

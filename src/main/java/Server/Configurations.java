package Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configurations {

    private static Configurations instance;
    private final Properties properties;

    private Configurations() {
        properties = new Properties();
        loadDefaults();
        loadFromResources();
    }

    public static synchronized Configurations getInstance() {
        if (instance == null) {
            instance = new Configurations();
        }
        return instance;
    }

    public int getThreadPoolSize() {
        return Integer.parseInt(properties.getProperty("threadPoolSize", "4"));
    }

    public String getMazeGeneratingAlgorithm() {
        return properties.getProperty("mazeGeneratingAlgorithm", "MyMazeGenerator");
    }

    public String getMazeSearchingAlgorithm() {
        return properties.getProperty("mazeSearchingAlgorithm", "BestFirstSearch");
    }

    private void loadDefaults() {
        properties.setProperty("threadPoolSize", "4");
        properties.setProperty("mazeGeneratingAlgorithm", "MyMazeGenerator");
        properties.setProperty("mazeSearchingAlgorithm", "BestFirstSearch");
    }

    private void loadFromResources() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                return;
            }
        } catch (IOException ignored) {
        }

        try (InputStream input = new FileInputStream("resources/config.properties")) {
            properties.load(input);
        } catch (IOException ignored) {
        }
    }
}

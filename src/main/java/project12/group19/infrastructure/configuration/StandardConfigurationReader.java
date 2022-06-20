package project12.group19.infrastructure.configuration;

import project12.group19.api.game.Configuration;
import project12.group19.api.infrastructure.configuration.ConfigurationReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StandardConfigurationReader implements ConfigurationReader {
    public Configuration read(String path) throws IOException {
        Properties source = new Properties();
        source.load(new BufferedReader(new FileReader(path)));
        ConfigurationContainer container = new ConfigurationContainer(source);
        return ConfigurationTranslator.translate(container);
    }
}

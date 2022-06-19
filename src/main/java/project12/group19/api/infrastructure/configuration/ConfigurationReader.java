package project12.group19.api.infrastructure.configuration;

import project12.group19.api.game.Configuration;

import java.io.IOException;

public interface ConfigurationReader {
    Configuration read(String path) throws IOException;
}

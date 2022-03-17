package project12.group19.support;

import java.net.URL;

public class ResourceLoader {
    public static URL load(String name) {
        ClassLoader loader = ResourceLoader.class.getClassLoader();
        return loader.getResource(name);
    }
}

package project12.group19.api.support;

import project12.group19.api.domain.Hit;

import java.io.IOException;
import java.util.List;

public interface HitReader {
    List<Hit> read(String... pathPatterns) throws IOException;
}

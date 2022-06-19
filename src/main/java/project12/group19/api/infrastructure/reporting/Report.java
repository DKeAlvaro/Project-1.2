package project12.group19.api.infrastructure.reporting;

import java.util.List;
import java.util.Map;

public interface Report {
    Report set(int row, String column, Object value);
    Row append();
    default Row append(Map<String, Object> values) {
        return append().set(values);
    }
    List<String> getColumns();
    int size();
    Object get(int row, String column);

    interface Row {
        Row set(String column, Object value);
        Row set(Map<String, Object> values);
        Report getParent();
    }
}

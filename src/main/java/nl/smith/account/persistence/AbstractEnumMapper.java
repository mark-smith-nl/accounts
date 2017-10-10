package nl.smith.account.persistence;

import java.util.List;
import java.util.Map;

public interface AbstractEnumMapper {

    List<Map<String, String>> getEnumByClassNameAndValue(Map<String, String> parameterMap);

    void persist(Map<String, String> parameterMap);
    
    void update(Map<String, String> parameterMap);
}

package nl.smith.account.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import nl.smith.account.annotation.PersistedEnum;
import nl.smith.account.enums.AbstractEnum;
import nl.smith.account.persistence.AbstractEnumMapper;

@Service
public class EnumService {

    private final AbstractEnumMapper abstractEnumMapper;

    @Autowired
    public EnumService(AbstractEnumMapper abstractEnumMapper) {
        this.abstractEnumMapper = abstractEnumMapper;
    }

    @PostConstruct
    private void persistEnums() {
        findAnnotatedClasses();
    }

    public void findAnnotatedClasses() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(PersistedEnum.class));
        provider.findCandidateComponents("nl.smith").forEach(candidateComponent -> {
            try {
                String beanClassName = candidateComponent.getBeanClassName();
                Class<?> clazz = Class.forName(beanClassName);
                Arrays.asList(clazz.getEnumConstants()).forEach(constant -> {
                    String name = ((Enum<?>) constant).name();
                    String description = ((AbstractEnum) constant).getDescription();
                    Map<String, String> parameterMap = new HashMap<>();
                    parameterMap.put("className", beanClassName);
                    parameterMap.put("name", name);
                    List<Map<String, String>> enumByClassNameAndValue = abstractEnumMapper.getEnumByClassNameAndValue(parameterMap);
                    switch (enumByClassNameAndValue.size()) {
                    case 0:
                        parameterMap.put("description", description);
                        abstractEnumMapper.persist(parameterMap);
                        break;
                    case 1:
                    	Map<String, String> map = enumByClassNameAndValue.get(0);
                    	if (!description.equals(enumByClassNameAndValue.get(0).get("description"))){
                            parameterMap.put("description", description);
                    		abstractEnumMapper.update(parameterMap);
                    	}
                        break;
                    default:
                    	throw new IllegalStateException(String.format("Duplicate enum found: %s.$s", beanClassName, name));
                    }
                    
                });
            } catch (ClassNotFoundException e) {
                // TODO: handle exception
            }
        });
    }

}

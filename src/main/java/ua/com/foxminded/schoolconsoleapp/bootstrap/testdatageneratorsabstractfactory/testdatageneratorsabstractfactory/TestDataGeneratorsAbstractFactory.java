package ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.testdatageneratorsabstractfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.factories.Factory;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.factories.impl.AssignerFactoryImpl;
import ua.com.foxminded.schoolconsoleapp.bootstrap.testdatageneratorsabstractfactory.factories.impl.GeneratorFactoryImpl;

public class TestDataGeneratorsAbstractFactory {
    private TestDataGeneratorsAbstractFactory() {
    };

    public static Optional<Factory<?>> getFactory(String factoryType) {
        if (factoryType.equalsIgnoreCase("AssignerFactoryImpl")) {
            Factory<Map<Integer, ArrayList<Integer>>> assignerFactory = new AssignerFactoryImpl();
            return Optional.of(assignerFactory);
        } else if (factoryType.equalsIgnoreCase("GeneratorFactoryImpl")) {
            Factory<List<String>> generatorFactory = new GeneratorFactoryImpl();
            return Optional.of(generatorFactory);
        }
        return Optional.empty();
    }
}

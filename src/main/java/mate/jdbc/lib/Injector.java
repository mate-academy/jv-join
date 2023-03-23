package mate.jdbc.lib;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mate.jdbc.exception.DataProcessingException;

public class Injector {
    private static final Map<String, Injector> injectors = new HashMap<>();
    private final Map<Class<?>, Object> instanceOfClasses = new HashMap<>();
    private final List<Class<?>> classList = new ArrayList<>();

    private Injector(String mainPackageName) {
        try {
            classList.addAll(getClasses(mainPackageName));
        } catch (IOException | ClassNotFoundException e) {
            throw new DataProcessingException("Can't get information about all classes", e);
        }
    }

    public static Injector getInstance(String mainPackageName) {
        if (injectors.containsKey(mainPackageName)) {
            return injectors.get(mainPackageName);
        }
        Injector injector = new Injector(mainPackageName);
        injectors.put(mainPackageName, injector);
        return injector;
    }

    public Object getInstance(Class<?> certainInterface) {
        Object newInstanceOfClass = null;
        Class<?> classInstance = findClassExtendingInterface(certainInterface);
        Object instanceOfCurrentClass = createInstance(classInstance);
        Field[] declaredFields = classInstance.getDeclaredFields();
        for (Field field : declaredFields) {
            if (isFieldInitialized(field, instanceOfCurrentClass)) {
                continue;
            }
            if (field.getDeclaredAnnotation(Inject.class) != null) {
                Object classToInject = getInstance(field.getType());
                newInstanceOfClass = getNewInstance(classInstance);
                setValueToField(field, newInstanceOfClass, classToInject);
            } else {
                throw new DataProcessingException("Field " + field.getName() + " in class "
                        + classInstance.getName() + " hasn't annotation Inject", null);
            }
        }
        if (newInstanceOfClass == null) {
            return getNewInstance(classInstance);
        }
        return newInstanceOfClass;
    }

    private Class<?> findClassExtendingInterface(Class<?> certainInterface) {
        for (Class<?> classList : classList) {
            Class<?>[] interfaces = classList.getInterfaces();
            for (Class<?> singleInterface : interfaces) {
                if (singleInterface.equals(certainInterface)
                        && (classList.isAnnotationPresent(Service.class)
                        || classList.isAnnotationPresent(Dao.class))) {
                    return classList;
                }
            }
        }
        throw new DataProcessingException("Can't find class which implements "
                + certainInterface.getName()
                + " interface and has valid annotation (Dao or Service)", null);
    }

    private Object getNewInstance(Class<?> certainClass) {
        if (instanceOfClasses.containsKey(certainClass)) {
            return instanceOfClasses.get(certainClass);
        }
        Object newInstance = createInstance(certainClass);
        instanceOfClasses.put(certainClass, newInstance);
        return newInstance;
    }

    private boolean isFieldInitialized(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance) != null;
        } catch (IllegalAccessException | SecurityException e) {
            throw new DataProcessingException("Can't get access to field " + field.getName(), e);
        }
    }

    private Object createInstance(Class<?> classInstance) {
        Object newInstance;
        try {
            Constructor<?> classConstructor = classInstance.getConstructor();
            newInstance = classConstructor.newInstance();
        } catch (Exception e) {
            throw new DataProcessingException("Can't create object of the class "
                    + classInstance.getName(), e);
        }
        return newInstance;
    }

    private void setValueToField(Field field, Object instanceOfClass, Object classToInject) {
        try {
            field.setAccessible(true);
            field.set(instanceOfClass, classToInject);
        } catch (IllegalAccessException e) {
            throw new DataProcessingException("Can't set value to field ", e);
        }
    }

    /**
     * Scans all classes accessible from the context class loader which
     * belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException if the class cannot be located
     * @throws IOException            if I/O errors occur
     */
    private static List<Class<?>> getClasses(String packageName)
            throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            throw new DataProcessingException("Class loader is null", null);
        }
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException if the class cannot be located
     */
    private static List<Class<?>> findClasses(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class<?>> classListFound = new ArrayList<>();
        if (!directory.exists()) {
            return classListFound;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (file.getName().contains(".")) {
                        throw new DataProcessingException(
                                "File name shouldn't consist point.", null);
                    }
                    classListFound.addAll(findClasses(file, packageName + "."
                            + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classListFound.add(Class.forName(packageName + '.'
                            + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        return classListFound;
    }
}

package top.xinsin.parser;


import top.xinsin.annotation.Command;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class XMTLApplication {

    private final static XMTLApplication xmtlApplication = new XMTLApplication();

    //需要扫描的注解
    private final static Class<?>[] commandAnnotation = {
            Command.class
    };

    /**
     * XMTL的入口方法
     * 整个系统从这里加载
     * @param clazz 主类
     * @param args 命令行参数
     * @param <T> 主类类型
     */
    public static <T> void run(Class<T> clazz, String[] args) {
        List<Class<?>> classes = new ArrayList<>();
        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
//        解析当前运行的项目下的所有类
//        并筛选出带有指定注解的类
        if (path.endsWith(".jar")) {
            try (JarFile jarFile = new JarFile(path)) {
                String packageName = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
                JarEntry jarEntry = jarFile.getJarEntry(packageName.replace('.', '/') + ".class");
                Class<?> classLoaderByJar = xmtlApplication.getClassLoaderByJar(jarFile, jarEntry, packageName);
                List<JarEntry> list = jarFile.stream()
                        .filter(item -> item.toString().startsWith(classLoaderByJar.getPackageName().replace('.', '/')) && item.toString().endsWith(".class")).toList();
                for (JarEntry entry : list) {
                    Class<?> classLoaderByJar1 = xmtlApplication.getClassLoaderByJar(jarFile, entry, entry.getName().replace(".class", "").replace('/', '.'));
                    xmtlApplication.getClassesByPackage(classes, classLoaderByJar1);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            String pathPackageName = clazz.getPackageName().replace('.', '/');
            path += pathPackageName;
            File file = new File(path);
            if (file.exists()) {
                List<File> classFiles = new ArrayList<>();
                xmtlApplication.getFilesByPath(file, classFiles);
                classes = xmtlApplication.getClassesFiltered(classFiles, pathPackageName);
            }
        }
        classes.forEach(System.out::println);
    }

    private void getClassesByPackage(List<Class<?>> classes, Class<?> clazz) {
        for (Class<?> aClass : commandAnnotation) {
            for (Annotation annotation : clazz.getAnnotations()) {
                if (annotation.annotationType().equals(aClass)) {
                    classes.add(clazz);
                }
            }
            for (Method method : clazz.getMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    if (annotation.annotationType().equals(aClass)) {
                        classes.add(clazz);
                    }
                }
            }
        }
    }
    private List<Class<?>> getClassesFiltered(List<File> classFiles, String packageName) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File classFile : classFiles) {
            Class<?> classByByte = getClassByByte(classFile, packageName);
            if (Objects.nonNull(classByByte)) {
                getClassesByPackage(classes, classByByte);
            }
        }
        return classes;
    }

    /**
     * 递归获取路径下的所有class文件
     * @param classFile 路径
     * @param classFiles class文件集合
     */
    private void getFilesByPath(File classFile, List<File> classFiles) {
        if (classFile.exists()) {
            if (classFile.isDirectory()) {
                for (File file : Objects.requireNonNull(classFile.listFiles())) {
                    if (file.isDirectory()) {
                        getFilesByPath(file, classFiles);
                    } else {
                        if (file.getName().endsWith(".class")) {
                            classFiles.add(file);
                        }
                    }
                }
            } else {
                if (classFile.getName().endsWith(".class")) {
                    classFiles.add(classFile);
                }
            }
        }
    }
    private Class<?> getClassByByte(File classPath, String packageName) {
        try (InputStream is = new FileInputStream(classPath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            ClassLoader customClassLoader = new ClassLoader() {
                @Override
                protected Class<?> findClass(String name) {
                    return defineClass(name, baos.toByteArray(), 0, baos.toByteArray().length);
                }
            };
            String className = classPath.getPath().substring(classPath.getPath().indexOf(packageName)).replace(".class", "").replace('/', '.');
            return customClassLoader.loadClass(className);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> getClassLoaderByJar(JarFile jarFile, JarEntry jarEntry, String packageName) {
        try {
            try (InputStream is = jarFile.getInputStream(jarEntry);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                ClassLoader customClassLoader = new ClassLoader() {
                    @Override
                    protected Class<?> findClass(String name) throws ClassNotFoundException {
                        return defineClass(name, baos.toByteArray(), 0, baos.toByteArray().length);
                    }
                };
                return customClassLoader.loadClass(packageName);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

/**
 * 
 */
package com.fire.login.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author lhl
 *
 *         2015年8月12日上午9:58:09
 */
final public class ClassUtil
{
    private ClassUtil() {
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and sub packages. Adapted from
     * http://snippets.dzone.com/posts/show/4831 and extended to support use of
     * JAR files
     * 
     * @param packageName The base package
     * @return The classes
     * @throws Exception
     */
    public static List<Class<?>> getClasses(String packageName) throws Exception {
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        Enumeration<URL> resources = classLoader.getResources(path);
        List<String> dirs = new ArrayList<String>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(resource.getFile());
        }
        TreeSet<String> classes = new TreeSet<String>();
        for (String directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
        for (String clazz : classes) {
            classList.add(Class.forName(clazz));
        }

        return classList;
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs. Adapted from http://snippets.dzone.com/posts/show/4831 and
     * extended to support use of JAR files
     * 
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base
     *            directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static TreeSet<String> findClasses(String directory, String packageName) throws Exception {
        TreeSet<String> classes = new TreeSet<String>();
        if (directory.startsWith("file:") && directory.contains("!")) {
            String[] split = directory.split("!");
            URL jar = new URL(split[0]);
            try (ZipInputStream zip = new ZipInputStream(jar.openStream())) {
                ZipEntry entry = null;
                while ((entry = zip.getNextEntry()) != null) {
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName().replaceAll("[$].*", "").replaceAll("[.]class", "")
                                .replace('/', '.');
                        if (className.startsWith(packageName)) {
                            classes.add(className);
                        }
                    }
                }
            }
        }
        File dir = new File(directory);
        if (!dir.exists()) {
            return classes;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file.getAbsolutePath(), packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                }
            }
        }

        return classes;
    }
}

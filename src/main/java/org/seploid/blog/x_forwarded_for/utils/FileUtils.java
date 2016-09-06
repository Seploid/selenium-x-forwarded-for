package org.seploid.blog.x_forwarded_for.utils;

import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtils {

    private static String tempDirectoryPath = null;

    public static String getResourcePath(String resourceName, boolean isDir) {
        String jarFileName = new File(FileUtils.class.getClassLoader().getResource(resourceName).getPath()).getAbsolutePath().replaceAll("(!|file:\\\\)", "");
        // Checking that resource pack to JAR
        if (jarFileName.contains(".jar")) {
            // Here we should unzip resource in temporary folder.
            // There are two different ways for Directory and File.
            if (isDir) {
                return getDirPath(resourceName);
            } else {
                return getFilePath(resourceName);
            }
        } else {
            // Here appear to resource in free access.
            return getResourcePath(resourceName);
        }
    }

    private static String getDirPath(String dirName) {
        JarFile jarFile = null;
        // First of all check that temporary directory already created
        // so if No temporary directory will be created
        if (tempDirectoryPath == null) {
            tempDirectoryPath = Files.createTempDir().getAbsolutePath();
        }
        // Now the resources will be copy from JAR to this temporary directory
        if (!new File(tempDirectoryPath + File.separator + dirName.replaceAll("/", "")).exists()) {
            try {
                List<JarEntry> dirEntries = new ArrayList<JarEntry>();
                File directory = null;
                String jarFileName = new File(FileUtils.class.getClassLoader().getResource(dirName).getPath()).getParent()
                        .replaceAll("(!|file:\\\\)", "").replaceAll("(!|file:)", "");
                jarFile = new JarFile(URLDecoder.decode(jarFileName, "UTF-8"));
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().startsWith(dirName)) {
                        if (jarEntry.getName().replaceAll("/", "").equals(dirName.replaceAll("/", ""))) {
                            directory = new File(tempDirectoryPath + File.separator + dirName.replaceAll("/", ""));
                            directory.mkdirs();
                        } else
                            dirEntries.add(jarEntry);
                    }
                }
                if (directory == null) {
                    throw new RuntimeException("There is no directory " +  dirName + "in the jar file");
                }
                for (JarEntry dirEntry : dirEntries) {
                    if (!dirEntry.isDirectory()) {
                        File dirFile = new File(directory.getParent() + File.separator + dirEntry.getName());
                        dirFile.createNewFile();
                        convertStreamToFile(dirEntry.getName(), dirFile);
                    } else {
                        File dirFile = new File(directory.getParent() + File.separator + dirEntry.getName());
                        dirFile.mkdirs();
                    }
                }
                return directory.getAbsolutePath();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    jarFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            throw new RuntimeException("There are problems in creation files in directory " + tempDirectoryPath);
        } else {
            return tempDirectoryPath + File.separator + dirName.replaceAll("/", "");
        }
    }

    private static void convertStreamToFile(String resourceFileName, File file) throws IOException {
        InputStream in = FileUtils.class.getClassLoader().getResourceAsStream(resourceFileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF8"));
        BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
        String line;
        while ((line = reader.readLine()) != null) {
            fileWriter.write(line + "\n");
        }
        fileWriter.flush();
        fileWriter.close();
        in.close();
    }

    private static String getFilePath(String fileName) {
        try {
            String[] fileType = fileName.split("\\.");
            int typeIndex = fileType.length;
            File file = File.createTempFile(StringUtils.generateRandomString("temp"), "." + fileType[typeIndex - 1]);
            file.deleteOnExit();
            convertStreamToFile(fileName, file);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Impossible to get file path");
        }
    }

    private static String getResourcePath(String resourceName) {
        String resourcePath = FileUtils.class.getClassLoader().getResource(resourceName).getPath();
        if (platformIsWindows()) {
            resourcePath = resourcePath.substring(1);
        }
        return resourcePath;
    }

    public static boolean platformIsWindows() {
        return  File.separatorChar == '\\';
    }

    public static void writeToJson(String jsonFilePath, Object object) {
        try {
            Gson gson = new Gson();
            FileWriter fileWriter = new FileWriter(jsonFilePath);
            fileWriter.write(gson.toJson(object));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

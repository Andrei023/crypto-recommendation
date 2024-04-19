package com.faptic.cryptorecommendation.utils;

import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FileUtils {

    private static final String DELIMITER = "/";
    private static final String FILE_EXTENSION = ".csv";

    private FileUtils() {
    }

    public static List<String[]> readLineByLine(Path filePath) throws Exception {
        List<String[]> list = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    list.add(line);
                }
            }
        }
        return list;
    }

    public static List<String[]> readLineByLineExample(String filePath) throws Exception {
        Path path = Paths.get(
                ClassLoader.getSystemResource(filePath).toURI());
        return readLineByLine(path);
    }

    public static Map<String, List<String[]>> readAllFiles(String folderName, String fileSuffix) throws Exception {
        Map<String, List<String[]>> result = new HashMap<>();
        File[] directoryListing = ResourceUtils.getFile("classpath:" + folderName).listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String filePath = folderName + DELIMITER + child.getName();
                List<String[]> fileLines = readLineByLineExample(filePath);
                result.put(child.getName().substring(0,
                        child.getName().length() - fileSuffix.length() - FILE_EXTENSION.length()), fileLines);
            }
        }
        return result;
    }
}

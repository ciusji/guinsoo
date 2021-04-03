/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.h2.ext;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FileRead
 *
 * @author cius.ji
 * @blame guinsoo Group
 * @since 1.8+
 */
public class FileRead {

    private final String file = "/Users/admin/Desktop/relations.csv";

    /**
     * OpenCSV read
     *
     * duration: ~1490
     *
     * @throws IOException io exception.
     */
    public void readByOpenCsv() throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            List<String[]> r = reader.readAll();
            // r.forEach(x -> System.out.println(Arrays.toString(x)));
            System.out.println(r.size());
        } catch (FileNotFoundException | CsvException e) {
            e.printStackTrace();
        }
    }

    /**
     * Buffer read
     *
     * duration: ~179
     *
     * @throws IOException io exception.
     */
    public void readByBuffer() throws IOException {
        int bufferSize = 1024;
        List<String> r = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(file), bufferSize)) {
            while ((line = br.readLine()) != null) {
                r.add(line);
            }
        }
        System.out.println(r.size());
    }

    /**
     * Stream read
     *
     * duration: ~2604
     *
     * @throws IOException io exception
     */
    public void readByStream() throws IOException {
        Stream<String> lines = Files.lines(Paths.get(file));
        List<String> r = lines.parallel().collect(Collectors.toList());
        System.out.println(r.size());
    }

    public static void main(String[] args) throws IOException {
        FileRead ff = new FileRead();
        long startTime = System.currentTimeMillis();
        // ff.readByOpenCsv();
        // ff.readByBuffer();
        ff.readByStream();
        System.out.println("Duration: " + (System.currentTimeMillis() - startTime));

    }
}

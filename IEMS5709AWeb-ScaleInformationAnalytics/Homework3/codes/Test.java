package com.monsterxia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        String filename = "data";
        String resourcesPath = RandomInit.class.getClassLoader().getResource("train_img").getPath();
        String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/";
        filename = outputPath + filename;

        for (int i = 0; i < 10; i++) {
            // 在每次循环结束时将数据写入文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                String data = "Data for iteration " + i + "\n";
                writer.write(data);
                System.out.println("Data written to file for iteration " + i);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 在下一次循环开始时从文件中按行读取数据
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Data read from file for iteration " + i + ": " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
package com.monsterxia;

import java.io.FileWriter;
import java.io.IOException;

public class RandomInit {
    public static void main(String[] args) {
        String fileName = "RandomInit";

        String resourcesPath = RandomInit.class.getClassLoader().getResource("train_img").getPath();
        String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/";
        fileName = outputPath + fileName;
        System.out.println(fileName);

        try {
            FileWriter fileWriter = new FileWriter(fileName);

            for (int i = 0; i < 10; i++){
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < 784; j++){
                    sb.append((int)(Math.random() * 256));
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length()-1);
                fileWriter.write(sb + "\n");
                System.out.println(sb);
            }
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

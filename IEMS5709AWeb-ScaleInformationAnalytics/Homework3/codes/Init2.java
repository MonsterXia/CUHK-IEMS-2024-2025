package com.monsterxia;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Init2 {
    public static void main(String[] args) {

        String inputName = "train_img";
        String tagName = "train_label";
        String fileName = "RandomInit2";

        String resourcesPath = RandomInit.class.getClassLoader().getResource("train_img").getPath();
        String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/";
        fileName = outputPath + fileName;

        try {
            BufferedReader imageReader = new BufferedReader(new FileReader(Init2.class.getClassLoader().getResource(inputName).getFile()));
            BufferedReader tagReader = new BufferedReader(new FileReader(Init2.class.getClassLoader().getResource(tagName).getFile()));

            BigInteger[][] sum = new BigInteger[10][784];
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 784; j++) {
                    sum[i][j] = new BigInteger(String.valueOf(0));
                }
            }

            int[] count = new int[10];

            String image;
            String tag;
            while( (image = imageReader.readLine() ) != null) {
                tag = tagReader.readLine();
                int index = Integer.parseInt(tag);
                count[index]++;

                String[] pixels = image.split(",");
                for (int i = 0; i < pixels.length; i++) {
                    BigInteger test = new BigInteger(pixels[i]);
                    sum[index][i] = sum[index][i].add(test);
                }
            }

            FileWriter fileWriter = new FileWriter(fileName);

            for (int i = 0; i < 10; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < 784; j++) {
                    sum[i][j] = sum[i][j].divide(new BigInteger(String.valueOf(count[i])));
                    sb.append(sum[i][j]).append(",");
                }
                sb.deleteCharAt(sb.length()-1).append("\n");
                fileWriter.write(sb.toString());
            }

            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

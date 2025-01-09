package com.monsterxia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class ResultA {
    public static void main(String[] args) throws IOException {
        String inputName = "train_img";
        String centerName = "output5";
        String resourcesPath = RandomInit.class.getClassLoader().getResource("train_img").getPath();
        String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/";
        inputName = outputPath + inputName;
        centerName = outputPath + centerName;

        BufferedReader br = new BufferedReader(new FileReader(inputName));
        BufferedReader centerReader = new BufferedReader(new FileReader(centerName));

        BigInteger[][] average = new BigInteger[10][784];

        for (int i = 0; i < 10; i++) {
            String line = centerReader.readLine();
            String[] pixels = line.split(",");
            for (int j = 0; j < pixels.length; j++) {
                average[i][j] = new BigInteger(pixels[j]);
            }
        }

        int[] count = new int[10];

        String image;
        while( (image = br.readLine() ) != null) {
            int tag = closest(image, average);
            count[tag]++;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append("Centroid ").append(i).append(": ").append(count[i]).append(", [");
            for (int j = 0; j < 784; j++) {
                sb.append(average[i][j]).append(",");
            }
            sb.deleteCharAt(sb.length()-1).append("]\n");
        }
        System.out.println(sb);
    }

    public static int closest(String image, BigInteger[][] average) {
        BigInteger[] distance = new BigInteger[10];
        for (int i = 0; i < 10; i++) {
            distance[i] = new BigInteger("0");
        }

        String[] temp = image.split(",");
        for (int i = 0; i < temp.length; i++) {
            BigInteger test = new BigInteger(temp[i]);
            for (int j = 0; j < 10; j++) {
                BigInteger temp2 = average[j][i].subtract(test).abs();
                distance[j] = distance[j].add(
                        temp2.multiply(temp2)
                );
            }
        }

        BigInteger min = distance[0];
        int minIndex = 0;
        for (int i = 1; i < distance.length; i++) {
            if (distance[i].compareTo(min) < 0) {
                min = distance[i];
                minIndex = i;
            }
        }

        return minIndex;
    }
}

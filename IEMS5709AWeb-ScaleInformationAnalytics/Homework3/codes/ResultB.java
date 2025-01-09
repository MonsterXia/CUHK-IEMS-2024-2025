package com.monsterxia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ResultB {
    public static void main(String[] args) throws IOException {
        String inputName = "train_img";
        String tagName = "train_label";
        String centerName = "output5";
        String resourcesPath = RandomInit.class.getClassLoader().getResource("train_img").getPath();
        String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/";
        inputName = outputPath + inputName;
        tagName = outputPath + tagName;
        centerName = outputPath + centerName;

        BufferedReader br = new BufferedReader(new FileReader(inputName));
        BufferedReader tagReader = new BufferedReader(new FileReader(tagName));
        BufferedReader centerReader = new BufferedReader(new FileReader(centerName));

        BigInteger[][] average = new BigInteger[10][784];

        for (int i = 0; i < 10; i++) {
            String line = centerReader.readLine();
            String[] pixels = line.split(",");
            for (int j = 0; j < pixels.length; j++) {
                average[i][j] = new BigInteger(pixels[j]);
            }
        }

        int[][] count = new int[10][10];
        for (int i = 0; i < 10; i++) {
            Arrays.fill(count[i], 0);
        }

        String image;
        String tag;
        while( (image = br.readLine() ) != null) {
            tag = tagReader.readLine();
            int index = Integer.parseInt(tag);
            int predict = closest(image, average);
            count[predict][index]++;
        }

        int totalNum = 0;
        int totalCorrect = 0;
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.00");
        for (int i = 0; i < 10; i++) {
            sb.append("Cluster ").append(i).append(", ");
            int num = 0;
            int max = 0;
            int index = 0;
            for (int j = 0; j < 10; j++) {
                num += count[i][j];
                if (count[i][j] > max) {
                    max = count[i][j];
                    index = j;
                }
            }

            sb.append(index).append(", ").append(num).append(", ").append(max).append(", ");
            double accuracy = (double) 100 * max / num;
            String formattedAccuracy = df.format(accuracy);
            sb.append(formattedAccuracy).append("\n");
            totalNum += num;
            totalCorrect += max;
        }
        sb.append("TotalSet,").append("N/A,").append(totalNum).append(",").append(totalCorrect).append(", ");
        double totalAccuracy = (double) 100 * totalCorrect / totalNum;
        String formattedTotalAccuracy = df.format(totalAccuracy);
        sb.append(formattedTotalAccuracy).append("\n");
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

package com.monsterxia;

import java.io.*;
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {

        String InitName = "RandomInit3";
        String outputName = "output5";
        String inputName = "train_img";
        String tagName = "train_label";

        String resourcesPath = RandomInit.class.getClassLoader().getResource("train_img").getPath();
        String Path = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/";
        InitName = Path + InitName;
        outputName = Path + outputName;

        BigInteger[][] average = new BigInteger[10][784];

        System.out.println("Round <>: D(curr, prev) = <>");

        boolean stop = false;
        int rounds = 1;
        while (!stop){
            try {
                // Read the init
                BufferedReader br;
                if (rounds == 1) {
                    br = new BufferedReader(new FileReader(InitName));
                }else {
                    br = new BufferedReader(new FileReader(outputName));
                }

                for (int i = 0; i < 10; i++) {
                    String line = br.readLine();
                    String[] pixels = line.split(",");
                    for (int j = 0; j < pixels.length; j++) {
                        average[i][j] = new BigInteger(pixels[j]);
                    }
                }

                BigInteger[][] sum = new BigInteger[10][784];
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 784; j++) {
                        sum[i][j] = new BigInteger("0");
                    }
                }
                int[] count = new int[10];

                BufferedReader imageReader = new BufferedReader(new FileReader(Init2.class.getClassLoader()
                                .getResource(inputName).getFile()));
                String image;
                while( (image = imageReader.readLine() ) != null) {
                    int tag = closest(image, average);
                    count[tag]++;

                    String[] pixels = image.split(",");
                    for (int i = 0; i < pixels.length; i++) {
                        BigInteger test = new BigInteger(pixels[i]);
                        sum[tag][i] = sum[tag][i].add(test);
                    }
                }

                FileOutputStream fos = new FileOutputStream(outputName);

                for (int i = 0; i < 10; i++) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < 784; j++) {
                        if (sum[i][j].compareTo(new BigInteger("0")) != 0){
                            sum[i][j] = sum[i][j].divide(new BigInteger(String.valueOf(count[i])));
                        }
                        sb.append(sum[i][j]).append(",");
                    }
                    sb.deleteCharAt(sb.length()-1).append("\n");
                    fos.write(sb.toString().getBytes());
                }

                double temp = 0;
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 784; j++) {
                        double thisTemp = sum[i][j].subtract(average[i][j]).abs().doubleValue();
                        temp += thisTemp * thisTemp;
                    }
                }

                temp = Math.sqrt(temp);

                System.out.println("Round " + rounds + ": D(curr, prev) = " + temp);
                if (temp <= 0.05) {
                    stop = true;
                    System.out.println("Achieve convergence at round = " + rounds);
                }
                rounds++;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
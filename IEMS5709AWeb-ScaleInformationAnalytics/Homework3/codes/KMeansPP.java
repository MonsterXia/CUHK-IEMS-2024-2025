package com.monsterxia;

import java.io.*;

public class KMeansPP {
    public static void main(String[] args) throws IOException {
        String inputName = "train_img";
        String fileName = "RandomInit3";

        String resourcesPath = RandomInit.class.getClassLoader().getResource("train_img").getPath();
        String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/";
        fileName = outputPath + fileName;
        inputName = outputPath + inputName;

        String[] kmpp = new String[10];

        BufferedReader br = new BufferedReader(new FileReader(inputName));
        int index = (int)(Math.random() * 60000);
        for (int i = 0; i < index; i++) {
            String temp = br.readLine();
            if (i == index - 1) {
                kmpp[0] = temp;
            }
        }

        for (int i = 1; i < 10; i++) {
            br = new BufferedReader(new FileReader(inputName));
            String image;
            int maxDistance = Integer.MIN_VALUE;
            String maxString = "";
            while( (image = br.readLine() ) != null) {
                int value = getMin(image, kmpp, i);
                if (value > maxDistance) {
                    maxDistance = value;
                    maxString = image;
                }
            }

            kmpp[i] = maxString;
        }

        FileOutputStream fos = new FileOutputStream(fileName);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(kmpp[i]).append("\n");
        }
        fos.write(sb.toString().getBytes());
    }

    private static int getMin(String image, String[] kmpp, int i) {
        int minDistance = Integer.MAX_VALUE;
        String pixels[] = image.split(",");

        for (int j = 0; j < i; j++) {
            int min = Integer.MAX_VALUE;
            String temp[] = kmpp[j].split(",");
            for (int k = 0; k < pixels.length; k++) {
                int distance = Math.abs(Integer.parseInt(pixels[k]) - Integer.parseInt(temp[k]));
                min += distance * distance;
            }
            if (min < minDistance) {
                minDistance = min;
            }
        }
        return minDistance;
    }
}

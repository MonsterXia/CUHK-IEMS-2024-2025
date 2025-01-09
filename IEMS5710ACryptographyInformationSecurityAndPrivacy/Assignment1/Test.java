package com.monsterxia;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("-----Question4-----");
        // Modulus:
        // 05:6b:c7:ad:4d:a8:33:16:db
        String hexString = "056bc7ad4da83316db";
        BigInteger decimalNumber = new BigInteger(hexString, 16);
        String prime = getPrimePairs(decimalNumber);


        System.out.println("Hexadecimal: " + hexString);
        System.out.println("Decimal: " + decimalNumber);
        System.out.println("Prime: " + prime);

        BigInteger phi = getPhi(prime);
        System.out.println("Phi(N): " + phi);

        BigInteger e = BigInteger.valueOf(65537);
        BigInteger d = e.modInverse(phi);
        System.out.println("d: " + d);

        String encryptedText = "AxMh6FqZ4mdV";
        BigInteger c = getBigInteger(encryptedText);
        System.out.println("c: " + c);

        BigInteger m = c.modPow(d, decimalNumber);
        System.out.println("m: " + m);

        String decryptedText = getText(m);
        System.out.println("decryptedText: " + decryptedText);

        System.out.println("-----Question5-----");
        BigInteger a5 = new BigInteger("2815675175253318914878108460948169305201889736892014759387029406311167");
        BigInteger c5 = new BigInteger("1904728121096264384293052023573590678799868915696638582430846369537791");
        BigInteger m5 = new BigInteger("1984022522177509005484138128176773942914583859539906313397324398933453");
        BigInteger ciphertext=	new BigInteger("1715610578739814070001774693311884433646613212955777636517269434000229");

        System.out.println("gcd(" + c5 + "," + m5 + "} = " + getGCD(c5, m5));

        getFormerN(10, a5, c5, m5, ciphertext);

    }

    private static void getFormerN(int i, BigInteger a5, BigInteger c5, BigInteger m5, BigInteger current) {
        BigInteger a5_ = a5.modInverse(m5);
        BigInteger former = current;

        for (int j = 0; j < i; j++){
            BigInteger target;
            if (former.compareTo(c5) < 0) {
                target = former.add(m5).subtract(c5);
            }else {
                target = former.subtract(c5);
            }
            former = target.multiply(a5_).remainder(m5);
            if (j == i-1){
                System.out.println("The 10th: "+ former);
                System.out.println("Decoded: " + getText(former));
            }
        }
    }

    private static void getNextN(int i, BigInteger a5, BigInteger c5, BigInteger m5, BigInteger current) {
        BigInteger next = current;
        for (int j = 0; j < i; j++) {
            next = a5.multiply(next).add(c5).remainder(m5);
            System.out.println(getText(next));
        }
    }

    public static BigInteger getGCD(BigInteger a, BigInteger b) {
        BigInteger gcd = BigInteger.ZERO;
        if (a.compareTo(b) < 0) {
            BigInteger temp = a;
            a = b;
            b = temp;
        }

        while (b.compareTo(BigInteger.ZERO) != 0) {
            int comparison = b.compareTo(BigInteger.ZERO);
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
        }
        return a;
    }


    public static BigInteger getBigInteger(String text){
        BigInteger temp = new BigInteger(1, Base64.getDecoder().decode(text));
        return temp;
    }

    public static char getChar(char[] a){
        int temp = 0;
        for (char c : a){
            int cInt = Integer.parseInt(String.valueOf(c), 16);
            temp = temp*16 + cInt;
        }
        return (char) temp;
    }

    public static String getText(BigInteger bigInteger){
        String temp = bigInteger.toString(16);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i+1 < temp.length(); i+=2) {
            char c = getChar(temp.substring(i, i + 2).toCharArray());
            sb.append(c);
        }
        return sb.toString();
    }

    public static BigInteger getPhi(String pair) {
        String[] pairs = pair.substring(1, pair.length()-1).split(",");
        BigInteger phi = BigInteger.ONE;
        BigInteger p = new BigInteger(pairs[0]);
        BigInteger q = new BigInteger(pairs[1]);
        phi = phi.multiply(p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)));

        return phi;
    }

    public static String shorGetPrimePairs(BigInteger n) {
        BigInteger prime1 = BigInteger.ZERO;
        BigInteger prime2 = BigInteger.ZERO;
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        while (true){
            // 在2 到 n -1 中随机选择一个数
            BigInteger a = new BigInteger(n.bitLength(), new java.util.Random());
            if (getGCD(a, n).compareTo(BigInteger.ONE) == 0){
                BigInteger t = BigInteger.ONE;
                while (a.modPow(t, n).compareTo(BigInteger.ONE) != 0) {
                    t = t.add(BigInteger.ONE);
                }
                if (t.remainder(new BigInteger("2")).compareTo(BigInteger.ZERO) == 1) {
                    continue;
                }else {
                    BigInteger t_2 = t.divide(new BigInteger("2"));
                    prime1 = getGCD(a.modPow(t_2.subtract(BigInteger.ONE), n), n);
                    prime2 = getGCD(a.modPow(t_2.add(BigInteger.ONE), n), n);
                    sb.append(prime1).append(",").append(prime2);
                    break;
                }
            }else {
                BigInteger x = getGCD(a, n);
                BigInteger result = n.divide(x);
                sb.append(x).append(",").append(result);
                break;
            }



        }

        return sb.toString();
    }
    public static String getPrimePairs(BigInteger n) {
        BigInteger prime1 = BigInteger.ZERO;
        BigInteger prime2 = BigInteger.ZERO;
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        long i = 10000000000L;
        while (i < Long.MAX_VALUE){
            if (isPrime(i)){
                BigInteger remainder = n.remainder(BigInteger.valueOf(i));
                if (remainder.equals(BigInteger.ZERO)) {
                    BigInteger result = n.divide(BigInteger.valueOf(i));
                    sb.append(i).append(",").append(result);
                    break;
                }
            }
            i++;
        }
        sb.append("}");

        return sb.toString();
    }
    public static boolean isPrime(long n) {
        long i = 2L;
        while (i <= n/i) {
            if (n % i == 0) {
                return false;
            }
            i++;
        }
        return true;
    }

}

public class Main {
    public static void main(String[] args) {
        int student_ID = 1155xxxxxx;
        // Size of dataset 1: (Your student ID % 3 + 1) GB
        int size_1 = student_ID % 3 +1;
        // Size of dataset 2: (Your student ID % 20 + 10) GB
        int size_2 = student_ID % 20 +10;
        System.out.println("size_1 = " + size_1); // size_1 = 2
        System.out.println("size_2 = " + size_2); // size_2 = 17
    }
}

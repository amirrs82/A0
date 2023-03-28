import java.util.ArrayList;
import java.util.Scanner;

public class Question3 {
    static int max = -1, xOfOne = 0, yOfOne = 0;
    static ArrayList<Integer> existingNumbers = new ArrayList<>();
    static int numberOfSides;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        numberOfSides = scanner.nextInt();
        numberOfSides++;
        int[][] numbers = new int[2 * numberOfSides - 1][2 * numberOfSides - 1];
        getInput(numbers, scanner);
        long start = System.nanoTime();
        dfs(numbers, xOfOne, yOfOne, 1);
        printOutput(numbers);
        long end = System.nanoTime();
        System.out.println((double) (end - start) / 1000000000);
    }

    public static void printOutput(int[][] arr) {
        int buff = numberOfSides;
        for (int i = 0; i < 2 * numberOfSides - 1; i++) {
            if (i >= numberOfSides) buff -= 2;
            for (int j = 0; j < 2 * numberOfSides - 1; j++) {
                if (!(i == 0 || i == 2 * numberOfSides - 2 || j == 0 || j >= buff + i - 1))
                    System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void getInput(int[][] arr, Scanner scanner) {
        int buff = numberOfSides;
        for (int i = 0; i < 2 * numberOfSides - 1; i++) {
            if (i >= numberOfSides) buff -= 2;
            for (int j = 0; j < 2 * numberOfSides - 1; j++) {
                if (i == 0 || i == 2 * numberOfSides - 2 || j == 0 || j >= buff + i - 1) arr[i][j] = -1;
                else {
                    arr[i][j] = scanner.nextInt();
                    if (arr[i][j] > 0) existingNumbers.add(arr[i][j]);
                    if (arr[i][j] > max) max = arr[i][j];
                }
                if (arr[i][j] == 1) {
                    xOfOne = i;
                    yOfOne = j;
                }
            }
        }
    }

    public static boolean dfs(int[][] numbers, int x, int y, int currentNumber) {
        if (numbers[x][y] == -1) return false;
        if (currentNumber == max && numbers[x][y] == currentNumber) return true;
        if (existingNumbers.contains(currentNumber) && numbers[x][y] != currentNumber) return false;
        if (numbers[x][y] != 0) {
            if (numbers[x][y] != currentNumber) return false;
            return checkDFS(numbers, x, y, currentNumber + 1);
        }
        numbers[x][y] = currentNumber;
        if (checkDFS(numbers, x, y, currentNumber + 1)) return true;
        numbers[x][y] = 0;
        return false;
    }

    public static boolean checkDFS(int[][] numbers, int x, int y, int currentNumber) {
        if (x == numberOfSides - 1) {
            if (dfs(numbers, x, y + 1, currentNumber)) return true;         //right
            if (dfs(numbers, x - 1, y, currentNumber)) return true;         //up right
            if (dfs(numbers, x - 1, y - 1, currentNumber)) return true;  //up left
            if (dfs(numbers, x, y - 1, currentNumber)) return true;         //left
            if (dfs(numbers, x + 1, y, currentNumber)) return true;         //down right
            return dfs(numbers, x + 1, y - 1, currentNumber);            //down left
        } else if (x > numberOfSides - 1) {
            if (dfs(numbers, x, y + 1, currentNumber)) return true;         //right
            if (dfs(numbers, x - 1, y + 1, currentNumber)) return true;  //up right
            if (dfs(numbers, x - 1, y, currentNumber)) return true;         //up left
            if (dfs(numbers, x, y - 1, currentNumber)) return true;         //left
            if (dfs(numbers, x + 1, y, currentNumber)) return true;         //down right
            return dfs(numbers, x + 1, y - 1, currentNumber);            //down left
        } else if (x < numberOfSides - 1) {
            if (dfs(numbers, x, y + 1, currentNumber)) return true;         //right
            if (dfs(numbers, x - 1, y, currentNumber)) return true;  //up right
            if (dfs(numbers, x - 1, y - 1, currentNumber))
                return true;         //up left
            if (dfs(numbers, x, y - 1, currentNumber)) return true;         //left
            if (dfs(numbers, x + 1, y + 1, currentNumber))
                return true;         //down right
            return dfs(numbers, x + 1, y, currentNumber);            //down left
        }
        return false;
    }
}

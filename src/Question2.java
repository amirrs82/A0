import java.util.*;

public class Question2 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int testCases = scanner.nextInt();
        for (int i = 0; i < testCases; i++) {
            int length = scanner.nextInt();
            ArrayList<Integer> numbers = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                numbers.add(j, scanner.nextInt());
            }
            if (removeAndCheckPalindrome(numbers)) System.out.println("YES");
            else System.out.println("NO");
        }
    }

    public static boolean isPalindrome(ArrayList<Integer> number) {
        ArrayList<Integer> copyNumber = new ArrayList<>(number);
        Collections.reverse(number);
        return number.equals(copyNumber);
    }

    public static void removeAll(int key, ArrayList<Integer> arr) {
        ArrayList<Integer> keys = new ArrayList<>();
        keys.add(0, key);
        arr.removeAll(keys);
    }

    public static boolean removeAndCheckPalindrome(ArrayList<Integer> arr) {
        ArrayList<Integer> copyArr = new ArrayList<>(arr);
        for (int j = 0; j < copyArr.size(); j++) {
            if (arr.size() <= 2) return true;
            if (!copyArr.get(j).equals(copyArr.get(copyArr.size() - j - 1))) { // compares first and last then second and one to last ...
                removeAll(copyArr.get(j), copyArr);
                if (isPalindrome(copyArr))
                    return true;
                else {
                    copyArr = new ArrayList<>(arr);
                    removeAll(copyArr.get(copyArr.size() - j - 1), copyArr);
                    return isPalindrome(copyArr);
                }
            }
        }
        return true;
    }
}
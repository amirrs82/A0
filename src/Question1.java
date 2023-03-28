import java.util.*;

public class Question1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        ArrayList<String> split = new ArrayList<>(Arrays.asList(input.split("-")));
        removeDuplicates(split);
        ArrayList<String> copySplit = new ArrayList<>(split);
        copySplit.replaceAll(Question1::sortString);
        for (int i = 0; i < copySplit.size(); i++) {
            System.out.print("*" + split.get(i) + "*");
            for (int j = i + 1; j < copySplit.size(); j++) {
                if (copySplit.get(i).equals(copySplit.get(j))) {
                    System.out.print(" *" + split.get(j) + "*");
                    copySplit.remove(j);
                    split.remove(j);
                    j--;
                }
            }
            System.out.println();
        }
    }

    public static void removeDuplicates(ArrayList<String> split) {

        for (int i = 0; i < split.size(); i++) {
            for (int j = i + 1; j < split.size(); j++) {
                if (split.get(i).equals(split.get(j))) {
                    split.remove(j--);
                }
            }
        }
    }

    public static String sortString(String stringToSort) {
        char[] tempArray = stringToSort.toCharArray();
        Arrays.sort(tempArray);
        return new String(tempArray);
    }
}
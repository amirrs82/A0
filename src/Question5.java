import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question5 {
    static LinkedHashMap<Integer, Customer> customers = new LinkedHashMap<>();
    static LinkedHashMap<Integer, Commodity> commodities = new LinkedHashMap<>();
    static ArrayList<String> categories = new ArrayList<>();

    static class Customer {
        private int balance;
        private final String name;
        private final LinkedHashMap<Integer, Commodity> basket = new LinkedHashMap<>();

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public String getName() {
            return name;
        }

        public LinkedHashMap<Integer, Commodity> getBasket() {
            return basket;
        }

        public Customer(String name) {
            this.name = name;
        }
    }

    static class Commodity {
        private final int price;
        private int inventory;
        public int rateSum, rateCounter;
        private final String name, category;
        private final ArrayList<String> commodityDetails = new ArrayList<>();
        private final ArrayList<String> commodityComments = new ArrayList<>();

        public ArrayList<String> getCommodityDetails() {
            return commodityDetails;
        }

        public ArrayList<String> getCommodityComments() {
            return commodityComments;
        }

        public int getPrice() {
            return price;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public int getInventory() {
            return inventory;
        }

        public void setInventory(int inventory) {
            this.inventory = inventory;
        }

        public Commodity(int price, String name, String category) {
            this.price = price;
            this.name = name;
            this.category = category;
        }
    }

    public static void main(String[] args) {
        ArrayList<String> regexes = new ArrayList<>();
        ArrayList<Pattern> patterns = new ArrayList<>();

        String command;
        Matcher matcher = null;
        boolean found = false;
        int i = 1;

        addRegexes(regexes);

        for (String regex : regexes)
            patterns.add(Pattern.compile(regex));

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().trim().equals("start")) ;

        command = scanner.nextLine().trim();

        while (!command.equals("end")) {
            for (Pattern pattern : patterns) {
                matcher = pattern.matcher(command);
                if (matcher.matches()) {
                    found = true;
                    break;
                }
                i++;
            }
            if (!found) System.out.println("Invalid command");
            else {
                switch (i) {
                    case 1 -> addCustomer(matcher);
                    case 2 -> addCategory(matcher);
                    case 3 -> {
                        boolean detailsExist = matcher.group("details") != null;
                        addCommodity(matcher, detailsExist);
                    }
                    case 4 -> addToCart(matcher);
                    case 5 -> increaseInventory(matcher);
                    case 6 -> changeBalance(matcher);
                    case 7 -> filterCommodity(matcher);
                    case 8 -> {
                        boolean commentExist = matcher.group("comment") != null;
                        boolean rateExist = matcher.group("rate") != null;
                        buyCommodity(matcher, commentExist, rateExist);
                    }
                    case 9 -> commodityInfo(matcher);
                    case 10 -> customerInfo(matcher);
                }
            }
            command = scanner.nextLine().trim();
            found = false;
            i = 1;
        }
    }

    public static void addRegexes(ArrayList<String> regexes) {
        regexes.add("(add\\s+customer)\\s+(?<id>\\d+)\\s+(?<name>\\S+)"); //add customer

        regexes.add("(add\\s+category)\\s+(?<name>\\D+\\d*$)"); // add category

        regexes.add("(add\\s+commodity)\\s+(?<id>\\d+)\\s+(?<category>\\S+)\\s+(?<name>\\S+)" +
                "\\s+(?<price>\\d+)(?<details>(\\s+\\?detail)\\s+\\{.+\\})?"); // add commodity

        regexes.add("(add\\s+to\\s+cart)\\s+(?<id>\\d+)\\s+(?<ids>\\d+(?:,\\d+)*$)"); // add to cart

        regexes.add("(increase\\s+inventory)\\s+(?<id>\\d+)\\s+(?<amount>\\d+)"); // increase inventory

        regexes.add("(change\\s+balance)\\s+(?<id>\\d+)\\s+(?<amount>-?\\d+)"); // change balance

        regexes.add("(filter\\s+commodity)\\s+(?<category>\\S+)\\s+\\(0x(?<lowerBound>[\\dA-F]+)+\\)\\s+to\\s+\\" +
                "(0x(?<upperBound>[\\dA-F]+)+\\)"); // filter commodity

        regexes.add("(buy\\s+commodity)\\s+(?<customerId>\\d+)\\s+(?<commodityId>\\d+)\\s+(?<count>\\d+)" +
                "(?<comment>\\s+\\?comment \\[.+\\])?(?<rate>\\s+\\?rate \\([1-5]\\))?"); // buy commodity

        regexes.add("(commodity\\s+info)\\s+(?<id>\\d+)"); // commodity info

        regexes.add("(customer\\s+info)\\s+(?<id>\\d+)"); // customer info
    }

    public static void addCustomer(Matcher matcher) {
        String name = matcher.group("name");
        int id = Integer.parseInt(matcher.group("id"));
        if (customerIdExists(id)) System.out.println("The id already exists");
        else customers.put(id, new Customer(name));
    }

    public static void addCategory(Matcher matcher) {
        String name = matcher.group("name");
        if (categoryExists(name)) System.out.println("The category already exists");
        else if (categoryFormatValidation(name)) categories.add(name);
    }

    public static void addCommodity(Matcher matcher, boolean detailsExist) { //not working
        int id = Integer.parseInt(matcher.group("id"));
        int price = Integer.parseInt(matcher.group("price"));
        String name = matcher.group("name");
        String category = matcher.group("category");

        if (commodityIdExists(id)) System.out.println("The id already exists");
        else if (!categoryExists(category)) System.out.println("The category does not exist");
        else {
            Commodity commodity = new Commodity(price, name, category);
            commodities.put(id, commodity);
            if (detailsExist) {
                String details = matcher.group("details").trim();
                createDetails(details, commodity);
            }
        }
    }

    public static void addToCart(Matcher matcher) {
        int customerId = Integer.parseInt(matcher.group("id"));
        if (!customerIdExists(customerId)) System.out.println("Customer's id does not exist");
        else {
            ArrayList<Integer> ids = new ArrayList<>(splitIds(matcher.group("ids")));
            Customer customer = customers.get(customerId);
            for (Integer commodityId : ids) {
                if (commodityIdExists(commodityId)) {
                    if (!customerCommodityIdExists(commodityId, customer)) {
                        Commodity commodity = commodities.get(commodityId);
                        customer.basket.put(commodityId, commodity);
                    } else System.out.println("Id " + commodityId + " has already been added");
                } else System.out.println("Id " + commodityId + " does not exist");
            }
        }
    }

    public static void increaseInventory(Matcher matcher) {
        int id = Integer.parseInt(matcher.group("id"));
        int amountToAdd = Integer.parseInt(matcher.group("amount"));

        if (commodityIdExists(id)) {
            Commodity commodity = commodities.get(id);
            int currentInventory = commodity.getInventory();
            commodity.setInventory(currentInventory + amountToAdd);
        } else System.out.println("The id does not exist");
    }

    public static void changeBalance(Matcher matcher) {
        int id = Integer.parseInt(matcher.group("id"));
        int amount = Integer.parseInt(matcher.group("amount"));

        if (customerIdExists(id)) {
            Customer customer = customers.get(id);
            int currentMoney = customer.getBalance();
            customer.setBalance(amount + currentMoney);
        } else System.out.println("The id does not exist");
    }

    public static void filterCommodity(Matcher matcher) {
        String category = matcher.group("category");
        String lowerBound = matcher.group("lowerBound");
        String upperBound = matcher.group("upperBound");
        long lower = convertToHex(lowerBound);
        long upper = convertToHex(upperBound);
        if (!categoryExists(category)) System.out.println("The category does not exist");
        else if (lower >= upper) System.out.println("Invalid bounds");
        else {
            int j = 1;
            for (Commodity commodity : commodities.values()) {
                if (commodity.getCategory().equals(category))
                    if (commodity.getPrice() > lower && commodity.getPrice() < upper) {
                        System.out.println(j + "." + commodity.getName());
                        j++;
                    }
            }
        }

    }

    public static void buyCommodity(Matcher matcher, boolean commentExist, boolean rateExist) {
        int customerId = Integer.parseInt(matcher.group("customerId"));
        int commodityId = Integer.parseInt(matcher.group("commodityId"));
        int count = Integer.parseInt(matcher.group("count"));

        if (!customerIdExists(customerId)) {
            System.out.println("The customer's id does not exist");
            return;
        } else if (!commodityIdExists(commodityId)) {
            System.out.println("The commodity's id does not exist");
            return;
        } else if (!commodityExistsInBasket(customerId, commodityId)) {
            System.out.println("Customer's cart does not include this commodity");
            return;
        }
        Customer customer = customers.get(customerId);
        Commodity commodity = commodities.get(commodityId);

        int currentInventory = commodity.getInventory();
        int price = customer.getBasket().get(commodityId).getPrice() * count;
        int currentBalance = customer.getBalance();

        if (price > currentBalance) System.out.println("Balance is not enough");
        else if (count > currentInventory) System.out.println("Insufficient stock");
        else {
            commodity.setInventory(currentInventory - count);
            customer.setBalance(currentBalance - price);
            customer.getBasket().remove(commodityId);
            if (commentExist) {
                String comments = matcher.group("comment").trim();
                createComments(comments, commodity);
            }
            if (rateExist) {
                String rate = matcher.group("rate");
                commodity.rateSum += (convertRate(rate));
                commodity.rateCounter++;
            }
        }
    }

    public static void commodityInfo(Matcher matcher) { //not correct
        int commodityId = Integer.parseInt(matcher.group("id"));
        if (!commodityIdExists(commodityId)) System.out.println("The id does not exist");
        else {
            Commodity commodity = commodities.get(commodityId);
            System.out.println("category : " + commodity.getCategory());
            System.out.println("name : " + commodity.getName());
            System.out.println("price : " + commodity.getPrice());
            System.out.println("count : " + commodity.getInventory());
            if (!commodity.getCommodityDetails().isEmpty())
                for (String detail : commodity.getCommodityDetails()) System.out.println("detail : " + detail);
            if (commodity.rateCounter != 0)
                System.out.printf("rate : %.2f\n", (double) commodity.rateSum / commodity.rateCounter);
            else System.out.println("rate : 0.00");
            if (!commodity.getCommodityComments().isEmpty())
                for (String comment : commodity.getCommodityComments()) System.out.println("comment : " + comment);
        }
    }

    public static void customerInfo(Matcher matcher) {
        int customerId = Integer.parseInt(matcher.group("id"));
        int i = 1;
        if (!customerIdExists(customerId)) System.out.println("The id does not exist");
        else {
            Customer customer = customers.get(customerId);
            System.out.println("name : " + customer.getName());
            System.out.println("balance : " + customer.getBalance());
            if (!customer.basket.values().isEmpty()) {
                for (Commodity commodity : customer.basket.values()) {
                    System.out.println(i + ".commodity : " + commodity.getName());
                    i++;
                }
            }
        }

    }

    private static void createComments(String comments, Commodity commodity) {
        StringBuilder comment = new StringBuilder();
        for (int i = 9; i < comments.length(); i++) {
            if (comments.charAt(i) == '[') {
                i++;
                while (comments.charAt(i) != ']') comment.append(comments.charAt(i++));
                commodity.commodityComments.add(comment.toString());
                comment = new StringBuilder();
            }
        }
    }

    private static void createDetails(String details, Commodity commodity) {
        StringBuilder comment = new StringBuilder();
        for (int i = 8; i < details.length(); i++) {
            if (details.charAt(i) == '{') {
                i++;
                while (details.charAt(i) != '}') comment.append(details.charAt(i++));
                commodity.commodityDetails.add(comment.toString());
                comment = new StringBuilder();
            }
        }
    }

    public static boolean categoryFormatValidation(String name) {
        if (name.contains(" ")) {
            System.out.println("Invalid command");
            return false;
        } else if (!name.trim().matches("^[a-zA-Z]+\\d*$")) {
            System.out.println("Category format is invalid");
            return false;
        }
        return true;
    }

    public static boolean customerIdExists(int id) {
        for (Integer customerId : customers.keySet())
            if (customerId.equals(id)) return true;
        return false;
    }

    public static boolean commodityIdExists(int id) {
        for (Integer commodityId : commodities.keySet())
            if (commodityId == id) return true;
        return false;
    }

    public static boolean categoryExists(String category) {
        return categories.contains(category.trim());
    }

    public static boolean customerCommodityIdExists(int id, Customer customer) {
        for (Integer commodityId : customer.getBasket().keySet())
            if (commodityId.equals(id)) return true;
        return false;
    }

    public static boolean commodityExistsInBasket(int customerId, int commodityId) {
        return customers.get(customerId).basket.containsKey(commodityId);
    }

    public static Integer convertRate(String rate) {
        return Integer.parseInt(rate.replaceAll("[^0-9]", ""));
    }

    public static ArrayList<Integer> splitIds(String ids) {
        ArrayList<Integer> commodityIds = new ArrayList<>();
        for (String number : ids.split(",")) commodityIds.add(Integer.parseInt(number));
        return commodityIds;
    }

    public static long convertToHex(String number) {
        return HexFormat.fromHexDigitsToLong(number);
    }
}
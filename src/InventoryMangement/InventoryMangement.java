package InventoryMangement;

import java.util.*;

public class InventoryMangement {

    // Item class
    static class Item {
        int id;
        String name;
        String category;
        int quantity;

        public Item(int id, String name, String category, int quantity) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", category='" + category + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    // Inventory Management class
    private final Map<Integer, Item> inventoryMap = new HashMap<>(100000);
    private final Map<String, TreeMap<Integer, Item>> categoryMap = new HashMap<>(100000);
    private final int restockThreshold = 10;
    private int nextId = 1;

    // Add or update an item
    public void addOrUpdateItem(String name, String category, int quantity, Integer id) {
        if (name == null || category == null || quantity < 0) {
            System.out.println("Invalid item details provided. Skipping.");
            return;
        }
        if (id == null) {
            id = nextId++;
        } else {
            removeItem(id);
        }

        Item item = new Item(id, name, category, quantity);
        inventoryMap.put(id, item);
        categoryMap.putIfAbsent(category, new TreeMap<>((id1, id2) -> {
            Item item1 = inventoryMap.get(id1);
            Item item2 = inventoryMap.get(id2);

            int quantity1 = (item1 != null) ? item1.quantity : Integer.MIN_VALUE;
            int quantity2 = (item2 != null) ? item2.quantity : Integer.MIN_VALUE;
            int quantityComparison = Integer.compare(quantity2, quantity1);
            return quantityComparison != 0 ? quantityComparison : Integer.compare(id1, id2);
        }));

        categoryMap.get(category).put(id, item);
        if (quantity < restockThreshold) {
            System.out.println("Restock Notification: " + item);
        }

        System.out.println("Item added/updated successfully.");
    }

    // Remove an item
    public void removeItem(int id) {
        Item item = inventoryMap.remove(id);
        if (item != null) {
            TreeMap<Integer, Item> categoryItems = categoryMap.get(item.category);
            if (categoryItems != null) {
                categoryItems.remove(id);
                if (categoryItems.isEmpty()) {
                    categoryMap.remove(item.category);
                }
            }
            System.out.println("Item with ID " + id + " removed successfully.");
        } else {
            System.out.println("Item with ID " + id + " not found.");
        }
    }

    // Merge another inventory into the current one
    public void mergeInventory(Map<Integer, Item> otherInventory) {
        for (Item otherItem : otherInventory.values()) {
            Item currentItem = inventoryMap.get(otherItem.id);
            if (currentItem == null || otherItem.quantity > currentItem.quantity) {
                addOrUpdateItem(otherItem.name, otherItem.category, otherItem.quantity, otherItem.id);
            }
        }
        System.out.println("Inventories merged successfully.");
    }

    // Display top k items with the highest quantity
    public void displayTopKItems(int k) {
        if (inventoryMap.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        if (inventoryMap.size() < k) {
            System.out.println("We don't have " + k + " items in the inventory. Showing all " + inventoryMap.size() + " items:");
            k = inventoryMap.size();
        }
        PriorityQueue<Item> pq = new PriorityQueue<>(Comparator.comparingInt(item -> item.quantity));
        for (Item item : inventoryMap.values()) {
            pq.offer(item);
            if (pq.size() > k) {
                pq.poll();
            }
        }
        List<Item> topKItems = new ArrayList<>(pq);
        topKItems.sort((item1, item2) -> Integer.compare(item2.quantity, item1.quantity));

        System.out.println("Top " + k + " items with the highest quantity:");
        for (Item item : topKItems) {
            System.out.println(item);
        }
    }

    // Display all items in the inventory
    public void displayAllItems() {
        if (inventoryMap.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        for (Item item : inventoryMap.values()) {
            System.out.println(item);
        }
    }

    // Main method for user interaction
    public static void main(String[] args) {
        InventoryMangement inventory = new InventoryMangement();
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Welcome to the Inventory Management System.");
        System.out.println("Commands: add, update, delete, display, exit, topk");

        while (true) {
            System.out.print("\nEnter a command: ");
            command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "add":
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine();
                    System.out.print("Enter quantity: ");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    inventory.addOrUpdateItem(name, category, quantity, null);
                    break;

                case "update":
                    System.out.print("Enter ID to update: ");
                    int updateId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter new category: ");
                    String newCategory = scanner.nextLine();
                    System.out.print("Enter new quantity: ");
                    int newQuantity = Integer.parseInt(scanner.nextLine());
                    inventory.addOrUpdateItem(newName, newCategory, newQuantity, updateId);
                    break;

                case "delete":
                    System.out.print("Enter ID to delete: ");
                    int deleteId = Integer.parseInt(scanner.nextLine());
                    inventory.removeItem(deleteId);
                    break;

                case "display":
                    inventory.displayAllItems();
                    break;

                case "exit":
                    System.out.println("Exiting Inventory Management System.");
                    scanner.close();
                    return;

                case "topk":
                    System.out.print("Enter the value of k: ");
                    int k = Integer.parseInt(scanner.nextLine());
                    inventory.displayTopKItems(k);
                    break;

                default:
                    System.out.println("Invalid command. Try again.");
                    break;
            }
        }
    }
}

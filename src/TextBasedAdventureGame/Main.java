package TextBasedAdventureGame;

import java.util.*;

public class Main {
    static int x_cordinate=0;
    static int y_cordinate=0;
    static String[][] world= {
            {"Entrance", "Forest", "extraPoints"},
            {"potions", "Dungeon", "Treasure Room"}
    };
    static String[][] room_descriptions={
            {"You are at the Entrance.The Journey begins here","You are in a forest.It feels mysterious.","You are in points room you can get Extra points"},
            {"In this room you can get potions ","You are in a dark Dungeon. Danger lurks here.","You are in the treasure Room. The treasure awaits!"}
    };
    static List<String> inventory=new ArrayList<>(List.of("Sword"));
    static int playerHealth=100;
    static boolean hasTreasure=false;
    static Map<String, String> npcs = new HashMap<>();
    static Map<String, String> npcHints = new HashMap<>();

    public static void main(String[] args) {
        initializeNPCs();
        Scanner sc=new Scanner(System.in);
        System.out.println("Welcome to the Text Adventure Game!");
        System.out.println("Your goal is to reach the treasure Room and collect treasure!..");
        while(playerHealth>0 && !hasTreasure){
            displayRoomDetails();
            List<String> validDirections=getValidDirections();
            validDirections.add("run");
            validDirections.add("attack");
            validDirections.add("check inventory");
            validDirections.add("exist");
            validDirections.add("talk");
            System.out.println("Available directions: "+validDirections);
            System.out.println("\nEnter your move (e.g., go north): ");
            String command=sc.nextLine().trim().toLowerCase();
            if(command.equals("exist")){
                System.out.println("Thank you  for playing! GoodBye!");
                break;
            }
            else if(command.startsWith("go ")) {
                String direction = command.split(" ")[1];
                if (validDirections.contains(direction)) {
                    move(direction);
                } else {
                    System.out.println("You can't go that way!");
                }
            }else if (command.equals("talk")) {
                interactWithNPC();
            } else if(command.equals("check inventory")){
                checkInventory();
            }else if(command.equals("run")){
                flee();
            }else if(command.equals("attack")){
                combat();
            }else{
                System.out.println("Invalid command. Try again");
            }
        }
        if(playerHealth<=0){
            System.out.println("Game Over! You were defeated.");
        }else if(hasTreasure){
            System.out.println("Congratulations! You collected the treasure and won the game :) ");
        }
    }
    private static void displayRoomDetails(){
        System.out.println("\n"+room_descriptions[x_cordinate][y_cordinate]);
        String npc = npcs.get(getCurrentRoom());
        if (npc != null) {
            System.out.println("You see " + npc + " here.");
        }
    }
    private static void initializeNPCs() {
        npcs.put("Forest", "Mysterious Stranger");
        npcHints.put("Mysterious Stranger", "Beware of the Dungeon! But if you're brave, treasures await.");

        npcs.put("Dungeon", "Guard");
        npcHints.put("Guard", "To proceed, you must prove your strength. Fight or flee, but choose wisely.");
    }
    private static void interactWithNPC() {
        String npc = npcs.get(getCurrentRoom());
        if (npc != null) {
            System.out.println(npc + " says: " + npcHints.get(npc));
            if (npc.equals("Mysterious Stranger")) {
                System.out.println("The stranger gives you a Health Potion.");
                inventory.add("Health Potion");
            }
        } else {
            System.out.println("There's no one to talk to here.");
        }
    }
    private static String getCurrentRoom() {
        return world[x_cordinate][y_cordinate];
    }
    private static void move(String direction){
        switch(direction){
            case "north":
                x_cordinate--;
                if("extraPoints".equals(world[x_cordinate][y_cordinate])){
                    playerHealth+=50;
                }else if ("potions".equals(world[x_cordinate][y_cordinate])){
                    inventory.add("medicine");
                }
                break;
            case "south":
                x_cordinate++;
                if("extraPoints".equals(world[x_cordinate][y_cordinate])){
                    playerHealth+=50;
                }else if ("potions".equals(world[x_cordinate][y_cordinate])){
                    inventory.add("medicine");
                }
                break;
            case "west":
                y_cordinate--;
                if("extraPoints".equals(world[x_cordinate][y_cordinate])){
                    playerHealth+=50;
                }else if ("potions".equals(world[x_cordinate][y_cordinate])){
                    inventory.add("medicine");
                }
                break;
            case "east":
                y_cordinate++;
                if("extraPoints".equals(world[x_cordinate][y_cordinate])){
                    playerHealth+=50;
                }else if ("potions".equals(world[x_cordinate][y_cordinate])){
                    inventory.add("medicine");
                }
                break;
        }
        if("Treasure Room".equals(world[x_cordinate][y_cordinate])){
            hasTreasure=true;
        }
    }
    private static void  checkInventory(){
        System.out.println("Inventory: "+ inventory);
    }
    private static void flee(){
        System.out.println("You ran back to a safer spot!");
        x_cordinate=0;
        y_cordinate=0;
    }
    private static List<String> getValidDirections(){
        List<String> directions=new ArrayList<>();
        if(x_cordinate>0 && world[x_cordinate-1][y_cordinate]!=null)directions.add("north");
        if(x_cordinate<world.length-1 && world[x_cordinate+1][y_cordinate]!=null)directions.add("south");
        if(y_cordinate>0 && world[x_cordinate][y_cordinate-1]!=null) directions.add("west");
        if(y_cordinate<world[0].length-1 && world[x_cordinate][y_cordinate+1]!=null) directions.add("east");
        return directions;
    }
    private static void combat(){
        if("Dungeon".equals(world[x_cordinate][y_cordinate])){
            System.out.println("An enemy attacks!");
            int enemyHealth=50;
            Random random=new Random();
            while(enemyHealth>0 && playerHealth>0){
                int playerAttack=random.nextInt(5)+10;
                int enemyAttack=random.nextInt(5)+5;
                enemyHealth-=enemyAttack;
                playerHealth-=playerAttack;
                System.out.println("You deal "+playerAttack+" damage. Enemy health: "+enemyHealth);
                System.out.println("Enemy deals "+ enemyAttack +" damage. your health: "+playerHealth);
                if(playerHealth>0){
                    System.out.println("You defeated the enemy!");
                }else{
                   if(inventory.contains("medicine")){
                       playerHealth+=100;
                       inventory.remove("medicine");
                   }else{
                       System.out.println("You were defeated by the enemy.");
                   }
                }
            }
        }else{
            System.out.println("There's no enemy to fight here.");
        }
    }
}

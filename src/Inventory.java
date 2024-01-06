import java.awt.*;

/*
We implemented the inventory as a simple integer array, giving each item an inventory index
as a sort of key to access a specific point in the array. The array itself simply holds the amount of whatever
that item is
 */
public class Inventory {

    //the inventory itself
    private int[] inventory;

    Player player;

    //this is just where I want to place the inventory when it pops up on the screen
    private static int x = gameTile.size * 4;
    private static int y = gameTile.size * 10;


    //item types are instances of all items in the game. The way the game works is that it does not
    //create a bunch of instances of each item, but instead creates one of each, and keeps track of "how many" there
    //are by updating the corresponding index of inventory.
    private final Storable[] itemTypes = new Storable[8];

    //this is what is drawn to the screen when the inventory is shown, basically the graphical rep of the inventory
    private final ItemSlot[] inventorySlots = new ItemSlot[15];



    public Inventory(Player player){
       inventory = new int[15];
       this.player = player;
       initializeItemTypes();
       initializeInventorySlots();

    }

    //a couple initializers
    public void initializeItemTypes(){
        //these are all the items in the game
        itemTypes[0]= new Carrot();
        itemTypes[1] = new Wheat();
        itemTypes[2] = new Strawberry();
        itemTypes[3] = new Fish();
        itemTypes[4] = new FishingPole();
        itemTypes[5] = new Pumpkin();
        itemTypes[6] = new AnimalTrap();
        itemTypes[7] = new BunnyMeat();
    }
    public void initializeInventorySlots(){
        for(int i = 0; i < inventorySlots.length ;i++){
                inventorySlots[i] = new ItemSlot(x + i * gameTile.size , y, this);
            }
        }

        //uses the inventory index and how many you want to add to change how the value at that position in the inventory
        //say for example you want to add 5 carrots to the inventory, which have an index of 0, all you have
        //to do is call this method with the inputs 0, and 5 and it works, which lets you not have to create a ton of
        //objects
    public void addToInventory(int inventoryIndex, int howMany){

            //basically change the value by howMany, the value at inventory[inventoryIndex] tells
            //me how many there are of that object in the inventory without having to create a bunch of those
            //objects. The index is like a key.
            inventory[inventoryIndex] += howMany;

    }

    public void addObjectExternally(int inventoryIndex, int howMany){
        //only add it to the inventory slots if it is not already there
        inventory[inventoryIndex] += howMany;
        for(ItemSlot inventorySlot : inventorySlots){
            if(inventorySlot.getItem() != null && inventorySlot.getItem().getInventoryIndex() == inventoryIndex){
                break;
            }
            else if (inventorySlot.getItem() == null){
                inventorySlot.setItem(itemTypes[inventoryIndex]);
                break;
            }
        }
    }

    //works the same as addToInventory but in reverse
    public void removeFromInventory(int inventoryIndex){
        inventory[inventoryIndex] --;
    }

    //returns the amount of that item in the inventory, once again accessed using an index, which is unique and assigned
    //to each item
    public int amountOfType(int inventoryIndex){
        return inventory[inventoryIndex];
    }

    //Drawing the inventory. the graphical representation is the inventory slots array, which is an array of item
    //slots, so we actually call the item slot draw method here for each of the slots in the inventory. Then
    //the graphic in UI is drawn, telling the user how to interact with the inventory
    public void draw(Graphics g){

        Color c = new Color(4,6,7,150);
        g.setColor(c);

        for(int i = 0; i < inventorySlots.length;i++){
            inventorySlots[i].draw(g);
        }
        UI.drawInventoryInfo(g);
    }

    //this method is used while interacting with the inventory to determine if
    //the mouse is within the dimensions of the inventory slots on screen, and if so, returning which slot it is actually
    //in
    public ItemSlot getItemSlotAt(double mx, double my){
        //check to see if mouse is in the general inventory slots rectangle, if not get out of method immediately
        if((mx < x) || (mx > x + gameTile.size * inventorySlots.length)  || (my < y) || (my > y + gameTile.size)){
            return null;
        }
        //mouse is in one of the slots
        int slotIndex = ((int)(mx - x)) / 48;
        if(slotIndex < 0 || slotIndex >= inventorySlots.length){
            return null;
        }
        return inventorySlots[slotIndex];
    }


}




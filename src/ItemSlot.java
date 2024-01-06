import java.awt.*;

public class ItemSlot  {

    /*
    So item slots are objects that compose both the inventory and the toolbar. They are
    //largely graphical representations, storing the row and column they are to be drawn in, but they
    also store a storable variable that defines the item that slot is holding. It can be null, which
    simply indicates that the slot is not occupied
     */


    private Storable item;
    private int row;
    private int column;
    Inventory inventory;

    public ItemSlot(int row, int column, Inventory inventory){
        //gives you the upper left corner of the item slot
        //row is the x coordinate and column is the y coordinate of that top left corner
        this.row = row;
        this.column = column;
        this.item = null;
        this.inventory = inventory;

    }

    //this is used during switching to set the item of the slot equal to whatever storable it needs to be
    public void setItem(Storable item){
        this.item = item;
    }
    //some get methods
    public Storable getItem(){
        return this.item;
    }
    public int getRow(){
        return this.row;
    }
    public int getColumn(){
        return this.column;
    }

    //used to compare item slots. The easiest way is to compare locations, since they all have a unique location, across
    //the inventory and the toolbar
    public boolean equals(ItemSlot other){
        return (this.row == other.row) && (this.column == other.column);
    }


    //this draws an individual item slot, a method that is called both when drawing the toolbar and the inventory
    public void draw(Graphics g){
        //draw the background of the slot
        Color c = new Color(4,6,7,150);
        g.setColor(c);
        g.fillRect(row, column, gameTile.size,gameTile.size);
        //draw the outline of the slot

        g.setColor(Color.WHITE);
        g.drawRect(row,column, gameTile.size, gameTile.size);

        //if there is an item within this slot, draw a string representation of how many there are of that item in
        //the inventory, this is just for user visual aid
        if(this.item != null){
            g.drawImage(item.getInventoryImage(), row,column,gameTile.size, gameTile.size,null);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(inventory.amountOfType(item.getInventoryIndex())), row + 5, column + 40);
        }
    }


}

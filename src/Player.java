import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Player extends GameCharacter {

    /*
    the player is the player. It contains a lot of the essential things that make the game playable.
    It is the object that users move around using keys, and it contains a tool bar and an inventory.
     */

    private static int toolbarX = gameTile.size *7;
    private static int toolbarY = gameTile.size*15;


    //the equipped slot is the slot that is selected by the user in the toolbar. If an item is in that slot, it
    //becomes equipped and you can do various thing. For example, if the equipped slot has a fishing pole in it,
    //you can click on a water tile to fish
    public ItemSlot equippedSlot;

    private BufferedImage image;

    private Inventory inventory;


    //level and experience don't do much in this project, but if we had more time,
    //you could use the existing foundation to make a reward system pretty easily
    private int level;
    private int experience;
    private double progressToNextLevel;


    //this is the row of 8 boxes at the bottom of the screen. you can equip on thing in these boxes, and switch
    //items between it and the inventory. It works as an array of itemslots.
    private ItemSlot [] toolbar;


    //different movement frames for the player depending on orientation
    private BufferedImage farmerUp1, farmerDown1,
            farmerRight1,  farmerLeft1,  farmerFishingLeft,
            farmerFishingRight, farmerFishingDown;




    public Player(Game gameManager){

        initializeFrames();

        movementSpeed = 150;
        size = 48;
        halfSize = size/2;
        experience = 0;
        orientation = "down";
        level = 1;
        image = farmerDown1;
        this.gameManager = gameManager;

        inventory = new Inventory(this);
        initializeToolbar();
        //starting position
        px = 500;
        py = 500;
        //starting movement speed
        vx = 0;
        vy = 0;
        characterType = "player";
    }


    //called when you need to check what the item in the equipped slot of the toolbar is
    public Storable getEquippedItem(){
        if(equippedSlot == null){return null;}
        if(equippedSlot.getItem() != null){return equippedSlot.getItem();}
        return null;
    }
    public Inventory getInventory(){
        return this.inventory;
    }
    public ItemSlot[] getToolbar(){return this.toolbar;}


    //this is used when switching items around. This detects if the mouse is within the toolbar,
    //and then if it is, it returns the specific item slot in the tool bar that the mouse is in
    public ItemSlot getToolbarItemSlotAt(double mx, double my){
        //check to see if mouse is in the toolbar rectangle, if not get out of method immediately
        if((mx < toolbarX) || (mx > toolbarX + gameTile.size * toolbar.length)  || (my < toolbarY) || (my > toolbarY + gameTile.size)){
            return null;
        }

        //mouse is in one of the toolbar slots
        int slotIndex = ((int)(mx - toolbarX)) / 48;
        //safety check
        if(slotIndex < 0 || slotIndex >= toolbar.length){
            return null;
        }
        return toolbar[slotIndex];
    }

    public void setEquippedSlot(ItemSlot equippedSlot){
        this.equippedSlot = equippedSlot;
    }

    //not used, but once again with a
    public int getLevel(){
        return this.level;
    }
    public int getExperience(){
        return this.experience;
    }

    //we have it working up to level 4, but it would be very easy to add more levels with a reward system
    public void updateProgressToNextLevelInPercent(){
        this.progressToNextLevel =  (experience/Game.experienceRequiredForNextLevel[level - 1]);
    }
    public void gainExperience(int experience){
        this.experience += experience;
    }
    public boolean levelUp(){
        return this.experience >= Game.experienceRequiredForNextLevel[level-1];
    }


    public void drawLevelBar(Graphics g){
        Color c = new Color(4,6,7,175);
        g.setColor(c);

        //draw the level bar and the two squares containing current level and next level
        g.fillRect(Game.WIDTH/2-100,0,200,30);
        g.fillRect(Game.WIDTH/2-100-30,0,30,30);
        g.fillRect(Game.WIDTH/2+100,0,30,30);

        //draw the bar representing how close you are to the next level!
        g.setColor(Color.ORANGE);
        g.fillRect(Game.WIDTH/2-100, 0, (int)(200* progressToNextLevel), 30);


        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        g.setColor(Color.WHITE);

        //draw the outlining rectangles/sq
        g.drawRect(Game.WIDTH/2-100,0,200,30);
        g.drawRect(Game.WIDTH/2-100-30,0,30,30);
        g.drawRect(Game.WIDTH/2+100,0,30,30);
        g.drawString(String.valueOf(level),Game.WIDTH/2-100-20, 20 );
        g.drawString(String.valueOf(level+1),Game.WIDTH/2+100+10, 20 );
    }


    public void draw(Graphics g){
        //drawn from center
        drawLevelBar(g);

        //the following makes the image one of the fishing frames if the farmer is currently fishing
        //which one it is depends on the orientation
        if(gameManager.gameState == "Fishing"){
            if(orientation == "left"){
                image = farmerFishingLeft;
            }
            else if(orientation == "right"){
                image = farmerFishingRight;
            }
            else if(orientation == "down"){
                image = farmerFishingDown;
            }
            else{
                image = farmerUp1;
            }

        }
        //draws the actual farmer image
        g.drawImage(image,(int)(px - halfSize), (int)(py - halfSize), size, size, null );


        //draws the toolbar
        for(ItemSlot slot : toolbar){
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            slot.draw(g);
            if(equippedSlot!= null && slot.equals(equippedSlot)){
                //if the equipped slot is not null, it becomes highlighted with a blue stroke
                //for the user clarity
                g2.setStroke(new BasicStroke(5));
                g2.setColor(Color.blue);
                g.drawRect(slot.getRow(),slot.getColumn(),gameTile.size,gameTile.size);
                g2.setStroke(new BasicStroke());
            }
        }
    }

    //called within the game's update method at different points depending on gameState. It
    //calls the super method of Game Character which does the physics check to see whether the
    //player can move into a tile or not.
    public void update(double time){
        //this is the physics check
       super.update(time);
       //level system
        updateProgressToNextLevelInPercent();
        if(levelUp()){
            level ++;
            //reset experience to 0 every level up. This allows the progress bar to always
            //start empty when a new level is hit
            experience = 0;
        }
        //update the tile in front of the player, discussed more in TileOperator, but
        //is essential for interaction
        gameManager.tileManager.setCoordinatesOfGameTileInFrontOfPlayer(px,py, orientation);
    }

    //player movement code, overriding Game Character
    @Override
    public void up(){
        super.up();
        image = farmerUp1;

    }
    @Override
    public void down(){
        super.down();
        image = farmerDown1;

    }
    @Override
    public void left(){
        super.left();
        image = farmerLeft1;

    }
    @Override
    public void right(){
        super.right();
        image = farmerRight1;

    }
    public void stopMovingHorizontal(){
        vx = 0;
    }
    public void stopMovingVertical(){
        vy = 0;
    }

    public void initializeToolbar(){
        toolbar = new ItemSlot[8];
        for(int i =0; i < toolbar.length; i++){
            toolbar[i] = new ItemSlot( toolbarX+ i*gameTile.size, toolbarY, inventory);
        }
    }

    //reading in all the images
    public void initializeFrames(){
        try{
            //static down facing
           farmerDown1 = ImageIO.read(getClass().getResourceAsStream("sprites/farmerDown1.png"));

            //static up facing
            farmerUp1 = ImageIO.read(getClass().getResourceAsStream("sprites/farmerUp1.png"));

            //left facing static
            farmerLeft1 = ImageIO.read(getClass().getResourceAsStream("sprites/farmerLeft1.png"));

            //Right facing static
            farmerRight1 = ImageIO.read(getClass().getResourceAsStream("sprites/farmerRight1.png"));

            //left facing fishing action
            farmerFishingLeft = ImageIO.read(getClass().getResourceAsStream("sprites/farmerFishingLeft.png"));
            //right facing fishing action
            farmerFishingRight = ImageIO.read(getClass().getResourceAsStream("sprites/farmerFishingRight.png"));
            //down facing fishing action
            farmerFishingDown = ImageIO.read(getClass().getResourceAsStream("sprites/farmerFishingDown.png"));

        }
        catch (IOException e){}
    }
}

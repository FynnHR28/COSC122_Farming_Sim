import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

//Instructions for use:
/* wasd moves the character around, if you want to open up the inventory, press i. This pauses the game and allows you
to move items around in inventory and tool bar by clicking one item, and then clicking another item slot (which can
be open or occupied). You can close the inventory and resume the game by pressing esc while in this state.
Your toolbar has a bunch of slots, these are accessed by pressing number keys. 0 for the first one, 1 for the second one,
and so on. Doing so equips the item (if there is one) in that toolbar slot. While having an equipped item you can do a few things:
if it is a crop you can plant that crop while clicking on a soil tile while that tile is the one that is in front of the player (
shown by the white rectangle highlight). If you equip the fishing rod you can fish when clicking on a water tile.
Doing so starts a fishing event which continues to update the AI animals and crops growing, but stops the player from being able
to move around while fishing. Additional things include placing animal traps on grass tiles while they are highlighted. If a bunny
walks over this trap a dialogue box pops up telling that you caught a bunny and a new one spawns. You can also customize
the look of the farm by walking into the house and clicking on the table in the middle of it while it is highlighted.
This activates a sort of creative mode where you can move around tree, soil, and grass tiles by clicking on two of them,
causing a swap. Note you cannot swap soil tiles when they have a crop growing in them, nor can you swap any of the other
tiles.

NOTE: We got the method of loading in all the various images used in the project
from this video on Youtube: Reading Resource Files (Images) with IntelliJ IDEA --> it is one line of code, but it is
used extensively throughout the project
NOTE: Every actual image is original artwork done on Photoshop or the website Piskel.com
 */

public class Game extends JPanel implements KeyListener, MouseListener {
    public static final int WIDTH = 1056; //22 48x48  tiles
    public static final int HEIGHT = 768; //16 48x48 tiles

    public static double[] experienceRequiredForNextLevel = new double[3];


    public static final int FPS = 60;

    Player player;

    ArrayList<CropTile> cropTiles = new ArrayList<>();

    TileOperator tileManager;

    private GameEventManager eventManager;

    String gameState;

    FishingEvent fishingEvent;

    //used for switching items around in the inventory/toolbar
    private ItemSlot selectedSlot;
    //used for switching tiles around in customizing mode
    private gameTile selectedGameTile;


    private ArrayList< NPC> npcs = new ArrayList<>();

    //only one dialogue box happens at a time, tells the player different pieces of info
    DialogueBox dialogueBox;

    //used when you have to make a npc despawn (bunny getting caught in a trap)
    private int indexToRemove;


    public Game() {
        //initialize the experience required to get to each level
        //10 needed to get to level 2
        experienceRequiredForNextLevel[0] = 10;
        //15 needed to get to level 3
        experienceRequiredForNextLevel[1] = 15;
        //25 needed to get to level 4
        experienceRequiredForNextLevel[2] = 25;


        gameState = "Menu Screen";

        selectedSlot = null;
        player = new Player(this);

        for(int i = 0; i< 3; i++) {
            npcs.add(new FishAI(this));
            npcs.add(new CowAI(this));
        }
        npcs.add(new BunnyAI(this));


        //We are adding all the items we have to the player's inventory immediately so that you can get a feel
        //for the extent of the activities you can do in the game. If we had time to do more, there would be a
        //reward system that added new items when the player levels up.

        //add 5 carrots to the inventory
        player.getInventory().addObjectExternally(0, 5);
        //add 3 wheat to the inventory
        player.getInventory().addObjectExternally(1,3);
        //add 4 strawberries to inventory
        player.getInventory().addObjectExternally(2,4);
        //add a fish to the inventory
        player.getInventory().addObjectExternally(3,1);
        //add fishing pole to the inventory
        player.getInventory().addObjectExternally(4,1);
        //add a couple pumpkins to inventory
        player.getInventory().addObjectExternally(5,2);
        //add 2 animal traps to the inventory
        player.getInventory().addObjectExternally(6,2);
        //add bunny meat to the inventory
        player.getInventory().addObjectExternally(7,1);


        addKeyListener(this);
        addMouseListener(this);
        tileManager = new TileOperator(this);
        eventManager = new GameEventManager(this, tileManager);
        indexToRemove = -1;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Thread mainThread = new Thread(new Runner());
        mainThread.start();


    }


    //like we did in the homeworks
    class Runner implements Runnable {
        public void run() {

            while (true) {
                update(1 / (double) FPS);
                repaint();
                try {
                    Thread.sleep(1000 / FPS);
                } catch (InterruptedException e) {
                }

            }
        }
    }

    //updates everything in the game. Only certain things are updated depending on the state of the game. For
    //example, during the "Menu Screen" state, nothing is updated.
    public void update(double time){
        //if the game state is "inventory shown" or dialogue box shown, the game is essentially paused, none of these elements update
        //otherwise...

        if(gameState == "Playing"){
            //if the game is currently being played, we want the player, the npcs, and the croptiles to all
            //be updating, we also want to check whether or not the player is entering or exiting the house
            player.update(time);

            for(NPC npc: npcs){
                npc.update(time);
                if(npc.despawn){
                    gameState = "Dialogue Box";
                    indexToRemove = npc.indexOfDespawnedNPC(npcs);
                }
            }
            if(indexToRemove > -1){
                npcs.remove(indexToRemove);
                indexToRemove = -1;
                npcs.add(new BunnyAI(this));
            }

            if(tileManager.enteringHouse(player.px, player.py)){
                tileManager.activeMap = tileManager.indoorGameMap;
                //set player coordinates to right above exit in indoor map
                player.px = 500;
                player.py = 600;
            }
            if(tileManager.exitingHouse()){
                tileManager.activeMap = tileManager.outdoorGameMap;
                //set player coordinates to right under the house tile on the outdoor map
                player.px = 15 * gameTile.size - 24;
                player.py = 6 * gameTile.size;
            }
            for(CropTile c: cropTiles){
                c.update(time);
            }
        }
        else if(gameState == "Fishing"){
            //if the game is in fishing mode, we want everything except the player (they shouldn't be able to move while
            // fishing) to be updating, we also need update the fishing event
            for(CropTile c: cropTiles){
                c.update(time);
            }
            for(NPC npc: npcs){
                npc.update(time);
            }
            fishingEvent.update(time);
            if(fishingEvent.doneFishing){
                //when the fishing game ends, it sets the game's dialogue box to the fishing one, which
                //is then immediately shown following the switching of the gameState to Dialogue Box below
                gameState = "Dialogue Box";
            }

        }

    }


    //Like the update method, draws different things depending on the state. It does not draw the player in the
    //when you are in the menu screen
    public void paintComponent(Graphics g) {
        if(gameState == "Menu Screen"){
            //draw menu screen
            UI.drawMenuScreen(g);
        }

        else if(gameState == "Customizing Mode"){
            //while in customizing mode, the active map has to be the outdoor map, and
            //we only want the map, the customize graphics, and the croptiles to be drawn
            //similarly to the inventory, the selected tile is highlighted if there is one
            tileManager.activeMap = tileManager.outdoorGameMap;
            tileManager.drawMap(g);
            UI.drawCustomizeGraphics(g);
            for(CropTile c: cropTiles){
                c.draw(g);
            }
            if(selectedGameTile!= null){
                UI.highlightSelectedTile( g, selectedGameTile.getRow(), selectedGameTile.getColumn());
            }
        }
        else {

            //if the game state is not the menu screen or the customizing mode,
            //we know we just have to draw whatever the active tile map is, the player, the npcs if they are
            //outdoors, and some UI

            //draw game map and player
            tileManager.drawMap(g);
            if(gameState == "Playing"){UI.drawAccessHelpGraphic(g);}
            //draw crop tiles if they are there
            if(tileManager.activeMap == tileManager.outdoorGameMap){
                for(NPC npc: npcs){
                    npc.draw(g);
                }
                for(CropTile c: cropTiles){
                    c.draw(g);
                }
            }
            player.draw(g);


            //draw the dialogue box if in the right game state
            if(gameState == "Dialogue Box"){
                dialogueBox.draw(g);
            }

            //draw the fishing event if there is one
            if(gameState == "Fishing" && fishingEvent != null){
                fishingEvent.draw(g);
            }

            //draw the inventory if the state is inventory shown
            if(gameState == "Inventory Shown"){
                player.getInventory().draw(g);
                //this highlights the selected slot, which is just for user clarity when switching items around
                if(selectedSlot != null){
                    UI.highlightSelectedItemSlot(g,selectedSlot.getRow(),selectedSlot.getColumn());
                }
            }
            if (gameState == "Help"){UI.drawHelpGraphic(g);}
        }

    }

    //key listener implementation

    public void keyTyped(KeyEvent e){
        //movement
        char c = e.getKeyChar();

            if(gameState == "Playing") {
                //player movement
                if (c == 'w') {player.up();}
                else if (c == 'a') {player.left();}
                else if (c == 's') {player.down();}
                else if (c == 'd') {player.right();}
                //typing 'i' while in "Playing" changes game to inventory mode
                else if(c == 'i'){ gameState = "Inventory Shown";}
                else if(c == 'h'){gameState = "Help";}

                //if you're trying to switch the equipped item in the toolbar
                else if(Character.isDigit(c) && (c-'0' <= 7) && c-'0'>= 0) {
                    //conversion from character to integer
                    //the toolbar have values from 0-7 from left to right, so typing one of these numbers equipps
                    //whatever is in that corresponding slot
                    //(typing 0 equips the far left toobar slot etc.)
                    player.setEquippedSlot(player.getToolbar()[c - '0']);
                }

            }
            else if(c == 27){
                //no matter the gameState you are in, 'esc' is the universal exit key to switch the game back
                //to playing

                //if the game is in Customizing Mode, you also need to reset the map to indoor once they exit
                //that state
                if(gameState == "Customizing Mode"){tileManager.activeMap = tileManager.indoorGameMap;}
                gameState = "Playing";
            }

    }
    public void keyReleased(KeyEvent e){
        //when the key is released it should stop the movement in the respective direction
        char c = e.getKeyChar();
        if (c == 'w'|| c == 's') {player.stopMovingVertical();}
        if (c == 'a' || c == 'd') {player.stopMovingHorizontal();}

    }


    public void addNotify() {
        //needed for key listener to work
        super.addNotify();
        requestFocus();
    }

    //Mouse Listener Methods
    @Override
    //this is the meat of the interactions in the game. You can only interact with the tile in front of the farmer.
    //this means clicking on a tile when it is not highlighted by the white border you see in game will do nothing.

    public void mouseClicked(MouseEvent e) {

        double mx = e.getX();
        double my = e.getY();

        int r = tileManager.getRowTileIsIn(my);
        int c = tileManager.getColumnTileIsIn(mx);

        //the way to get out of a dialogue box is just by clicking anywhere on the screen
        if(gameState == "Dialogue Box"){
            dialogueBox = null;
            gameState = "Playing";
        }

        //if the mouse is clicked within the dimensions of the play button, AND the gameState is currently
        //the menu screen, change the gameState to playing
        else if(eventManager.playButtonClickedInMenuScreen(mx,my) ){
            tileManager.activeMap = tileManager.outdoorGameMap;
            gameState = "Playing";
        }

        //when in customizing mode
        else if(gameState == "Customizing Mode"){
            gameTile clickedTile = tileManager.outdoorGameMap[r][c];

            if(selectedGameTile == null){
                selectedGameTile = clickedTile;
            }
            else{
                //this means you are in the second click, selected slot already has something in it
                //indicating that the player selected an item already
                if(clickedTile != null){
                    // Swap tile objects
                    tileManager.swapTiles(selectedGameTile,clickedTile);
                }
                //reset selected slot to null no matter what after the second click
                selectedGameTile = null;

            }
        }

        else if(gameState == "Playing"){
            //you can only interact with the tile in front of you

            String tileTypeClicked = eventManager.tileTypeClicked(mx,my,r,c);
            Storable equippedItem = player.getEquippedItem();

            //if the mouse is clicked when within the dimensions of the tile in front of you
            // AND the tile in front of you is a soil tile...
                if(tileTypeClicked == "soil") {
                    //if the soil tile is unavailable, you can't plant anything in it, so just return
                    if (tileManager.getGameTileInFrontOfPlayer().getPlantAvailability()) {
                        eventManager.plantCropIfPossible(r, c);
                    } else {
                        //this means that soil tile is not available, so check if it is ready to be harvested
                        //if it is, carry out the harvest action
                        eventManager.harvestCropIfReady(r, c);
                    }
                }


                //if you click a water tile in front of you while you have the fishing pole equipped
                else if(tileTypeClicked == "water" && player.equippedSlot != null &&
                        equippedItem.getType().equals("fishing pole")){
                    fishingEvent = new FishingEvent(this,0.5,r,c);
                    gameState = "Fishing";
                }

                //if you click a grass tile while you have the animal trap equipped and you don't have 0 left,
                //plant that trap in the ground (this changes the grass tile to a trap tile)
                else if(tileTypeClicked == "grass" &&
                        (equippedItem.getType().equals("animal trap")) &&
                        player.getInventory().amountOfType(6) !=0 ){
                         eventManager.placeTrap(r,c);
                }

                //if you click a trap tile, you add it back to the inventory and the tile becomes a grass tile again
                else if (tileTypeClicked == "trap"){
                    eventManager.pickUpTrap(r,c);
                }

                //clicking the creative table changes the game to customizing mode, this can only happen if the creative
                //table is on the screen, which only happens when the active map is the indoor map
                else if(tileTypeClicked == "creativeTable"){
                    gameState = "Customizing Mode";
                }
        }

        //this is the code that allows for moving and switching items around while in inventory mode
        else if (gameState == "Inventory Shown"){
            //is point in a slot in the inventory or toolbar slots?
            //get the identity of the slot clicked, if a slot is already selected, swap the items that are
            //assigned to the two slots and then clear the selected slot variable. No matter what, if a second
            //click happens, or the inventory is exited, have to set selected slot back to null.

            ItemSlot clickedSlot = player.getInventory().getItemSlotAt(mx, my);
            if(clickedSlot == null){
                clickedSlot = player.getToolbarItemSlotAt(mx, my);
            }

            if(selectedSlot == null){
                selectedSlot = clickedSlot;
            }
            else{
               //this means you are in the second click, selected slot already has something in it
               //indicating that the player selected an item already
                if(clickedSlot != null){
                    //do swap
                    Storable temp = selectedSlot.getItem();
                    selectedSlot.setItem(clickedSlot.getItem());
                    clickedSlot.setItem(temp);

                }
                //reset selected slot to null no matter what after the second click
                selectedSlot = null;
            }

        }

    }

    //have to have these implemented to avoid error message, but they are not used
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void keyPressed(KeyEvent e){}


}

import java.awt.*;
import java.util.Random;

public class GameEventManager {

    /*
    This class handles the various game events that happen so that the code within the mouseClicked method
    of game can be more clean and clear. It thus needs instances of the game and the tile operator because it needs
    access to tile and various components of the game like the player
     */
    private TileOperator tileManager;
    private Game gameManager;

    public GameEventManager(Game gameManager, TileOperator tileManager) {
        //needs access to the game and the tiles so because the various events modify them
        this.gameManager = gameManager;
        this.tileManager = tileManager;
    }

    //returns true if the mouse is clicked while within the dimensions of the play button WHILE the game state is
    //menu screen. Used to switch the game to "playing"
    public boolean playButtonClickedInMenuScreen(double mx, double my){
        //the numbers here are tweaked through experimentation such that the click detection is precise
       return  (mx >= (double)gameManager.WIDTH/4.0+130 && mx < (double)gameManager.WIDTH/4.0 + 370) &&
                (my >= 130 && my <= 250 && gameManager.gameState == "Menu Screen");
    }


    //helpful in the mouseClicked method of Game. returns the tile type of the tile that is clicked,
    //once again only if the tile that is clicked is the tile in front of the player
    public String tileTypeClicked(double mx, double my, int r, int c){
        if(tileManager.isPointInGameTileInFrontOfPlayer(mx,my,r,c)){
            return tileManager.getGameTileInFrontOfPlayer().getTileType();
        }
        return null;
    }


    //this is the planting action
    public void plantCropIfPossible(int r, int c) {
        //if the equipped item is null, don't do anything, you need a crop to be equipped in order to plant it

        Storable equippedItem = gameManager.player.getEquippedItem();
        if(equippedItem == null){
            return;
        }

        //you now have to check if the item is a plantable item and that you have
        //enough in the inventory to do so
        if (gameManager.player.getInventory().amountOfType(equippedItem.getInventoryIndex())> 0 && equippedItem.getIsPlantable()) {
            //entering this statement means the planting can commence
            //set the soil tile to unavailable so nothing can be planted over it
            tileManager.getGameTileInFrontOfPlayer().setAvailableToPlant(false);
            //remove one of the item from the inventory
            gameManager.player.getInventory().removeFromInventory(gameManager.player.equippedSlot.getItem().getInventoryIndex());
            //add a crop tile (this is the visual representation of crops growing)
            gameManager.cropTiles.add(new CropTile((Crop) gameManager.player.equippedSlot.getItem(), r, c));
        }
    }


    //harvest crop actions takes the r and c
    public void harvestCropIfReady(int r, int c){
        //check crop in this soil tile
        //you are iterating through crop tiles and if there is a crop tile at these coordinates r,c you check to see
        //if it is set to ready to harvest.
        for(CropTile cropTile : gameManager.cropTiles){

            if(cropTile.isAt(r,c) && cropTile.readyToHarvest()){
                //if it is ready to harvest, remove the crop tile from the croptiles list, and add the crop to the inventory, and
                //add experience to the player's total experience
                gameManager.player.getInventory().addToInventory(cropTile.getCrop().getInventoryIndex(),cropTile.getCrop().getCropYield());
                gameManager.player.gainExperience(cropTile.getCrop().getExperienceYield());
                //set the soil tile back to available so you can plant in it again
                tileManager.getGameTileInFrontOfPlayer().setAvailableToPlant(true);
                gameManager.cropTiles.remove(cropTile);
                break;
            }

        }
    }

    //method to place a trap on the grass, what it is really doing is changing the clicked tile to a trap tile
    //and removing a trap from the inventory
    public void placeTrap(int r, int c){
        gameManager.player.getInventory().removeFromInventory(6);
        tileManager.activeMap[r][c] = new gameTile(true,false,"trap", r,c);
    }

    //does opposite of place trap, adds one trap to the inventory and changes the tile back to a grass one
    public void pickUpTrap(int r, int c){
        gameManager.player.getInventory().addToInventory(6,1);
        tileManager.activeMap[r][c] = new gameTile(true,false,"grass", r,c);
    }


}


 class FishingEvent {
    //fishing event is handled a bit differently that the other events, because it does not happen simulataneously,
     //there is a timer involved
     //You also need the game, and you need which row and column the water tile involved is so that you
     // can draw the timer in the appropriate spot
    private double fishTimeRemaining;
    public boolean doneFishing;

    public double catchChance;

    private int row;
    private int column;
    Game gameManager;
    public FishingEvent(Game gameManager, double catchChance, int r, int c){
        fishTimeRemaining = 5;
        this.gameManager = gameManager;
        doneFishing = false;
        this.row = r;
        this.column = c;
        this.catchChance = catchChance;
    }

    //fishing events are updated using a simple timer
    public void update(double time){
        if(fishTimeRemaining > 0){
            fishTimeRemaining -= time;
            if(fishTimeRemaining <= 0 ){
                //this tells the game to change the state to dialogue box,
                //this tells the player if they caught a fish or not
                doneFishing = true;
                //check to see if a fish is caught
                if(fishCaught(catchChance)){
                    //if so, add one fish to the inventory, and create a new dialogue box saying they
                    //caught a fish
                    gameManager.player.getInventory().addToInventory(3,1);
                    gameManager.dialogueBox = new DialogueBox("You caught a fish! Added to inventory.");
                }
                else{
                    //if they didn't catch one, the message of the dialogue box is negative
                    gameManager.dialogueBox = new DialogueBox("You did not a catch fish. Try Again!");
                }
            }
        }
    }

    //self-explanatory draw method
    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 25));
        g.drawString(String.valueOf((int)fishTimeRemaining),(column * gameTile.size) + 18 , (row * gameTile.size) + 32 );
    }

    //uses randomization and based on the catch chance, which is set to 0.5, returns whether or not they caught
     //a fish
    public boolean fishCaught(double catchChance){
        Random rand = new Random();
        double gotACatch = rand.nextDouble();
        return gotACatch <= catchChance;
    }
}


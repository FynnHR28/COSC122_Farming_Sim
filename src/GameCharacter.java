import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

abstract class GameCharacter {

    //game character makes sense as an abstract class --> all characters, whether that is the player or a type of npc all share
    //these characteristics

    int size;
    Game gameManager;

    String orientation;

    int movementSpeed;

    double px;
    double py;

    int vy;
    int vx;

    int halfSize = size/2;

    BufferedImage image;

    String characterType;



    abstract void draw(Graphics g);

    public void update(double time){
        //this update method is called within all game characters, it basically stores the old coordinates of the
        //character, updates them based on velocity and time, and then checks to see whether the new position
        //depending on the tile it is in, is a valid position, if not, it reverts back to the old position
        //the result is essentially characters not being able to move into tiles that they cannot move into
        double oldPx = px;
        double oldPy = py;

        //update the position
        px += vx * time;
        py += vy * time;

        //check for position's validity BEFORE repainting, if it is not within valid bounds, revert position
        //back to last valid position
        if(!gameManager.tileManager.canPointBeInTile(px,py, this)){
            px = oldPx;
            py = oldPy;
        }

    }

    public void up(){
        vy = -movementSpeed;
        orientation = "up";
    }
    public void down(){
        vy = movementSpeed;
        orientation = "down";
    }
    public void left(){
        vx = -movementSpeed;
        orientation = "left";
    }
    public void right(){
        vx = movementSpeed;
        orientation = "right";
    }

}

class NPC extends GameCharacter implements Automated{
    //NPCs are characters like the player but they do not wait for user input to move,
    //they use randomization to determine the direction of movement
    int movementCounter;
    boolean despawn;
    public NPC(Game gameManager){
        this.gameManager = gameManager;
        movementCounter = 0;
        vx = -50;
        vy = 0;
        orientation = "left";
        despawn = false;
    }
    public void draw(Graphics g){
        g.drawImage(image,(int)(px - halfSize), (int)(py - halfSize), size, size, null );

    }

    @Override
    public void resetMovement(){
        vx = 0;
        vy = 0;
        orientation = "";
    }

    @Override
    //Decide direction uses randomization to determine the next direction of the NPC
    public void decideDirection(){
        Random rand = new Random();
        double whichDirection = rand.nextDouble();
        if(whichDirection <= 0.25){ up(); }
        else if( whichDirection <= 0.5){ down(); }
        else if( whichDirection <= 0.75){ right(); }
        else { left(); }
    }


    public boolean equals(NPC other) {
        return (this.characterType == other.characterType) && (this.px == other.px);
    }

    //this returns the index of the npc that needs to be removed from the NPCs list
    public int indexOfDespawnedNPC(ArrayList<NPC> npcs){

        for(int i = 0; i < npcs.size();i++){
            if(npcs.get(i).equals(this)){
                return  i;
            }
        }
        return 0;
    }

}


class FishAI extends NPC {
    /*The first type of NPC,
    this one cannot move into any tile that isn't water

     */
    private final static BufferedImage up = loadAppropriateImage("Up");
    private final static BufferedImage down = loadAppropriateImage("Down");
    private final static BufferedImage left = loadAppropriateImage("Left");
    private final static BufferedImage right = loadAppropriateImage("Right");

    public FishAI(Game gameManager){
        super(gameManager);
        //fish have their own size, position, and movement speed compared to other NPCs
        size = 32;
        px = 19 * gameTile.size - 24;
        py = 14 * gameTile.size - 24;
        movementSpeed = 40;
        image = left;
        characterType = "fish";

    }

    public void update(double time){

        //decide direction
        //the movement counter essentially controls when the NPC decides a new direction to move in
        //the lower the movement counter threshold (which is 40 for a fish) the more often it will
        //change direction
        if(movementCounter > 40){
            resetMovement();
            decideDirection();
            movementCounter = 0;
        }

        //calling the validity check of the current position of the fish
       super.update(time);
        movementCounter ++;

    }

    //overriding movement methods
    @Override
    public void up(){
        super.up();
        image = up;

    }
    @Override
    public void down(){
        super.down();
        image = down;
    }
    @Override
    public void left(){
        super.left();
        image = left;
    }
    @Override
    public void right(){
        super.right();
        image = right;
    }

    //loads in the fish images, up, left, right, and down
    private static BufferedImage loadAppropriateImage(String orientation){
        try {
             return ImageIO.read(gameTile.class.getResourceAsStream("npcs/underwaterFish" + orientation + ".png"));
        }
        catch(IOException e){

        }
        return null;
    }
}

class CowAI extends NPC {
    //cows only have left and right images
    private final static BufferedImage left = loadAppropriateImage("Left");
    private final static BufferedImage right = loadAppropriateImage("Right");
    public CowAI(Game gameManager){
        super(gameManager);
        size = 48;
        px = 2 * gameTile.size - 24;
        py = 6 * gameTile.size - 24;
        movementSpeed = 25;
        image = left;
        characterType = "cow";
    }

    public void update(double time){
        //decide direction
        //cows move in the same direction for longer than fish because their
        //threshold is larger
        if(movementCounter > 60){
            resetMovement();
            decideDirection();
            movementCounter = 0;
        }

        super.update(time);
        movementCounter ++;

    }

    public void left(){
        super.left();
        image = left;

    }
    public void right(){
        super.right();
        image = right;
    }


    private static BufferedImage loadAppropriateImage(String orientation){
        try {
            return ImageIO.read(gameTile.class.getResourceAsStream("npcs/cow" + orientation + ".png"));
        }
        catch(IOException e){

        }
        return null;
    }

}

class BunnyAI extends NPC {
    /*
    the bunny is very similar to the cow, except for its different speed and size properties, and the update method
    The update method here checks to see whether the bunny walks over a trap or not based on it's position
     */
    private final static BufferedImage left = loadAppropriateImage("Left");
    private final static BufferedImage right = loadAppropriateImage("Right");

    private boolean caught;
    public BunnyAI(Game gameManager){
        super(gameManager);
        size = 48;
        px = 10 * gameTile.size;
        py = 3 * gameTile.size;
        movementSpeed = 40;
        image = left;
        characterType = "bunny";
        caught = false;
    }

    @Override
    public void update(double time){
        //decide direction
        if(movementCounter > 60){
            resetMovement();
            decideDirection();
            movementCounter = 0;
        }


        super.update(time);
        //if the bunny is in a trap tile, set it to despawn, and make a new dialogue box telling the
        //player it caught a bunny
        if(gameManager.tileManager.tileTypePointIsIn(px + 20,py +30) == "trap"){
            despawn = true;
            gameManager.dialogueBox = new DialogueBox("You caught a bunny! Meat added to inventory." );
            gameManager.player.getInventory().addToInventory(7,1);
            gameManager.gameState = "Dialogue Box";
        }

        movementCounter ++;

    }

    @Override
    public void left(){
        super.left();
        image = left;

    }
    @Override
    public void right(){
        super.right();
        image = right;
    }

    private static BufferedImage loadAppropriateImage(String orientation){
        try {return ImageIO.read(gameTile.class.getResourceAsStream("npcs/bunny" + orientation + ".png"));}
        catch(IOException e){}

        return null;
    }



}

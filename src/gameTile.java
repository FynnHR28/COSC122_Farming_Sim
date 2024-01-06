
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class gameTile {
    /*
    Game tiles provide both graphical representation of a game map, and a way to retrieve locational information
    for various game components.The game map is made up of gametiles, which have various properties
     */

    //images for all the gameTiles
    private static BufferedImage grass, tree, soil, water, horizontalFence, verticleFence,
            house, interiorFloor,interiorWall, trap, creativeTable;
    private BufferedImage image;
    public final static int size = 48;


    //describes whether the tile can be walked through or not
    private boolean traversible;

    //the type of tile is needed for actions like planting and fishing
    //if it is of type water, you can fish in it
    private String type;

    //for soil tiles, can only plant in a tile if it is available
    private boolean availableToPlant;


    //locational information is necessary
    private int row;
    private int column;


    public gameTile(boolean traversible, boolean availableToPlant, String type, int r, int c){


        this.traversible = traversible;

        this.row = r;
        this.column = c;

        this.type = type;
        this.availableToPlant = availableToPlant;

        if(this.type.equals("soil")){
            this.image = soil;
        }
        else if(this.type.equals("grass")){
            this.image = grass;
        }
        else if(this.type.equals("tree")){
            this.image = tree;
        }
        else if(this.type.equals("water")){
            this.image = water;
        }
        else if(this.type.equals("horizontalFence")){
            this.image = horizontalFence;
        }
        else if(this.type.equals("verticalFence")){
            this.image = verticleFence;
        }
        else if(this.type.equals("house")){
            this.image = house;
        }
        else if(this.type.equals("interiorFloor")){
            this.image = interiorFloor;
        }
        else if(this.type.equals("interiorWall")){
            this.image = interiorWall;
        }
        else if (this.type.equals("trap")){
            this.image = trap;
        }
        else if (this.type.equals("creativeTable")){
            this.image = creativeTable;
        }


    }

    //determines whether the tile in question is at the given coordinates (r,c)
    public boolean isAt(int r, int c){
        return (this.row == r) && (this.column == c);
    }

    //this loads in all the images for each tile, once it is called once, it won't load in again, so it saves
    //a lot of time
    private static void initializeGameTileImages(){
       if(grass != null) return;
        try {
            grass = ImageIO.read(gameTile.class.getResourceAsStream("tiles/grass.png"));
            tree = ImageIO.read(gameTile.class.getResourceAsStream("tiles/tree.png"));
            soil = ImageIO.read(gameTile.class.getResourceAsStream("tiles/soil.png"));
            water = ImageIO.read(gameTile.class.getResourceAsStream("tiles/water.png"));
            horizontalFence = ImageIO.read(gameTile.class.getResourceAsStream("tiles/horizontalFence.png"));
            verticleFence = ImageIO.read(gameTile.class.getResourceAsStream("tiles/verticalFence.png"));
            house = ImageIO.read(gameTile.class.getResourceAsStream("tiles/house.png"));
            interiorFloor = ImageIO.read(gameTile.class.getResourceAsStream("tiles/interiorFloor.png"));
            interiorWall = ImageIO.read(gameTile.class.getResourceAsStream("tiles/interiorWall.png"));
            trap = ImageIO.read(gameTile.class.getResourceAsStream("tiles/trap.png"));
            creativeTable = ImageIO.read(gameTile.class.getResourceAsStream("tiles/creativeTable.png"));

        }
        catch(IOException e){

        }
    }

    //a bunch of get methods here
    public boolean isTraversible() {return traversible;}
    public String getTileType(){return type;}
    public BufferedImage getImage(){return image;}
    public int getRow(){return row;}
    public int getColumn(){return column;}
    public boolean getPlantAvailability(){return this.availableToPlant;}

    //used if you want to change the image of the tile
    public void setImage(BufferedImage img){this.image = img;}
    //changes a tiles planting availability
    public void setAvailableToPlant(boolean availableToPlant){this.availableToPlant = availableToPlant;}


    //We needed this separate create tile method from the constructor, because this is called when
    //creating a tile map from an integer map. The integers correspond to various tile types.
    public static gameTile createTile(int type, int r, int c){
        initializeGameTileImages();
        if(type == 0 ){
            //grass is 0
            return new gameTile(true,false,"grass", r, c );
        }
        else if(type == 1){
            //trees are 1
            return new gameTile(false, false,"tree", r, c);
        }
        else if (type == 2){
            //soil is 2
            return new gameTile(true, true, "soil", r, c);
        }
        else if (type == 3){
            //water is 3
            return new gameTile(false, false, "water", r, c);
        }
        else if(type == 4){
            //horizontal fence is type 4
            return new gameTile(false,false,"horizontalFence", r ,c);
        }
        else if(type == 5){
            //house is type 5
            return new gameTile(true,false,"house", r ,c);
        }
        else if(type == 6){
            //interior floor is type 6
            return new gameTile(true,false,"interiorFloor", r ,c);
        }
        else if(type == 7){
            //interior wall is type 7
            return new gameTile(false,false,"interiorWall", r ,c);
        }
        else if(type == 8){
            //vertical fence is type 8
            return new gameTile(false,false, "verticalFence", r ,c);
        }
        else if(type == 9){
            return new gameTile(true, false,"trap", r, c);
        }
        else if(type == 10){
            return new gameTile(false, false,"creativeTable", r, c);
        }

        return null;
    }

    public void draw(Graphics g){
        g.drawImage(this.image,column* gameTile.size, row * gameTile.size,gameTile.size,gameTile.size, null);

    }

}


/*
croptiles show the planting actions and the growth of the plants graphically.
The game has a arraylist of croptiles, and each time a plant is planted in a soil tile,
a croptile is drawn above that soil tile. Crop tiles have a growing and harvestable image, which show the plant
growing and being done growing respectively. Once a croptile is done growing and is clicked, it is removed from
the arraylist and thus is not drawn anymore, leaving only that soil tile left, giving the impression that you just harvested
a crop.
 */
class CropTile extends gameTile {

    //each croptile logically holds a specific crop, this is the one that the
    //player planted inside it.
    private Crop crop;
    //each croptile has a growing time and once that is done, it can be harvested, this variable is modified
    //in the update method
    private double growTimeRemaining;

    public CropTile(Crop crop, int r, int c){
        //first creates a tile that is traversible at not able to be planted in at the specified location r,c
        super(true,false,crop.getType(), r, c);
        this.crop = crop;
        //sets the grow time to the specific grow time of the crop planted
        this.growTimeRemaining = this.crop.getGrowingTime();
        //changes the image to the growing image associated with that crop, which is
        //loaded in in that crop's constructor
        setImage(crop.getGrowingImage());
    }

    //update is called, once the timer hits 0,
    // the image is changed to the harvest image for that crop, and it is ready to
    //be removed
    public void update(double time){
        if(growTimeRemaining > 0){
            growTimeRemaining -= time;
            if(growTimeRemaining <= 0 ){
                setImage(crop.getHarvestImage());
            }
        }
    }

    public boolean readyToHarvest(){
        return growTimeRemaining <=0;
    }

    public Crop getCrop(){
        return crop;
    }



}







import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface Storable{

    /*
    storable is the main interface used in this project. Every storable shares these behaviors,
    but are not necessarily related in the way that class hierarchy would be effective in representing, so this
    is a logical use of interfaces
    as we see in the inventory itemtypes, all items are storables. you can see all these storables in the inventory upon starting
    the game
     */

    //pretty self explanatory get methods, but different storables implement them in differnt ways
    String getType();
    BufferedImage getInventoryImage();
    int getInventoryIndex();
    boolean getIsPlantable();

}



class FishingPole implements Storable{
    //a storable tool, use it to fish.
    private double catchChance;
    private BufferedImage inventoryImage;

    public FishingPole(){
        this.catchChance = 0.2;
        try{
            inventoryImage = ImageIO.read(getClass().getResourceAsStream("storableObjects/fishingPole.png"));
        }
        catch (IOException e){}
    }

    //you can see here how the overriding methods change as a function of what storable this is
    @Override public String getType() {return "fishing pole";}

    @Override public BufferedImage getInventoryImage() {return inventoryImage;}

    @Override public int getInventoryIndex() {return 4;}

    @Override public boolean getIsPlantable() {return false;}
    public double getCatchChance(){return catchChance;}

}
class AnimalTrap implements Storable{
    private BufferedImage inventoryImage;

    public AnimalTrap(){
        try{
            inventoryImage = ImageIO.read(getClass().getResourceAsStream("storableObjects/animalTrap.png"));
        }
        catch (IOException e){}
    }

    @Override public String getType() {return "animal trap";}

    @Override public BufferedImage getInventoryImage() {return inventoryImage;}

    @Override public int getInventoryIndex() {return 6;}

    @Override public boolean getIsPlantable() {return false;}


}

class Meat implements Storable{
    private int inventoryIndex, experienceYield;
    private String type;
    private BufferedImage inventoryImage;

    public Meat(String type, int inventoryIndex, int exYield, String invImgPath){
        this.type = type;
        this.inventoryIndex = inventoryIndex;
        this.experienceYield = exYield;

        try{
            inventoryImage = ImageIO.read(getClass().getResourceAsStream(invImgPath));
        }
        catch (IOException e){

        }
    }

    @Override public String getType() {return type;}
    @Override public BufferedImage getInventoryImage() {return inventoryImage;}
    @Override public int getInventoryIndex() {return inventoryIndex;}
    @Override public boolean getIsPlantable() {return false;}

    public int getExperienceYield(){return experienceYield;}

}

//the following two classes extend meat and just call the super instructor with different parameters, according to
//the qualities of those classes
class BunnyMeat extends Meat{
    public BunnyMeat(){
        super("bunnyMeat",7,1,"storableObjects/bunnyMeat.png");
    }
}

class Fish extends Meat{
    public Fish(){
        super("fish",3,5,"storableObjects/fish.png");
    }
}


class Crop implements Storable{
    /*
    crops are storables with a few new properties including growing time crop yield, and two new images,
    growing and harvest
     */
    private int growingTime, cropYield, experienceYield, inventoryIndex;
    private String type;

    private BufferedImage inventoryImage, growingImage, harvestImage;


    //This crop constructor allows sub classes to call this class and simply change the inputs, making code clean
    //and clear
    public Crop(String type, int inventoryIndex, int growT, int cropYield, int exYield, String invImgPath,
                String growImgPath, String harvImgPath){
        this.type = type;
        this.inventoryIndex = inventoryIndex;
        this.growingTime = growT;
        this.cropYield = cropYield;
        this.experienceYield = exYield;

        try{
            inventoryImage = ImageIO.read(getClass().getResourceAsStream(invImgPath));
            growingImage = ImageIO.read(getClass().getResourceAsStream(growImgPath));
            harvestImage = ImageIO.read(getClass().getResourceAsStream(harvImgPath));
        }
        catch (IOException e){

        }
    }


    @Override public String getType() {return type;}

    @Override public int getInventoryIndex() {return inventoryIndex;}

    @Override public boolean getIsPlantable() {return true;}
    @Override public BufferedImage getInventoryImage(){return inventoryImage;}

    //additional behaviors unique to crops that not all storables share
    public int getGrowingTime(){return growingTime;}
    public int getCropYield(){return cropYield;}
    public int getExperienceYield(){return experienceYield;}
    public BufferedImage getGrowingImage(){return growingImage;}
    public BufferedImage getHarvestImage(){return harvestImage;}

}

//The following are a bunch of storables that all extend crop, and differ as a function of their inputs
class Carrot extends Crop{
    public Carrot(){
        super("carrot", 0,25,3,1,"storableObjects/carrot.png",
                "tiles/carrotGrowing.png", "tiles/carrotReadyToHarvest.png");
    }
}

class Wheat extends Crop{
    public Wheat(){
        super("wheat", 1,10,3,1,"storableObjects/wheat.png",
                "tiles/wheatGrowing.png", "tiles/wheatReadyToHarvest.png" );
    }
}

class Strawberry extends Crop{
    public Strawberry(){
        super("strawberry",2,45,2,4,"storableObjects/strawberry.png",
                "tiles/strawberriesGrowing.png", "tiles/strawberriesReadyToHarvest.png");
    }
}


class Pumpkin extends Crop{
    public Pumpkin(){
        super("pumpkin", 5,60,2,6, "storableObjects/pumpkin.png",
                "tiles/pumpkinGrowing.png", "tiles/pumpkinsReadyToHarvest.png");
    }
}


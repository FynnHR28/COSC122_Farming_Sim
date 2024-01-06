import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileOperator {

    /*This class is all about tiles, it contains the various maps that are drawn to the screen, and controls the
    the very important gameTileInFrontOfPlayer. The player can only interact with this tile, and this class sets
    tile every time the player is updated which is 60 times per second. It also contains methods for figuring out
    whether a point can be in a tile on the screen, what type that tile is, and so on. These methods are essential for
    knowing where the user clicked and what they clicked on.

     */

    //NOTE: the loadIntegerMapFromTextFile method was found from a source online, this source is
    //Java 2D Tile Engine Basics: Loading Map from Text File.


    int[][] integerOutdoorMap = new int[Game.HEIGHT/gameTile.size][Game.WIDTH/gameTile.size];
    int[][] integerIndoorMap = new int[Game.HEIGHT/gameTile.size][Game.WIDTH/gameTile.size];

    gameTile [][] outdoorGameMap = new gameTile[Game.HEIGHT/gameTile.size][Game.WIDTH/gameTile.size];
    gameTile [][] indoorGameMap = new gameTile[Game.HEIGHT/gameTile.size][Game.WIDTH/gameTile.size];


    //this is the map that is currently "active" on the screen. if a player goes inside the house, the active
    //map becomes the indoorGameMap. The Tileoperator draws whatever is the active map when its draw method is called
    gameTile [][] activeMap;


    private int gameTileInFrontOfPlayerColumn;
    private int gameTileInFrontOfPlayerRow;

    //this is needed for player object interaction mechanics in the game
    private gameTile gameTileInFrontOfPlayer;

    //this class needs access to the game.
    private Game gameManager;
    public TileOperator(Game gameManager){
        loadIntegerMapFromTextFile("outdoor");
        loadIntegerMapFromTextFile("indoor");
        initializeTileMap("outdoor");
        initializeTileMap("indoor");
        this.gameManager = gameManager;
        activeMap = outdoorGameMap;
    }



    //method for other classes to retrieve the game tile in front of the player, which once again is used for
    //most activities in the game
    public gameTile getGameTileInFrontOfPlayer() {
        return gameTileInFrontOfPlayer;
    }

    //this method is called within the player class's update method, which means the coordinates of the
    //game tile in front of the player is updated simultaneously with player movement, which is what is
    //necessary
    public void setCoordinatesOfGameTileInFrontOfPlayer(double px, double py, String orientation){
        int r = getRowTileIsIn(py);
        int c = getColumnTileIsIn(px);


        //the coordinates depend on the orientation of the player, depending on which way they are facing, it could be 1 of 4
        //possibilities
        if(orientation == "down"){
            gameTileInFrontOfPlayerRow = r + 1;
            gameTileInFrontOfPlayerColumn = c;

        }
        else if(orientation == "up"){
            gameTileInFrontOfPlayerRow = r - 1;
            gameTileInFrontOfPlayerColumn = c;
        }
        else if(orientation == "right"){
            gameTileInFrontOfPlayerRow = r;
            gameTileInFrontOfPlayerColumn = c + 1;
        }
        else if(orientation == "left"){
            gameTileInFrontOfPlayerRow = r;
            gameTileInFrontOfPlayerColumn = c - 1 ;
        }

        //once you figure out what the row and column of the tile should be, actually set it
        setGameTileInFrontOfPlayer();

    }

    public void setGameTileInFrontOfPlayer(){
        //only set the tile if it is within the player map, else make it null. This was initially a bug that froze the game
        //if the tile went beyond the map, so adding the following if statement fixed that
        if(gameTileInFrontOfPlayerRow < 0 || gameTileInFrontOfPlayerRow > 15 || gameTileInFrontOfPlayerColumn < 0
                ||gameTileInFrontOfPlayerColumn > 21){
            gameTileInFrontOfPlayer = null;
        }
        else{
            gameTileInFrontOfPlayer = activeMap[gameTileInFrontOfPlayerRow][gameTileInFrontOfPlayerColumn];
        }

    }


    //gets the row a point is in based on a point. This is useful for mouse interaction stuff
    public int getRowTileIsIn(double py){
        return (int)(py/gameTile.size);
    }

    //same as above
    public int getColumnTileIsIn(double px){
        return (int)(px/gameTile.size);
    }

    //this method is used once, but it is essential. you can only interact with the tile that is the
    //gameTileInFrontOfPlayer, so this method takes the mouse coordinates, and checks to see if it is within
    //the bounds of that tile
    public boolean isPointInGameTileInFrontOfPlayer(double mx, double my, int r, int c){
        return (mx >= c * gameTile.size) && (mx <= (c * gameTile.size) + gameTile.size) && (my >= r * gameTile.size)
                && (my <= (r * gameTile.size) + gameTile.size);
    }


    /*This method is the "physics" of this game. Every tile has a traversable quality. This
    method, when called in the update methods of various game characters, checks to see whether that character
    can be in that tile or not. Note that different characters have different tiles they can or can't be in, and those
    distinctions are made within this method depending on the character type of that entity
     */

    public boolean canPointBeInTile(double px, double py, GameCharacter character){
        //get the tile the point is in
        int r = getRowTileIsIn(py);
        int c = getColumnTileIsIn(px);

        //a bit of finicky math to make the bunny's movement look more convincing
        if(character.characterType == "bunny"){
            if(character.orientation == "right"){
                c = getColumnTileIsIn(px + 32);
            }
            if(character.orientation == "down"){
                r = getRowTileIsIn(py + 40);
            }
        }

        //return true if the point is within bounds and the tile it is in is traversable for that Specific type
        //of gameCharacter
        //The only character that has a different type of movement/ traversable is a fish

        if(character.characterType == "fish"){
            if(character.orientation == "right"){
                c = getColumnTileIsIn(px + 32);
            }
            if(character.orientation == "down"){
                r = getRowTileIsIn(py + 20);
            }
            //for a fish, it can only be in water, so it looks a bit different than if it is not a fish
            return ((r >= 0) && (r <= 15) && (c >= 0) && (c <= 21) && activeMap[r][c].getTileType() == "water");
        }
        else{
            //return true if the point is within bounds and the tile it is in is traversible
            return ((r >= 0) && (r <= 15) && (c >= 0) && (c <= 21) && activeMap[r][c].isTraversible());
        }
    }

    public String tileTypePointIsIn(double px, double py){
        int r = getRowTileIsIn(py);
        int c = getColumnTileIsIn(px);

        return activeMap[r][c].getTileType();
    }



    //checks to see if the player enters the house based on the coordinates, and changes the active map
    //to indoor if needed
    public boolean enteringHouse(double px, double py){
        //get the tile the point is in
        int r = getRowTileIsIn(py);
        int c = getColumnTileIsIn(px);
        //player enters the house once they step on the house tile in the outdoor map
        return (activeMap == outdoorGameMap && activeMap[r][c].getTileType() == "house");
    }

    //checks to see if the player exits the house based on the coordinates of gameTileInFrontOfPlayer,
    // and changes the active map to outdoor if needed
    public boolean exitingHouse(){
        //get the tile the point is in
        //player is exiting the house if they are in the indoor map and the tile in front of them is the door tile
        return (activeMap == indoorGameMap && gameTileInFrontOfPlayerRow == 13 && gameTileInFrontOfPlayerColumn == 10);
    }

    //This method is used during Customizing Mode. It basically allows you to only switch soil, grass, and tree tiles
    //around, based on the clicks that you made.
    public void swapTiles(gameTile selectedTile, gameTile clickedTile){

        int clickedType;
        int selectedType;

        //don't do anything if the selected tiles are not grass, tree, or soil, you can't swap any other tiles
        if((selectedTile.getTileType()!= "grass" && selectedTile.getTileType() != "soil" && selectedTile.getTileType() != "tree") ||
                (clickedTile.getTileType()!= "grass" && clickedTile.getTileType() != "soil" && clickedTile.getTileType() != "tree")){return;}

        if(selectedTile.getTileType() == "grass"){clickedType = 0;}
        else if(selectedTile.getTileType() == "tree"){clickedType = 1;}
        else{clickedType = 2;}

        if(clickedTile.getTileType() == "grass"){selectedType = 0;}
        else if(clickedTile.getTileType() == "tree"){selectedType = 1;}
        else{selectedType = 2;}

        for(CropTile cropTile: gameManager.cropTiles){
            //if a crop is planted in the soil tile at either location, it cannot be moved
            //you can't just transplant a crop like that.
            if(cropTile.isAt(clickedTile.getRow(),clickedTile.getColumn())){
                return;
            }
            else if (cropTile.isAt(selectedTile.getRow(),selectedTile.getColumn())){
               return;
            }
        }
        //this is the swap code. It is actually creating new tiles for each clicked tile based on what type the other was.
        //for example, if the clicked tile was a grass, the coordinates of the selected tile in the map becomes a grass tile.
        outdoorGameMap[selectedTile.getRow()][selectedTile.getColumn()] = gameTile.createTile(selectedType,selectedTile.getRow(),selectedTile.getColumn());
        outdoorGameMap[clickedTile.getRow()][clickedTile.getColumn()] = gameTile.createTile(clickedType,clickedTile.getRow(),clickedTile.getColumn());


    }



    public void loadIntegerMapFromTextFile(String whichMap){
        //this basically allows me to create a map of my choosing, and then apply it to the actual map that is
        //composed of gameTiles

        //source --> snippet of code from online youtube video: Java 2D Tile Engine Basics: Loading Map from Text File
        String fileName = " ";

        if(whichMap == "outdoor"){fileName = "maps/outdoorGameMap.txt";}
        else if(whichMap == "indoor"){fileName = "maps/indoorGameMap.txt";}

        try{
            InputStream is = getClass().getResourceAsStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while(col < 22 && row < 16){
                String line = br.readLine();

                while(col < 22){
                    String[] numbers = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    if(whichMap == "outdoor"){integerOutdoorMap[row][col] = num;}
                    else if(whichMap == "indoor"){integerIndoorMap[row][col] = num;}
                    col++;
                }
                if(col == 22){
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (IOException e) {

        }

    }

    public void initializeTileMap(String whichMap){
        //using the integer map, you can make the tile map by using the create tile method of gameTile, which
        //creates a tile using an integer to represent the tile type, and a position (r,c)
        if(whichMap == "outdoor") {
            for (int r = 0; r < outdoorGameMap.length; r++) {
                for (int c = 0; c < outdoorGameMap[r].length; c++) {
                    //create tile takes an integer and creates a new tile with the desired type that corresponds to that
                    //integer
                    outdoorGameMap[r][c] = gameTile.createTile(integerOutdoorMap[r][c], r, c);
                }
            }
        }
        else if(whichMap == "indoor"){
            for (int r = 0; r < indoorGameMap.length; r++) {
                for (int c = 0; c < indoorGameMap[r].length; c++) {
                    //create tile takes an integer and creates a new tile with the desired type that corresponds to that
                    //integer
                    indoorGameMap[r][c] = gameTile.createTile(integerIndoorMap[r][c], r, c);
                }
            }
        }

    }

    //this simply iterates through whatever is the active map, and draws it
    //note that once it gets to the row and column that match the coordinates of the game tile
    //in front of the player, it highlights it so the user can see
    public void drawMap(Graphics g){
        if(activeMap == null){return;}
        for (int r = 0; r < activeMap.length; r++) {
            for (int c = 0; c < activeMap[r].length; c++) {
               activeMap[r][c].draw(g);
               if(r == gameTileInFrontOfPlayerRow && c == gameTileInFrontOfPlayerColumn){
                   //highlight game tile in front of player for user
                   g.setColor(Color.WHITE);
                   g.drawRect(c* gameTile.size, r * gameTile.size,gameTile.size,gameTile.size);
               }
            }
        }

    }
}

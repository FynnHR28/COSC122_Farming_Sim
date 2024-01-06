import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class UI {

    /*
    This class is a selection of static methods that the game calls at various points to draw various elements
    We thought it would be cleaner for these methods to all be in one place under the user interface umbrella.
    All the images here are original.
     */
    public static int WIDTH = Game.WIDTH;
    public static int HEIGHT = Game.HEIGHT;
    private static BufferedImage playButton, customize, customizeESC, inventoryInfo,menuScreen, helpInfo, accessHelp;


    public static void drawPlayButton(Graphics g){
        loadImgs();
        g.drawImage(playButton,WIDTH/4 + 130 ,80,240,240,null);

    }

    public static void drawCustomizeGraphics(Graphics g){
        loadImgs();
        g.drawImage(customize,-5 ,-90,240,240,null);
        g.drawImage(customizeESC, WIDTH-235,HEIGHT-180,240,240,null);
    }

    public static void drawInventoryInfo(Graphics g){
        loadImgs();
        g.drawImage(inventoryInfo,WIDTH/2 - 350,150,500,322,null);
    }
    public static void drawMenuScreen(Graphics g){
        loadImgs();
        g.drawImage(menuScreen,0,0,WIDTH,HEIGHT,null);
        drawPlayButton(g);
    }

    public static void highlightSelectedTile(Graphics g, int row, int col){
        g.setColor(new Color(137, 207, 240, 100));
        g.fillRect(col * gameTile.size,row * gameTile.size,gameTile.size,gameTile.size);
    }

    public static void highlightSelectedItemSlot(Graphics g, int row, int col){
        g.setColor(new Color(137, 207, 240, 100));
        g.fillRect(row,col,gameTile.size,gameTile.size);
    }

    public static void drawAccessHelpGraphic(Graphics g){
        g.drawImage(accessHelp,0 ,-40,120,120,null);
    }

    public static void drawHelpGraphic(Graphics g){
        g.drawImage(helpInfo,WIDTH/2 - 500,75,1000,644,null);
        g.drawImage(customizeESC, 0 ,-40,120,120,null);
    }

    private static void loadImgs(){
        if(playButton != null) return;
        try {
            playButton = ImageIO.read(gameTile.class.getResourceAsStream("UI/playButton.png"));
            customize = ImageIO.read(gameTile.class.getResourceAsStream("UI/customize.png"));
            customizeESC = ImageIO.read(gameTile.class.getResourceAsStream("UI/escButton.png"));
            inventoryInfo = ImageIO.read(gameTile.class.getResourceAsStream("UI/inventoryInfo.png"));
            menuScreen = ImageIO.read(gameTile.class.getResourceAsStream("UI/menuScreen.png"));
            helpInfo = ImageIO.read(gameTile.class.getResourceAsStream("UI/help.png"));
            accessHelp = ImageIO.read(gameTile.class.getResourceAsStream("UI/accessHelp.png"));

        }
        catch(IOException e){

        }
    }

}


 class DialogueBox {
    /*
    This class is sort of a user interface thing, so we wanted to put it in the UI file. It always has
    the same position and image, but message changes depending on what it is supposed to tell the player.
    Dialogue boxes pop up after fishing to tell the player if they caught a fish or not, and when they catch
    a bunny to tell them that bunny meat was added to their inventory. With more actions, this class could be used
    for game event worth notifying the player about.
     */
    private String message;
    private final static int px = 0;
    private final static int py = 500;
    private final static int width = 268 * 2;
    private final static int height = 268;
    BufferedImage boxImg;
    public DialogueBox(String message){
        this.message = message;
        try{
            boxImg = ImageIO.read(getClass().getResourceAsStream("UI/dialogueBox.png"));
        }
        catch (IOException e){}
    }

    public void draw(Graphics g){
        g.drawImage(boxImg,px,py,width,height, null);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        g.drawString(message, px + 230, py + 150);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 10));
        g.drawString("Click anywhere on the screen to exit this dialogue", px + 315, py + 250);
    }
}


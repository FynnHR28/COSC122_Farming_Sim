import javax.swing.*;
//hello
//Run the program from here, but the main description is at the top of Game.java
public class Main {
    public static void main(String[] args) {
        //Same as HW's
        JFrame frame = new JFrame("Farmer John's World");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game mainInstance = new Game();
        frame.setContentPane(mainInstance);
        frame.pack();
        frame.setVisible(true);

    }
}

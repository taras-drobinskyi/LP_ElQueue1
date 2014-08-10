import javax.swing.*;
import java.awt.*;

/**
 * Created by forando on 06.04.14.
 * The starting point
 */
public class Main {
    public static void main(String[] args) {
        MainForm form = new MainForm();
        GraphicsEnvironment env = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice vc = env.getDefaultScreenDevice();
        //vc.setFullScreenWindow(form);
    }
}

import java.awt.*;

/**
 * Created by forando on 06.04.14.
 */
public class Main {
    public static void main (String[] args){
        MainForm form = new MainForm();
        GraphicsEnvironment env = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice vc = env.getDefaultScreenDevice();
        vc.setFullScreenWindow(form);
    }
}

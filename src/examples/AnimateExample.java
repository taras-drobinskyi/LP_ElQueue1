package examples;

import helpers.ResourceFile;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.EnumMap;

import javax.imageio.ImageIO;
import javax.swing.*;

@SuppressWarnings("serial")
public class AnimateExample extends JPanel implements MouseMotionListener {
    public static final String IMG_PATH = "/resources/ic_launcher.png";
    private static final int PREF_W = 800;
    private static final int PREF_H = 800;
    private static final int TIMER_DELAY = 20;
    private static final String KEY_DOWN = "key down";
    private static final String KEY_RELEASE = "key release";
    public static final int TRANSLATE_SCALE = 3;
    private static final String BACKGROUND_STRING = "Use Arrow Keys to Move Image";
    private static final Font BG_STRING_FONT = new Font(Font.SANS_SERIF,
            Font.BOLD, 32);
    private EnumMap<Direction, Boolean> dirMap =
            new EnumMap<AnimateExample.Direction, Boolean>(Direction.class);
    private BufferedImage image = null;
    private int imgX = 0;
    private int imgY = 0;
    private int bgStringX;
    private int bgStringY;

    public AnimateExample() {

        addMouseMotionListener(this);

        for (Direction dir : Direction.values()) {
            dirMap.put(dir, Boolean.FALSE);
        }
        try {
            ResourceFile rf = new ResourceFile(IMG_PATH);
            File imageFile = rf.getFile();
            image = ImageIO.read(imageFile);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Timer(TIMER_DELAY, new TimerListener()).start();

        // here we set up our key bindings
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = getInputMap(condition);
        ActionMap actionMap = getActionMap();
        for (final Direction dir : Direction.values()) {

            // for the key down key stroke
            KeyStroke keyStroke = KeyStroke.getKeyStroke(dir.getKeyCode(), 0,
                    false);
            inputMap.put(keyStroke, dir.name() + KEY_DOWN);
            //inputMap.put(keyStroke, KEY_DOWN1);
            actionMap.put(dir.name() + KEY_DOWN, new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    dirMap.put(dir, true);
                }
            });

            // for the key release key stroke
            keyStroke = KeyStroke.getKeyStroke(dir.getKeyCode(), 0, true);
            inputMap.put(keyStroke, dir.name() + KEY_RELEASE);
            //inputMap.put(keyStroke, KEY_RELEASE1);
            actionMap.put(dir.name() + KEY_RELEASE, new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    dirMap.put(dir, false);
                }
            });
        }

        FontMetrics fontMetrics = getFontMetrics(BG_STRING_FONT);
        int w = fontMetrics.stringWidth(BACKGROUND_STRING);
        int h = fontMetrics.getHeight();

        bgStringX = (PREF_W - w) / 2;
        bgStringY = (PREF_H - h) / 2;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g.setFont(BG_STRING_FONT);
        g.setColor(Color.LIGHT_GRAY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawString(BACKGROUND_STRING, bgStringX, bgStringY);

        if (image != null) {
            g.drawImage(image, imgX, imgY, this);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println("Mouse Moved to " + e.getX() + "x" + e.getY());
    }

    private class TimerListener implements ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            for (Direction dir : Direction.values()) {
                if (dirMap.get(dir)) {
                    imgX += dir.getX() * TRANSLATE_SCALE;
                    imgY += dir.getY() * TRANSLATE_SCALE;
                    bgStringX += dir.getX() * TRANSLATE_SCALE;
                    bgStringY += dir.getY() * TRANSLATE_SCALE;
                }
            }
            repaint();
        };
    }

    enum Direction {
        Up(KeyEvent.VK_UP, 0, -1), Down(KeyEvent.VK_DOWN, 0, 1), Left(
                KeyEvent.VK_LEFT, -1, 0), Right(KeyEvent.VK_RIGHT, 1, 0);

        private int keyCode;
        private int x;
        private int y;

        private Direction(int keyCode, int x, int y) {
            this.keyCode = keyCode;
            this.x = x;
            this.y = y;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

    }

    private static void createAndShowGui() {
        AnimateExample mainPanel = new AnimateExample();

        final JFrame frame = new JFrame("Animate Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        //frame.setLocationByPlatform(true);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowActivated(WindowEvent e) {
                System.out.println("WINDOW ACTIVATED!!!");
                frame.setLocation(1300,200);
                frame.setLocation(1500,200);
            }
        });
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        System.out.println("Screen Size is " + width + "x" + height);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }
}
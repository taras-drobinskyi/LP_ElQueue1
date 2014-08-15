/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package externals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by forando on 14.08.14.
 */
public class Terminal extends JFrame{
    private JPanel rootPanel;
    private JButton b_next;
    private JLabel l_client;
    private JRadioButton r1;
    private JRadioButton r2;
    private JRadioButton r3;
    private JRadioButton r4;
    private JRadioButton r5;
    private JPanel radioPanel;


    int x;
    int y;
    int nx;
    int ny;
    int formWidth;
    int formHeight;

    private boolean mouseEntered = false;
    private boolean locked = false;
    private boolean snapped = false;
    private boolean minimized = false;
    private boolean slidingINInProgress = false;
    private int sd = 50;

    public Terminal() {
        //Form Title
        super("Slider");

        setContentPane(rootPanel);
        setUndecorated(true);

        setAlwaysOnTop(true);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// locate Form in the center of the screen

        rootPanel.setFocusable(true);
        rootPanel.requestFocusInWindow();

        //setSize(300, 300);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                doSnapping(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (snapped && !mouseEntered && minimized && !slidingINInProgress){
                    mouseEntered = true;
                    minimized = false;
                    slidingINInProgress = true;
                    System.out.println("MOUSE ENTERED!");
                    doSlideIN();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(snapped && !slidingINInProgress && !rootPane.getVisibleRect().contains(e.getPoint())){
                    mouseEntered = false;
                    minimized = true;
                    System.out.println("MOUSE EXITED!");
                    doSlideOUT();
                }
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int width = getSize().width;
                int height = getSize().height;
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (int)screenSize.getWidth() - width;
                int y = ((int)screenSize.getHeight() - height)/2;

                setBounds(x, y,width, height);

            }
        });

        setVisible(true);
    }

    private void doSlideOUT() {
        Timer timer = new Timer(10, new SliderTimer(nx, ny, formWidth, 10, formHeight, false));
        timer.setInitialDelay(0);
        timer.start();
    }

    private void doSlideIN() {
        Timer timer = new Timer(10, new SliderTimer(nx, ny, 10, 250, formHeight, true));
        timer.setInitialDelay(0);
        timer.start();
    }

    void doSnapping(ComponentEvent evt){
        if (locked)
            return;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        nx = evt.getComponent().getX();
        ny = evt.getComponent().getY();
        formWidth = evt.getComponent().getWidth();
        formHeight = evt.getComponent().getHeight();
        // top
        if (ny < 0 + sd) {
            ny = 0;
        }
        // left
        if (nx < 0 + sd) {
            nx = 0;
        }
        // right
        if (nx > screenSize.getWidth() - formWidth - sd) {
            nx = (int) screenSize.getWidth() - formWidth;
            snapped = true;
        }else{
            snapped = false;
        }
        // bottom
        if (ny > screenSize.getHeight() - formHeight - sd) {
            ny = (int) screenSize.getHeight() - formHeight;
        }
        // make sure we don't get into a recursive loop when the
        // set location generates more events
        locked = true;
        evt.getComponent().setLocation(nx, ny);
        locked = false;
    }

    private class SliderTimer implements ActionListener{
        int x;
        int y;
        int initialWidth;
        int finalWidth;
        int height;
        boolean slideIN;
        boolean stopAction = false;

        private SliderTimer(int x, int y, int initialWidth, int finalWidth, int height, boolean slideIN) {
            this.x = x;
            this.y = y;
            this.initialWidth = initialWidth;
            this.finalWidth = finalWidth;
            this.height = height;
            this.slideIN = slideIN;
            System.out.println("WIDTH = " + initialWidth);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(stopAction){
                ((Timer) e.getSource()).stop();
                slidingINInProgress = false;
            }else {
                if (slideIN) {
                    x -= 10;
                    initialWidth += 10;
                    if (initialWidth > finalWidth + 20) {
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        int finalX = (int) screenSize.getWidth() - finalWidth;
                        setBounds(finalX, y, finalWidth, height);

                        /*((Timer) e.getSource()).stop();
                        slidingINInProgress = false;*/
                        stopAction = true;
                    } else {
                        setBounds(x, y, initialWidth, height);
                    }

                } else {
                    x += 10;
                    initialWidth -= 10;
                    if (initialWidth < finalWidth + 20) {
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        int finalX = (int) screenSize.getWidth() - finalWidth;
                        setBounds(finalX, y, finalWidth, height);

                        //((Timer) e.getSource()).stop();
                        stopAction = true;
                    } else {
                        setBounds(x, y, initialWidth, height);
                    }
                }
            }

        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Terminal terminalForm = new Terminal();
            }
        });
    }
}

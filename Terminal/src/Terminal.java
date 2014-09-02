/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import helpers.APP;
import helpers.SocketMessage;
import services.TerminalClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by forando on 14.08.14.
 */
public class Terminal extends JFrame implements TerminalClient.TerminalClientListener{

    TerminalClient terminalClient;

    private JPanel rootPanel;
    private JButton b_next;
    private JLabel l_client;
    private JRadioButton r1;
    private JRadioButton r2;
    private JRadioButton r3;
    private JRadioButton r4;
    private JRadioButton r5;
    private JPanel radioPanel;


    int unfoldX;
    int foldX;
    int nx;
    int ny;
    int formWidth;
    int formHeight;
    int screenWidth;

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

        /*addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                //super.componentMoved(e);
                doSnapping(e);
                *//*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                nx = (int) screenSize.getWidth() - formWidth;
                ny = (int) screenSize.getHeight() - formHeight;*//*
            }
        });*/

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
                Point p = e.getPoint();
                if(snapped && !slidingINInProgress && !rootPane.getVisibleRect().contains(p)){
                    int x = p.x;
                    if (x <= formWidth) {
                        mouseEntered = false;
                        minimized = true;
                        System.out.println("MOUSE EXITED!");
                        doSlideOUT();
                    }
                }
            }
        });

        b_next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminalClient.message.operation = SocketMessage.ACCEPT_CLIENT;
                terminalClient.message.received = false;
                terminalClient.send();
            }
        });


        terminalClient = new TerminalClient(APP.IP, APP.PORT, 4);
        terminalClient.addTerminalClientListener(this);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                formWidth = getSize().width;
                formHeight = getSize().height;
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                screenWidth = (int)screenSize.getWidth();
                unfoldX = screenWidth - formWidth;
                foldX = (int)screenSize.getWidth() - 10;
                nx = unfoldX;
                ny = ((int)screenSize.getHeight() - formHeight)/2;

                setBounds(nx, ny,formWidth, formHeight);
                snapped = true;

                terminalClient.startClient();
            }
        });

        setVisible(true);
    }

    private void startClient(){
        terminalClient.startClient();
    }

    private synchronized void doSlideOUT() {
        radioPanel.setVisible(false);
        l_client.setVisible(false);
        b_next.setVisible(false);
        Timer timer = new Timer(10, new SliderTimer(nx, ny, formWidth, 10, formHeight, false));
        timer.setInitialDelay(0);
        timer.start();
    }

    private synchronized void doSlideIN() {
        //locked = true;
        Timer timer = new Timer(10, new SliderTimer(nx, ny, 10, 250, formHeight, true));
        timer.setInitialDelay(0);
        timer.start();
    }

    private void afterSlideIN(){
        radioPanel.setVisible(true);
        l_client.setVisible(true);
        b_next.setVisible(true);
        slidingINInProgress = false;
        //locked = false;
    }

    void doSnapping(ComponentEvent evt){
        if (locked)
            return;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        /*nx = evt.getComponent().getX();
        ny = evt.getComponent().getY();
        formHeight = evt.getComponent().getHeight();*/
        //formWidth = evt.getComponent().getWidth();
        /*
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
        }*/
        // make sure we don't get into a recursive loop when the
        // set location generates more events
        locked = true;
        //nx = (int) screenSize.getWidth() - formWidth;
        //evt.getComponent().setLocation(nx, ny);
        locked = false;
    }

    private class SliderTimer implements ActionListener{
        /*int x;
        int y;*/
        int initialWidth;
        int finalWidth;
        int height;
        boolean slideIN;
        boolean stopAction = false;

        private SliderTimer(int x, int y, int initialWidth, int finalWidth, int height, boolean slideIN) {
            this.initialWidth = initialWidth;
            this.finalWidth = finalWidth;
            this.height = height;
            this.slideIN = slideIN;
            //System.out.println("WIDTH = " + initialWidth);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(stopAction){
                ((Timer) e.getSource()).stop();
                if (slideIN){
                    afterSlideIN();
                }
            }else {
                if (slideIN) {
                    nx -= 10;
                    initialWidth += 10;
                    if (initialWidth > finalWidth + 20) {
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        //int finalX = (int) screenSize.getWidth() - finalWidth;
                        nx = unfoldX;
                        setBounds(nx, ny, finalWidth, height);

                        /*((Timer) e.getSource()).stop();
                        slidingINInProgress = false;*/
                        stopAction = true;
                    } else {
                        setBounds(nx, ny, initialWidth, height);
                    }

                } else {
                    nx += 10;
                    initialWidth -= 10;
                    if (initialWidth < finalWidth + 20) {
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        //int finalX = (int) screenSize.getWidth() - finalWidth;
                        nx = foldX;
                        setBounds(nx, ny, finalWidth, height);

                        //((Timer) e.getSource()).stop();
                        stopAction = true;
                    } else {
                        setBounds(nx, ny, initialWidth, height);
                    }
                }
            }

        }
    }

    private synchronized void checkForExiting(Point p){
        new MouseExiteChecker(p).start();

    }

    private class MouseExiteChecker extends Thread{
        private Point point;

        public MouseExiteChecker(Point p){
            this.point = p;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(300);
                if(snapped && !slidingINInProgress && !rootPane.getVisibleRect().contains(point)){
                    mouseEntered = false;
                    minimized = true;
                    System.out.println("MOUSE EXITED!");
                    doSlideOUT();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRegister() {
        b_next.setEnabled(true);
    }

    @Override
    public void onInputMessage() {

    }

    @Override
    public void onCloseSocket() {
        b_next.setEnabled(false);
        startClient();
    }
}

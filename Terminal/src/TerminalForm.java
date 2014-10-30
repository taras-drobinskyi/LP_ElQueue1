/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import client.ClientServer;
import main.APP;
import sockets.SocketMessage;
import sockets.TerminalMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by forando on 14.08.14.
 */
public class TerminalForm extends JFrame implements ClientServer.ClientServerListener {

    ClientServer clientServer;

    private JPanel rootPanel;
    private JButton b_next;
    private JLabel l_client;
    private JRadioButton r1;
    private JRadioButton r2;
    private JRadioButton r3;
    private JRadioButton r4;
    private JRadioButton r5;
    private JPanel radioPanel;

    private final static String CONNECT = "Подключиться";
    private final static String NEXT = "Следующий";
    private final static String ACCEPT = "Принять";
    private final static String WAIT = "Подождите";

    private int state = SocketMessage.REGISTER_SOCKET;
    private boolean requestIsStopped = false;


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

    public TerminalForm() {
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
                    if (x <= formWidth && state != TerminalMessage.ACCEPT_CLIENT) {
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
                switch (state){
                    case TerminalMessage.REQUEST_CLIENT:
                        clientServer.message.operation = TerminalMessage.REQUEST_CLIENT;
                        clientServer.message.received = false;
                        clientServer.send();
                        break;
                    case TerminalMessage.ACCEPT_CLIENT:
                        clientServer.message.operation = TerminalMessage.ACCEPT_CLIENT;
                        clientServer.message.received = false;
                        clientServer.send();
                        break;
                    default://Open Terminal
                        startClientServer();
                        break;
                }
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                formWidth = getSize().width;
                formHeight = getSize().height;
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                screenWidth = (int) screenSize.getWidth();
                unfoldX = screenWidth - formWidth;
                foldX = (int) screenSize.getWidth() - 10;
                nx = unfoldX;
                ny = ((int) screenSize.getHeight() - formHeight) / 2;

                setBounds(nx, ny, formWidth, formHeight);
                snapped = true;
            }
        });

        setVisible(true);
    }

    private void startClientServer(){
        clientServer = new ClientServer(APP.IP, APP.PORT, SocketMessage.TERMINAL, 4);
        clientServer.addClientServerListener(this);
        clientServer.startClient();
    }

    private synchronized void doSlideOUT() {
        radioPanel.setVisible(false);
        l_client.setVisible(false);
        b_next.setVisible(false);
        Timer timer = new Timer(10, new SliderTimerListener(nx, ny, formWidth, 10, formHeight, false));
        timer.setInitialDelay(0);
        timer.start();
    }

    private synchronized void doSlideIN() {
        //locked = true;
        Timer timer = new Timer(10, new SliderTimerListener(nx, ny, 10, 250, formHeight, true));
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

    private class SliderTimerListener implements ActionListener{
        /*int x;
        int y;*/
        int initialWidth;
        int finalWidth;
        int height;
        boolean slideIN;
        boolean stopAction = false;

        private SliderTimerListener(int x, int y, int initialWidth, int finalWidth, int height, boolean slideIN) {
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

    private class BlinkTimerListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if (state == TerminalMessage.ACCEPT_CLIENT) {
                l_client.setVisible(!l_client.isVisible());
            }else{
                ((Timer)e.getSource()).stop();
                l_client.setVisible(true);
            }
        }
    }

    private class MouseExitChecker extends Thread{
        private Point point;

        public MouseExitChecker(Point p){
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

    private synchronized void submitInputMessage(){
        switch (clientServer.message.operation){
            case TerminalMessage.ACCEPT_CLIENT:
                if (clientServer.message.received){
                    l_client.setText("0");
                    state = TerminalMessage.REQUEST_CLIENT;
                    if (requestIsStopped) {
                        b_next.setEnabled(false);
                        b_next.setText(WAIT);
                    }else{
                        b_next.setText(NEXT);
                    }
                }
                break;
            case TerminalMessage.REQUEST_CLIENT:
                if (clientServer.message.received){
                    l_client.setText(String.valueOf(clientServer.message.value));
                    b_next.setText(ACCEPT);
                    state = TerminalMessage.ACCEPT_CLIENT;
                    new Timer(500, new BlinkTimerListener()).start();
                    if (minimized){
                        minimized = false;
                        slidingINInProgress = true;
                        doSlideIN();
                    }
                }
            case TerminalMessage.HOLD_TERMINAL:
                System.out.println("TERMINAL onHOLD value = " + clientServer.message.value);
                if (clientServer.message.value == 1){
                    requestIsStopped = true;
                    if (state == TerminalMessage.REQUEST_CLIENT) {
                        b_next.setEnabled(false);
                        b_next.setText(WAIT);
                    }
                }else{
                    requestIsStopped = false;
                    if (state == TerminalMessage.REQUEST_CLIENT) {
                        b_next.setEnabled(true);
                        b_next.setText(NEXT);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRegister(int id) {
        /*if (clientServer.message.operation == sockets.SocketMessage.HOLD_TERMINAL &&
                clientServer.message.value == 1){
            System.out.println("TERMINAL onHOLD value = " + clientServer.message.value);
            requestIsStopped = true;
            b_next.setEnabled(false);
            b_next.setText(WAIT);
        }else{*/
            b_next.setText(NEXT);
        //}
        state = TerminalMessage.REQUEST_CLIENT;
        l_client.setText("0");
    }

    @Override
    public void onInputMessage() {
        submitInputMessage();
    }

    @Override
    public void onCloseSocket() {
        l_client.setText("---");
        b_next.setText(CONNECT);
        state = SocketMessage.REGISTER_SOCKET;
        clientServer = null;
    }
}

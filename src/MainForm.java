/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

/**
 * Created by forando on 06.04.14.
 * This is Main Form that indicates info for clients
 */
public class MainForm extends JFrame {
    static int standardBlinkRate;
    static int clicksToChangeBattery;
    static int ticketsToInsertPaper;
    static int takeTicketBlinkRate;
    int lastClient = 0;
    int nextClient = 0;
    int client1 = 0;
    int client2 = 0;
    int client3 = 0;
    int client4 = 0;
    int client5 = 0;
    int buttonClicked = 0;
    int ticketsPrinted = 0;
    Timer timerClient1;
    Timer timerClient2;
    Timer timerBottomLine;
    Timer timerError;
    Timer timerServiceStopped;
    Timer timerPrinter;
    Audio notificationSound;
    Audio errorSound;
    POS_PRINTER printer;
    XMLVARIABLES variables;
    private boolean PRINTER_ERROR = false;
    private boolean SERVICE_STOPPED = false;
    private boolean TICKET_TAKEN = false;
    private boolean TICKET_IS_PRINTING = false;
    private int total = 0;
    private JPanel rootPanel;
    private JLabel l_clientTitle;
    private JLabel l_terminal1;
    private JLabel l_terminal2;
    private JLabel l_terminalTitle;
    private JLabel l_client1;
    private JLabel l_client2;
    private JLabel l_client3;
    private JLabel l_client4;
    private JLabel l_totalTitle;
    private JLabel l_total;
    private JLabel l_client1_arrow;
    private JLabel l_client2_arrow;
    private JLabel l_takeTicket;
    private JPanel mainUIPanel;
    private JLabel l_serviceStopped;
    private JPanel bottomPanel;
    private JPanel videoPanel;
    private JLabel l_client3_arrow;
    private JLabel l_client4_arrow;
    private JLabel l_client5;
    private JLabel l_client5_arrow;
    private JLabel l_terminal3;
    private JLabel l_terminal4;
    private JLabel l_terminal5;

    private Canvas canvas;

    private int formWidth;
    private int formHeight;
    private int uiPanelWidth;
    private int uiPanelHeight;
    private int w_percent;
    private int h_percent;
    private Point hor_line1_p1 = new Point(100, 100);
    private Point hor_line1_p2 = new Point(200, 200);
    private Point hor_line2_p1 = new Point(100, 100);
    private Point hor_line2_p2 = new Point(200, 200);
    private Point hor_line3_p1 = new Point(100, 100);
    private Point hor_line3_p2 = new Point(200, 200);
    
    private int restOfClients;

    public MainForm() {
        //Form Title
        super("Продукт Компании \"ВЕРСИЯ\"");

        setContentPane(rootPanel);
        //setUndecorated(true);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// locate Form in the center of the screen

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        //MyLayoutManager mgr = new MyLayoutManager();

        mainUIPanel.setLayout(null);
        videoPanel.setLayout(null);
        bottomPanel.setLayout(null);

        initVariables();
        initObjects();

        // messagePanel.setSize(uiPanelWidth, 0);

        //Print first ticket
        total = lastClient + 1;
        printer.Print(total);

        mainUIPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = e.getComponent().getBounds();
                uiPanelWidth = (int) r.getWidth();
                uiPanelHeight = (int) r.getHeight();
                w_percent = uiPanelWidth / 100;
                h_percent = uiPanelHeight / 100;

                relocateMyComponents();
            }
        });

        videoPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = e.getComponent().getBounds();
                Component cmp = videoPanel.getComponent(0);
                int width = (int)r.getWidth();
                int height = (int)r.getHeight();
                cmp.setBounds(0, 0, width, height);
            }
        });

        bottomPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = e.getComponent().getBounds();
                int width = (int)r.getWidth();
                int height = (int)r.getHeight();
                relocateBottomComponents(width, height);
            }
        });

        rootPanel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                //System.out.println("Pressed " + e.getKeyCode());
                switch (e.getKeyCode()) {
                    case 112: //F1 - terminal1 Button Pressed
                        if (nextClient > 0) {
                            client1 = nextClient;
                            reasignClients34();
                            relocateMyComponents();
                            timerClient1.start();
                            notificationSound.Play();
                            variables.setClientAsigned(1, client1);
                            variables.setNextClient(nextClient);
                        }
                        buttonClicked++;
                        variables.setButtonClicked(buttonClicked);
                        break;
                    case 113: //F2 - terminal2 Button Pressed
                        if (nextClient > 0) {
                            client2 = nextClient;
                            reasignClients34();
                            relocateMyComponents();
                            timerClient2.start();
                            notificationSound.Play();
                            variables.setClientAsigned(2, client2);
                            variables.setNextClient(nextClient);
                        }
                        buttonClicked++;
                        variables.setButtonClicked(buttonClicked);
                        break;
                    case 114: //F3 - Reset System
                        total = 1;
                        lastClient = 0;
                        nextClient = 0;
                        client1 = 0;
                        client2 = 0;
                        client3 = 0;
                        client4 = 0;
                        relocateMyComponents();

                        SERVICE_STOPPED = false;
                        triggerService(SERVICE_STOPPED, true);

                        batteryCheck();
                        variables.setLastClient(lastClient);
                        variables.setNextClient(nextClient);
                        variables.setClientAsigned(1, client1);
                        variables.setClientAsigned(2, client2);
                        break;
                    case 116: //F5 - Printer ERROR ON
                        if (!SERVICE_STOPPED) {
                            PRINTER_ERROR = true;
                            ticketsPrinted = 0;
                            variables.setTicketsPrinted(ticketsPrinted);
                            relocateMyComponents();
                            timerBottomLine.stop();
                            timerError.start();
                            notificationSound.Stop();
                            errorSound.Play();
                            notificationSound.Reset();
                            //errorSound.Play();
                            //incrementing lastClient by 1
                            lastClient++;
                            total = lastClient + 1;
                            variables.setLastClient(lastClient);
                            if (nextClient == 0) {
                                nextClient = lastClient;
                                //client3 = nextClient;
                            }/* else if (client3 > 0 && client4 == 0) {
                                client4 = lastClient;
                            }*/
                            variables.setNextClient(nextClient);
                        }
                        break;
                    case 117: //F6 - Printer ERROR OFF
                        if (!SERVICE_STOPPED) {
                            timerError.stop();
                            timerBottomLine.start();
                            PRINTER_ERROR = false;
                            printer.Print(total);
                            relocateMyComponents();
                        }
                        break;
                    case 118: //F7 - Service Stopped
                        SERVICE_STOPPED = true;
                        triggerService(SERVICE_STOPPED);
                        break;
                    case 119: //F8 - Service Renewed
                        SERVICE_STOPPED = false;
                        triggerService(SERVICE_STOPPED);
                        break;
                    case 120: //F9 - Trigger Service
                        SERVICE_STOPPED = !SERVICE_STOPPED;
                        triggerService(SERVICE_STOPPED);
                        break;
                    case 36://signal to print a ticket
                        if(!TICKET_IS_PRINTING) {
                            total++;
                            lastClient = total - 1;
                            if (nextClient == 0) {
                                nextClient = lastClient;
                                //client3 = nextClient;
                            } /*else if (client3 > 0 && client4 == 0) {
                                client4 = lastClient;
                            }*/
                            relocateMyComponents();
                            variables.setLastClient(lastClient);
                            variables.setNextClient(nextClient);
                            printTicket();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        //subscribing to Window(or JFrame) Opened Event
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                batteryCheck();
            }
        });

        //start blinking bottom line
        timerBottomLine.start();

        rootPanel.setFocusable(true);
        rootPanel.requestFocusInWindow();

        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);
        /*MyLayoutManager.MouseDragger mouseDragger = mgr.new MouseDragger();
        mouseDragger.makeDraggable(canvas);*/

        //videoPanel.add(canvas, new Rectangle(0, 0, 500, 500));
        videoPanel.add(canvas, BorderLayout.CENTER);

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
        EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
        mediaPlayer.setVideoSurface(videoSurface);
        mediaPlayer.playMedia("/media/forando/DATA/Фильмы/mamont.2009.xvid.111dvdrip.(hdrip).avi");

        setVisible(true);

    }

    //By Service means possibility to accept new clients
    private void triggerService (boolean turnOn, boolean... flags){
        boolean reset = false;
        if (flags.length>0){
            reset = flags[0];
        }
        if (turnOn){
            if (PRINTER_ERROR){
                timerError.stop();
            }else {
                timerBottomLine.stop();
            }
            relocateMyComponents();
            timerServiceStopped.start();
        }else{
            timerServiceStopped.stop();
            relocateMyComponents();
            if (PRINTER_ERROR){
                timerError.start();
            }else {
                timerBottomLine.start();
                if (TICKET_TAKEN || reset) {
                    printTicket();
                }
            }
        }
    }

    private void batteryCheck() {
        if (buttonClicked > clicksToChangeBattery) {
            errorSound.Play();
            MessageForm f = new MessageForm();
            f.addMessageFormListener(new MessageFormListener() {
                @Override
                public void onReset() {
                    buttonClicked = 0;
                    variables.setButtonClicked(buttonClicked);
                    redrawLines();
                }

                @Override
                public void onClose() {
                    redrawLines();
                }

                @Override
                public void onPrintTicket() {
                    printTicket();
                }
            });
        }
    }

    private void printTicket(){
        if (!PRINTER_ERROR) {
            if (TICKET_TAKEN){
                TICKET_IS_PRINTING = true;
                //1 sec deley that prevents to print something new
                timerPrinter.start();
                printer.Print(total);

                ticketsPrinted++;
                variables.setTicketsPrinted(ticketsPrinted);
                TICKET_TAKEN = false;
            }else {
                if (!SERVICE_STOPPED) {

                    TICKET_IS_PRINTING = true;
                    timerPrinter.start();
                    //1 sec deley that prevents to print something new
                    printer.Print(total);
                    ticketsPrinted++;
                    variables.setTicketsPrinted(ticketsPrinted);
                } else {
                    TICKET_TAKEN = true;
                }
            }
        }
    }

    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.white);
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(8,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        Line2D h_lin1 = new Line2D.Float(hor_line1_p1.x, hor_line1_p1.y, hor_line1_p2.x, hor_line1_p2.y);
        g2.draw(h_lin1);
        Line2D h_lin2 = new Line2D.Float(hor_line2_p1.x, hor_line2_p1.y, hor_line2_p2.x, hor_line2_p2.y);
        g2.draw(h_lin2);
        Line2D h_lin3 = new Line2D.Float(hor_line3_p1.x, hor_line3_p1.y, hor_line3_p2.x, hor_line3_p2.y);
        g2.draw(h_lin3);
    }

    private void initObjects() {
        timerBottomLine = new Timer(takeTicketBlinkRate, new TimerListener(0));
        timerBottomLine.setInitialDelay(0);
        timerClient1 = new Timer(standardBlinkRate, new TimerListener(1));
        timerClient1.setInitialDelay(0);
        timerClient2 = new Timer(standardBlinkRate, new TimerListener(2));
        timerClient2.setInitialDelay(0);
        timerError = new Timer(standardBlinkRate, new TimerListener(3));
        timerError.setInitialDelay(0);
        timerServiceStopped = new Timer(takeTicketBlinkRate, new TimerListener(4));
        timerServiceStopped.setInitialDelay(0);
        timerPrinter = new Timer(1000, new TimerListener(5));
        timerPrinter.setInitialDelay(1000);
        printer = new POS_PRINTER();
        errorSound = new Audio("/resources/notify.wav");
        notificationSound = new Audio("/resources/chimes.wav");
    }

    private void initVariables() {

        variables = new XMLVARIABLES(APP.VARIABLES_PATH);

        variables.setLastClient(variables.getLastClient() + 1);
        lastClient = variables.getLastClient();
        client1 = variables.getClientAsigned(1);
        client2 = variables.getClientAsigned(2);
        client3 = variables.getClientAsigned(3);
        client4 = variables.getClientAsigned(4);
        client5 = variables.getClientAsigned(5);
        buttonClicked = variables.getButtonClicked();
        ticketsPrinted = variables.getTicketsPrinted();
        nextClient = variables.getNextClient();

        clicksToChangeBattery = variables.getClicksToChangeBattery();
        ticketsToInsertPaper = variables.getTicketsToInsertPaper();
        standardBlinkRate = variables.getStandardBlinkRate();
        takeTicketBlinkRate = variables.getTakeTicketBlinkRate();

        //client3 = nextClient;
        if (nextClient == 0) {
            nextClient = lastClient;
            //client3 = nextClient;
        } /*else if (nextClient > 0 && nextClient < lastClient) {
            client4 = nextClient + 1;
        } else {
            client4 = 0;
        }*/

        variables.setNextClient(nextClient);

        setRestOfClients();
    }

    private void setRestOfClients() {
        if (nextClient > 0) {
            restOfClients = lastClient - nextClient + 1;
        } else {
            restOfClients = 0;
        }
    }

    private void relocateBottomComponents(int width, int height) {

        int h_percent = height / 100;
        int w_percent = width / 100;
        int titleHeight = h_percent * 12;
        int tableDataHeight = h_percent * 26;
        int dataHeight = h_percent * 16;
        int totalDataHeight = h_percent * 90;

        int w_loc;
        int h_loc;

        String labelText;
        int stringWidth;
        String fontName = l_clientTitle.getFont().getName();

        //=====================TOTAL DATA ========================================


        l_totalTitle.setText("ВСЕГО  В  ОЧЕРЕДИ:");
        l_totalTitle.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_totalTitle.getText();
        int totalTitle_stringWidth = l_totalTitle.getFontMetrics(l_totalTitle.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (totalTitle_stringWidth / 2);
        //w_loc = l_total.getLocation().x - (totalTitle_stringWidth) - (w_percent * 4);
        h_loc = h_percent * 30;
        l_totalTitle.setLocation(w_loc, h_loc);
        l_totalTitle.setSize(totalTitle_stringWidth, totalDataHeight + h_percent * 3);

        setRestOfClients();
        l_total.setText(String.valueOf(restOfClients));
        l_total.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_total.getText();
        stringWidth = l_total.getFontMetrics(l_total.getFont()).stringWidth(labelText);
        w_loc = l_totalTitle.getLocation().x + totalTitle_stringWidth + w_percent;
        //w_loc = (w_percent * 101) - stringWidth - (w_percent * 2);
        //w_loc = width - stringWidth - (h_percent*4);
        h_loc = h_percent * 30;
        l_total.setLocation(w_loc, h_loc);
        l_total.setSize(stringWidth, totalDataHeight + h_percent * 3);

        //===========================================================================
        if(SERVICE_STOPPED){
            l_takeTicket.setText("ТАЛОНОВ  НЕТ");
        }else if (PRINTER_ERROR) {
            l_takeTicket.setText("ВСТАВЬТЕ  БУМАГУ!");
        } else {
            l_takeTicket.setText("ВОЗЬМИТЕ  ТАЛОН");
        }
        l_takeTicket.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_takeTicket.getText();
        stringWidth = l_takeTicket.getFontMetrics(l_takeTicket.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (stringWidth / 2);
        //h_loc = h_percent * 85;
        h_loc = l_totalTitle.getLocation().y;
        l_takeTicket.setLocation(w_loc, h_loc);
        l_takeTicket.setSize(stringWidth, totalDataHeight + h_percent * 3);
        //the very first second they should not appear on screen:
        l_takeTicket.setText("");

        l_serviceStopped.setText("ТЕХНИЧЕСКИЙ  ПЕРЕРЫВ");
        l_serviceStopped.setFont(new Font(fontName, Font.PLAIN, totalDataHeight - h_percent * 2));
        labelText = l_serviceStopped.getText();
        stringWidth = l_serviceStopped.getFontMetrics(l_serviceStopped.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (stringWidth / 2);
        //h_loc = h_percent * 85;
        h_loc = l_totalTitle.getLocation().y;
        l_serviceStopped.setLocation(w_loc, h_loc);
        l_serviceStopped.setSize(stringWidth, totalDataHeight + h_percent * 3);
        //the very first second they should not appear on screen:
        l_serviceStopped.setText("");

        if (PRINTER_ERROR || SERVICE_STOPPED) {
            l_total.setText("");
            l_totalTitle.setText("");
        }
    }

    private void relocateMyComponents() {

        int titleHeight = h_percent * 8;
        int tableDataHeight = h_percent * 16;
        int dataHeight = h_percent * 16;

        int w_loc;
        int h_loc;

        String labelText;
        int stringWidth;
        String fontName = l_clientTitle.getFont().getName();


        l_clientTitle.setFont(new Font(fontName, Font.PLAIN, titleHeight));
        labelText = l_clientTitle.getText();
        stringWidth = l_clientTitle.getFontMetrics(l_clientTitle.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        h_loc = h_percent;
        l_clientTitle.setLocation(w_loc, h_loc);
        l_clientTitle.setSize(stringWidth, titleHeight - h_percent * 2);

        l_terminalTitle.setFont(new Font(fontName, Font.PLAIN, titleHeight));
        labelText = l_terminalTitle.getText();
        stringWidth = l_terminalTitle.getFontMetrics(l_terminalTitle.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 75) - (stringWidth / 2);
        h_loc = h_percent;
        l_terminalTitle.setLocation(w_loc, h_loc);
        l_terminalTitle.setSize(stringWidth, titleHeight - h_percent * 2);


        //=====================TABLE DATA ========================================


        l_client1.setText(String.valueOf(client1));
        l_client1.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client1.getText();
        stringWidth = l_client1.getFontMetrics(l_client1.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        h_loc = h_percent * 13;
        l_client1.setLocation(w_loc, h_loc);
        l_client1.setSize(stringWidth, tableDataHeight - h_percent * 3);

        l_client1_arrow.setText(">");
        l_client1_arrow.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client1_arrow.getText();
        stringWidth = l_client1_arrow.getFontMetrics(l_client1_arrow.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (stringWidth / 2);
        h_loc = h_percent * 13;
        l_client1_arrow.setLocation(w_loc, h_loc);
        l_client1_arrow.setSize(stringWidth, tableDataHeight - h_percent * 3);

        if (client1 == 0) {
            l_client1.setText("");
            l_client1_arrow.setText("");
        }

        l_client2.setText(String.valueOf(client2));
        l_client2.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client2.getText();
        stringWidth = l_client2.getFontMetrics(l_client2.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        h_loc = h_percent * 30;
        l_client2.setLocation(w_loc, h_loc);
        l_client2.setSize(stringWidth, tableDataHeight - h_percent * 3);

        l_client2_arrow.setText(">");
        l_client2_arrow.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client2_arrow.getText();
        stringWidth = l_client2_arrow.getFontMetrics(l_client2_arrow.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (stringWidth / 2);
        h_loc = h_percent * 30;
        l_client2_arrow.setLocation(w_loc, h_loc);
        l_client2_arrow.setSize(stringWidth, tableDataHeight - h_percent * 3);

        if (client2 == 0) {
            l_client2.setText("");
            l_client2_arrow.setText("");
        }


        //=====================DATA ========================================

        l_client3.setText(String.valueOf(client3));
        l_client3.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client3.getText();
        stringWidth = l_client3.getFontMetrics(l_client3.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        h_loc = h_percent * 47;
        //h_loc = l_client2.getLocation().y + l_client2.getHeight() + h_percent * 6;
        //h_loc = l_client4.getLocation().y - l_client3.getHeight();
        l_client3.setLocation(w_loc, h_loc);
        l_client3.setSize(stringWidth, tableDataHeight - h_percent * 3);

        l_client3_arrow.setText(">");
        l_client3_arrow.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client3_arrow.getText();
        stringWidth = l_client3_arrow.getFontMetrics(l_client3_arrow.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (stringWidth / 2);
        h_loc = h_percent * 47;
        l_client3_arrow.setLocation(w_loc, h_loc);
        l_client3_arrow.setSize(stringWidth, tableDataHeight - h_percent * 3);

        if (client3 == 0) {
            l_client3.setText("");
            l_client3_arrow.setText("");
        }

        //hiding this label:
        //l_client3.setText("");

/*        if (client3 == 0) {
            l_client3.setText("");
        }*/

        l_client4.setText(String.valueOf(client4));
        l_client4.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client4.getText();
        stringWidth = l_client4.getFontMetrics(l_client4.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        h_loc = h_percent * 64;
        //h_loc = l_client3.getLocation().y + l_client3.getHeight() + h_percent * 2;
        //h_loc = uiPanelHeight - l_client4.getHeight() - h_percent;
        l_client4.setLocation(w_loc, h_loc);
        l_client4.setSize(stringWidth, tableDataHeight - h_percent * 3);

        l_client4_arrow.setText(">");
        l_client4_arrow.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client4_arrow.getText();
        stringWidth = l_client4_arrow.getFontMetrics(l_client4_arrow.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (stringWidth / 2);
        h_loc = h_percent * 64;
        l_client4_arrow.setLocation(w_loc, h_loc);
        l_client4_arrow.setSize(stringWidth, tableDataHeight - h_percent * 3);

        if (client4 == 0) {
            l_client4.setText("");
            l_client4_arrow.setText("");
        }

        l_client5.setText(String.valueOf(client5));
        l_client5.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client5.getText();
        stringWidth = l_client5.getFontMetrics(l_client5.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        h_loc = h_percent * 81;
        //h_loc = l_client3.getLocation().y + l_client3.getHeight() + h_percent * 2;
        //h_loc = uiPanelHeight - l_client4.getHeight() - h_percent;
        l_client5.setLocation(w_loc, h_loc);
        l_client5.setSize(stringWidth, tableDataHeight - h_percent * 3);

        l_client5_arrow.setText(">");
        l_client5_arrow.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client5_arrow.getText();
        stringWidth = l_client5_arrow.getFontMetrics(l_client5_arrow.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (stringWidth / 2);
        h_loc = h_percent * 81;
        l_client5_arrow.setLocation(w_loc, h_loc);
        l_client5_arrow.setSize(stringWidth, tableDataHeight - h_percent * 3);

        if (client5 == 0) {
            l_client5.setText("");
            l_client5_arrow.setText("");
        }

        l_terminal1.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_terminal1.getText();
        stringWidth = l_terminal1.getFontMetrics(l_terminal1.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 75) - (stringWidth / 2);
        h_loc = h_percent * 13;
        l_terminal1.setLocation(w_loc, h_loc);
        l_terminal1.setSize(stringWidth, tableDataHeight - h_percent * 3);

        l_terminal2.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_terminal2.getText();
        stringWidth = l_terminal2.getFontMetrics(l_terminal2.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 75) - (stringWidth / 2);
        h_loc = h_percent * 30;
        l_terminal2.setLocation(w_loc, h_loc);
        l_terminal2.setSize(stringWidth, tableDataHeight - h_percent * 3);

        l_terminal3.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_terminal3.getText();
        stringWidth = l_terminal3.getFontMetrics(l_terminal3.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 75) - (stringWidth / 2);
        h_loc = h_percent * 47;
        l_terminal3.setLocation(w_loc, h_loc);
        l_terminal3.setSize(stringWidth, tableDataHeight - h_percent * 3);

        l_terminal4.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_terminal4.getText();
        stringWidth = l_terminal4.getFontMetrics(l_terminal4.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 75) - (stringWidth / 2);
        h_loc = h_percent * 64;
        l_terminal4.setLocation(w_loc, h_loc);
        l_terminal4.setSize(stringWidth, tableDataHeight - h_percent * 3);

        l_terminal5.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_terminal5.getText();
        stringWidth = l_terminal5.getFontMetrics(l_terminal5.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 75) - (stringWidth / 2);
        h_loc = h_percent * 81;
        l_terminal5.setLocation(w_loc, h_loc);
        l_terminal5.setSize(stringWidth, tableDataHeight - h_percent * 3);

        //hiding this label:
        //l_client4.setText("");

/*        if (client4 == 0) {
            l_client4.setText("");
        }*/
        /*if (PRINTER_ERROR || SERVICE_STOPPED) {
            l_client3.setText("");
            l_client4.setText("");
        }*/

        redrawLines();
    }

    private void redrawLines() {
        int left = 0;
        int right = uiPanelWidth;

        int correction = 40;

        hor_line1_p1 = new Point(left, correction + l_clientTitle.getHeight());
        hor_line1_p2 = new Point(right, correction + l_clientTitle.getHeight());

        hor_line2_p1 = new Point(left, correction + l_client1.getLocation().y + l_client1.getHeight());
        hor_line2_p2 = new Point(right, correction + l_client1.getLocation().y + l_client1.getHeight());

        hor_line3_p1 = new Point(left, correction + l_client2.getLocation().y + l_client2.getHeight());
        hor_line3_p2 = new Point(right, correction + l_client2.getLocation().y + l_client2.getHeight());

        repaint();
    }

    private void reasignClients34() {
        if (nextClient < lastClient) {
            nextClient ++;
            /*client3 = nextClient;
            if (client3 < lastClient) {
                client4++;
            } else {
                client4 = 0;
            }*/
        } else{
            nextClient = 0;
            //client3 = nextClient;
        }
    }

    private class TimerListener implements ActionListener {
        private final static int maxBlinking = 4;
        private JLabel label1;
        private JLabel label2;
        private JLabel label3;
        private Color bg;
        private Color fg;
        private boolean isForeground = true;
        private boolean option1 = true;
        private int alreadyBlinked = 0;
        private int _optionNumber;

        public TimerListener(int optionNumber) {
            switch (optionNumber) {
                case 0: //initialize bottom line blinking
                    this.label1 = l_total;
                    this.label2 = l_totalTitle;
                    this.label3 = l_takeTicket;
                    break;
                case 1: //termina1 pushbutton
                    this.label1 = l_client1;
                    this.label2 = l_client1_arrow;
                    break;
                case 2: //termina2 pushbutton
                    this.label1 = l_client2;
                    this.label2 = l_client2_arrow;
                    break;
                case 3: //Printer Error ON
                    this.label1 = l_takeTicket;
                    break;
                case 4: //Service Stopped
                    this.label1 = l_takeTicket;
                    this.label2 = l_serviceStopped;
                    break;
                case 5: //ticket is printing
                    //no neccessity to init something (we do it just to escape an error):
                    this.label1 = l_takeTicket;
                    this.label2 = l_serviceStopped;
                    break;
                default:
                    break;
            }
            fg = label1.getForeground();
            bg = label1.getBackground();
            this._optionNumber = optionNumber;
        }

        public void actionPerformed(ActionEvent e) {

            switch (_optionNumber) {
                case 0:
                    if (!option1) {
                        label3.setText("");
                        label1.setText(String.valueOf(restOfClients));
                        label2.setText("ВСЕГО  В  ОЧЕРЕДИ:");
                    } else {
                        label1.setText("");
                        label2.setText("");
                        label3.setText("ВОЗЬМИТЕ  ТАЛОН");
                    }
                    option1 = !option1;
                    break;
                case 3:
                    if (option1) {
                        label1.setText("");
                    } else {
                        label1.setText("ВСТАВЬТЕ  БУМАГУ!");
                    }
                    option1 = !option1;
                    break;
                case 4:
                    if (option1) {
                        label1.setText("");
                        label2.setText("ТЕХНИЧЕСКИЙ  ПЕРЕРЫВ");
                    } else {
                        label2.setText("");
                        label1.setText("ТАЛОНОВ  НЕТ");
                    }
                    option1 = !option1;
                    break;
                case 5://ticket has been printed
                    TICKET_IS_PRINTING = false;
                    timerPrinter.stop();
                    break;
                default:
                    if (alreadyBlinked <= maxBlinking * 2) {
                        alreadyBlinked++;
                        if (isForeground) {
                            label1.setForeground(fg);
                            label2.setForeground(fg);
                        } else {
                            label1.setForeground(bg);
                            label2.setForeground(bg);
                        }
                        isForeground = !isForeground;
                    } else {
                        alreadyBlinked = 0;
                        isForeground = true;
                        switch (_optionNumber) {
                            case 1:
                                timerClient1.stop();
                                break;
                            case 2:
                                timerClient2.stop();
                                break;
                            default:
                                break;
                        }
                    }
                    break;
            }
        }
    }
}

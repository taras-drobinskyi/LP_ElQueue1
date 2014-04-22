/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

/**
 * Created by forando on 06.04.14.
 * This is Main Form that indicates info for clients
 */
public class MainForm extends  JFrame {
    private static final int BLINKING_RATE = 500;
    int lastClient = 0;
    int nextClient = 0;
    int client1 = 0;
    int client2 = 0;
    int client3 = 0;
    int client4 = 0;
    int buttonClicked = 0;
    int ticketsPrinted = 0;
    static int clicksToChangeBattery;
    static int ticketsToInsertPaper;
    Timer timerClient1;
    Timer timerClient2;
    Timer timerBottomLine;
    Timer timerError;
    Audio notificationSound;
    Audio errorSound;
    POS_PRINTER printer;
    XMLVARIABLES variables;
    private boolean PRINTER_ERROR = false;
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
    private int formWidth;
    private int formHeight;
    private int w_percent;
    private int h_percent;
    private Point hor_line1_p1 = new Point(100, 100);
    private Point hor_line1_p2 = new Point(200, 200);
    private Point hor_line2_p1 = new Point(100, 100);
    private Point hor_line2_p2 = new Point(200, 200);
    private Point hor_line3_p1 = new Point(100, 100);
    private Point hor_line3_p2 = new Point(200, 200);
    private int restOfClients;

    public MainForm(){
        //Form Title
        super("Продукт Компании \"ВЕРСИЯ\"");

        setContentPane(rootPanel);
        setUndecorated(true);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// locate Form in the center of the screen

        this.setLayout(null);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        rootPanel.setLayout(null);

        initObjects();

        initVariables();

        //Print first ticket
        total = lastClient + 1;
        printer.Print(total);

        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = e.getComponent().getBounds();
                formWidth = (int) r.getWidth();
                formHeight = (int) r.getHeight();
                w_percent = formWidth / 100;
                h_percent = formHeight / 100;

                relocateMyComponents();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

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
                    case 112: //terminal1 Button Pressed
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
                    case 113: //terminal2 Button Pressed
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
                    case 114: //Reset System
                        total = 1;
                        lastClient = 0;
                        nextClient = 0;
                        client1 = 0;
                        client2 = 0;
                        client3 = 0;
                        client4 = 0;
                        relocateMyComponents();
                        printer.Print(total);
                        variables.setLastClient(lastClient);
                        variables.setNextClient(nextClient);
                        variables.setClientAsigned(1, client1);
                        variables.setClientAsigned(2, client2);
                        break;
                    case 115: //terminal2 Button Pressed
                        //reserve
                        break;
                    case 116: //Printer ERROR ON
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
                        if (client3 == 0) {
                            nextClient = lastClient;
                            client3 = nextClient;
                        } else if (client3 > 0 && client4 == 0) {
                            client4 = lastClient;
                        }
                        variables.setNextClient(nextClient);
                        //reinitialie Printer
                        //printer.Reset();
                        break;
                    case 117: //Printer ERROR OFF
                        timerError.stop();
                        timerBottomLine.start();
                        PRINTER_ERROR = false;
                        printer.Print(total);
                        relocateMyComponents();
                        break;
                    case 36://signal to print a ticket
                        if (!PRINTER_ERROR) {
                            total++;
                            lastClient = total - 1;
                            if (client3 == 0) {
                                nextClient = lastClient;
                                client3 = nextClient;
                            } else if (client3 > 0 && client4 == 0) {
                                client4 = lastClient;
                            }
                            relocateMyComponents();
                            variables.setLastClient(lastClient);
                            variables.setNextClient(nextClient);
                            printer.Print(total);
                            ticketsPrinted++;
                            variables.setTicketsPrinted(ticketsPrinted);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        //start blinking bottom line
        timerBottomLine.start();

        rootPanel.setFocusable(true);
        rootPanel.requestFocusInWindow();
        setVisible(true);

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

    private void initObjects(){
        timerBottomLine = new Timer(2000, new TimerListener(0));
        timerBottomLine.setInitialDelay(0);
        timerClient1 = new Timer(BLINKING_RATE, new TimerListener(1));
        timerClient1.setInitialDelay(0);
        timerClient2 = new Timer(BLINKING_RATE, new TimerListener(2));
        timerClient2.setInitialDelay(0);
        timerError = new Timer(BLINKING_RATE, new TimerListener(3));
        timerError.setInitialDelay(0);
        printer = new POS_PRINTER();
        errorSound = new Audio("/resources/notify.wav");
        notificationSound = new Audio("/resources/chimes.wav");
        variables = new XMLVARIABLES(APP.VARIABLES_PATH);
    }

    private void initVariables(){
        variables.setLastClient(variables.getLastClient() + 1);
        lastClient = variables.getLastClient();
        client1 = variables.getClientAsigned(1);
        client2 = variables.getClientAsigned(2);
        buttonClicked = variables.getButtonClicked();
        ticketsPrinted = variables.getTicketsPrinted();
        nextClient = variables.getNextClient();

        clicksToChangeBattery = variables.getClicksToChangeBattery();
        ticketsToInsertPaper = variables.getTicketsToInsertPaper();

        client3 = nextClient;
        if (nextClient == 0) {
            nextClient = lastClient;
            client3 = nextClient;
        } else if (nextClient > 0 && nextClient<lastClient) {
            client4 = nextClient + 1;
        }else{
            client4 = 0;
        }

        variables.setNextClient(nextClient);

        setRestOfClients();
    }

    private void setRestOfClients(){
        if (nextClient > 0){
            restOfClients = lastClient - nextClient +1;
        }else{
            restOfClients = 0;
        }
    }

    private void relocateMyComponents() {

        int titleHeight = h_percent * 12;
        int tableDataHeight = h_percent * 26;
        int dataHeight = h_percent * 16;
        int totalDataHeight = h_percent * 14;

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
        l_clientTitle.setSize(stringWidth, titleHeight - h_percent * 3);

        l_terminalTitle.setFont(new Font(fontName, Font.PLAIN, titleHeight));
        labelText = l_terminalTitle.getText();
        stringWidth = l_terminalTitle.getFontMetrics(l_terminalTitle.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 75) - (stringWidth / 2);
        h_loc = h_percent;
        l_terminalTitle.setLocation(w_loc, h_loc);
        l_terminalTitle.setSize(stringWidth, titleHeight - h_percent * 3);


        //=====================TABLE DATA ========================================


        l_client1.setText(String.valueOf(client1));
        l_client1.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client1.getText();
        stringWidth = l_client1.getFontMetrics(l_client1.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        h_loc = h_percent * 18;
        l_client1.setLocation(w_loc, h_loc);
        l_client1.setSize(stringWidth, tableDataHeight - h_percent * 5);

        l_client1_arrow.setText(">");
        l_client1_arrow.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client1_arrow.getText();
        stringWidth = l_client1_arrow.getFontMetrics(l_client1_arrow.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (stringWidth / 2);
        h_loc = h_percent * 18;
        l_client1_arrow.setLocation(w_loc, h_loc);
        l_client1_arrow.setSize(stringWidth, tableDataHeight - h_percent * 5);

        if (client1 == 0) {
            l_client1.setText("");
            l_client1_arrow.setText("");
        }

        l_client2.setText(String.valueOf(client2));
        l_client2.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client2.getText();
        stringWidth = l_client2.getFontMetrics(l_client2.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        h_loc = h_percent * 48;
        l_client2.setLocation(w_loc, h_loc);
        l_client2.setSize(stringWidth, tableDataHeight - h_percent * 5);

        l_client2_arrow.setText(">");
        l_client2_arrow.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client2_arrow.getText();
        stringWidth = l_client2_arrow.getFontMetrics(l_client2_arrow.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (stringWidth / 2);
        h_loc = h_percent * 48;
        l_client2_arrow.setLocation(w_loc, h_loc);
        l_client2_arrow.setSize(stringWidth, tableDataHeight - h_percent * 5);

        if (client2 == 0) {
            l_client2.setText("");
            l_client2_arrow.setText("");
        }


        l_terminal1.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_terminal1.getText();
        stringWidth = l_terminal1.getFontMetrics(l_terminal1.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 75) - (stringWidth / 2);
        h_loc = h_percent * 18;
        l_terminal1.setLocation(w_loc, h_loc);
        l_terminal1.setSize(stringWidth, tableDataHeight - h_percent * 5);

        l_terminal2.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_terminal2.getText();
        stringWidth = l_terminal2.getFontMetrics(l_terminal2.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 75) - (stringWidth / 2);
        h_loc = h_percent * 48;
        l_terminal2.setLocation(w_loc, h_loc);
        l_terminal2.setSize(stringWidth, tableDataHeight - h_percent * 5);


        //=====================DATA ========================================

        l_client3.setText(String.valueOf(client3));
        l_client3.setFont(new Font(fontName, Font.PLAIN, dataHeight));
        labelText = l_client3.getText();
        stringWidth = l_client3.getFontMetrics(l_client3.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        //h_loc = l_client2.getLocation().y + l_client2.getHeight() + h_percent * 6;
        h_loc = l_client4.getLocation().y - l_client3.getHeight();
        l_client3.setLocation(w_loc, h_loc);
        l_client3.setSize(stringWidth, dataHeight - h_percent * 3);

        //hiding this label:
        l_client3.setText("");

/*        if (client3 == 0) {
            l_client3.setText("");
        }*/

        l_client4.setText(String.valueOf(client4));
        l_client4.setFont(new Font(fontName, Font.PLAIN, dataHeight));
        labelText = l_client4.getText();
        stringWidth = l_client4.getFontMetrics(l_client4.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        //h_loc = l_client3.getLocation().y + l_client3.getHeight() + h_percent * 2;
        h_loc = formHeight - l_client4.getHeight() - h_percent;
        l_client4.setLocation(w_loc, h_loc);
        l_client4.setSize(stringWidth, dataHeight - h_percent * 3);

        //hiding this label:
        l_client4.setText("");

/*        if (client4 == 0) {
            l_client4.setText("");
        }*/


        //=====================TOTAL DATA ========================================

        setRestOfClients();
        l_total.setText(String.valueOf(restOfClients));
        l_total.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_total.getText();
        stringWidth = l_total.getFontMetrics(l_total.getFont()).stringWidth(labelText);
        //w_loc = l_totalTitle.getLocation().x + totalTitle_stringWidth + w_percent;
        w_loc = (w_percent * 101) - stringWidth - (w_percent * 2);
        h_loc = h_percent * 85;
        l_total.setLocation(w_loc, h_loc);
        l_total.setSize(stringWidth, totalDataHeight - h_percent * 2);


        l_totalTitle.setText("ВСЕГО В ОЧЕРЕДИ:");
        l_totalTitle.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_totalTitle.getText();
        int totalTitle_stringWidth = l_totalTitle.getFontMetrics(l_totalTitle.getFont()).stringWidth(labelText);
        w_loc = l_total.getLocation().x - (totalTitle_stringWidth) - (w_percent * 2);
        h_loc = h_percent * 85;
        l_totalTitle.setLocation(w_loc, h_loc);
        l_totalTitle.setSize(totalTitle_stringWidth, totalDataHeight - h_percent * 2);

        //===========================================================================

        if (PRINTER_ERROR){
            l_takeTicket.setText("ВСТАВЬТЕ БУМАГУ!");
        }else {
            l_takeTicket.setText("ВОЗЬМИТЕ БИЛЕТ");
        }
        l_takeTicket.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_takeTicket.getText();
        stringWidth = l_takeTicket.getFontMetrics(l_takeTicket.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (stringWidth/2);
        h_loc = h_percent * 85;
        l_takeTicket.setLocation(w_loc, h_loc);
        l_takeTicket.setSize(stringWidth, totalDataHeight - h_percent * 2);
        //the very first second they should not appear on screen:
        l_takeTicket.setText("");

        if (PRINTER_ERROR){
            l_total.setText("");
            l_totalTitle.setText("");
            l_client3.setText("");
            l_client4.setText("");
        }

        redrawLines();
    }

    private void redrawLines() {
        int left = 0;
        int right = formWidth;

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
        if (client4 > 0) {
            nextClient = client4;
            client3 = nextClient;
            if (client3 < lastClient) {
                client4++;
            } else {
                client4 = 0;
            }
        } else {
            nextClient = 0;
            client3 = nextClient;
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
        private int _timerClientNumber;

        public TimerListener(int timerClientNumber) {
            switch (timerClientNumber) {
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
                default:
                    break;
            }
            fg = label1.getForeground();
            bg = label1.getBackground();
            this._timerClientNumber = timerClientNumber;
        }

        public void actionPerformed(ActionEvent e) {

            switch (_timerClientNumber) {
                case 0:
                    if (!option1) {
                        label3.setText("");
                        label1.setText(String.valueOf(restOfClients));
                        label2.setText("ВСЕГО В ОЧЕРЕДИ:");
                    } else {
                        label1.setText("");
                        label2.setText("");
                        label3.setText("ВОЗЬМИТЕ БИЛЕТ");
                    }
                    option1 = !option1;
                    break;
                case 3:
                    if (option1) {
                        label1.setText("");
                    } else {
                        label1.setText("ВСТАВЬТЕ БУМАГУ!");
                    }
                    option1 = !option1;
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
                        switch (_timerClientNumber) {
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

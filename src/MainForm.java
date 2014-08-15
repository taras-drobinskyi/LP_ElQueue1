/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import externals.POS_PRINTER;
import helpers.APP;
import helpers.Audio;
import helpers.XMLVARIABLES;
import innerforms.ClientMessageForm;
import innerforms.SystemMessageForm;
import innerforms.interfaces.SystemMessageFormListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by forando on 06.04.14.
 * This is Main Form for Application
 */
public class MainForm extends JFrame {

    final static int TERMINAL_QUANTITY = 5;
    final static int[] clientHeightOffsets = {13, 30, 47, 64, 81};
    final static int[] widthOffsets = {30, 60, 85};

    //System Commands:
    final static int RESET_SYSTEM = 112;//F1
    final static int PRINTER_ERROR_ON = 113;//F2
    final static int PRINTER_ERROR_OFF = 114;//F3
    final static int STOP_SERVICE = 115;//F4
    final static int RESET_SERVICE = 116;//F5
    final static int TRIGGER_SERVICE = 117;//F6
    final static int PRINT_TICKET = 36;//HOME

    //Terminal Commands:
    final static int TERMINAL_BASE = 49;

    private static final Font BG_STRING_FONT = new Font(Font.SANS_SERIF,
            Font.BOLD, 72);
    private int bgStringX;
    private int bgStringY;


    static int standardBlinkRate;
    static int clicksToChangeBattery;
    static int ticketsToInsertPaper;
    static int takeTicketBlinkRate;
    int lastClient = 0;
    int nextClient = 0;
    int buttonClicked = 0;
    int ticketsPrinted = 0;
    Timer timerTicker;
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
    private JPanel mediaContentPanel;
    private JLabel l_client3_arrow;
    private JLabel l_client4_arrow;
    private JLabel l_client5;
    private JLabel l_client5_arrow;
    private JLabel l_terminal3;
    private JLabel l_terminal4;
    private JLabel l_terminal5;
    private JPanel videoPanel;
    private JPanel tickerPanel;
    private JLabel l_ticker;

    private Canvas canvas;

    List<JLabel> clients;
    List<JLabel> arrows;
    List<JLabel> terminals;
    List<Timer> clientTimers;

    List<String> tickerMessages;
    int tickerMessagesItem = 0;

    int[] clientValues;

    private int bottomPanelWidth;
    private int bottomPanelHeight;
    private int uiPanelWidth;
    private int uiPanelHeight;
    private int tickerPanelWidth;
    private int tickerPanelHeight;
    private int w_percent;
    private int h_percent;
    private Point hor_line1_p1 = new Point(100, 100);
    private Point hor_line1_p2 = new Point(200, 200);
    
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

        //examples.MyLayoutManager mgr = new examples.MyLayoutManager();

        //on these panels we locating components pragmatically
        mainUIPanel.setLayout(null);
        bottomPanel.setLayout(null);
        //tickerPanel.setLayout(null);

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

                relocateTitles();
               for (int i=0; i<clients.size(); i++){
                   relocateClientComponent(i);
               }
            }
        });

        mediaContentPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = e.getComponent().getBounds();
                System.out.println("Video Screen Size = " + r.getWidth() + "x" + r.getHeight());
                Component cmp = mediaContentPanel.getComponent(0);
                int width = (int)r.getWidth();
                int height = (int)r.getHeight();
                cmp.setBounds(0, 0, width, height);
            }
        });

        tickerPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = e.getComponent().getBounds();
                tickerPanelWidth = (int)r.getWidth();
                tickerPanelHeight = (int)r.getHeight();

                FontMetrics fontMetrics = getFontMetrics(BG_STRING_FONT);
                int h = fontMetrics.getHeight();

                bgStringX = tickerPanelWidth + 10;
                bgStringY = (tickerPanelHeight + h/2)/ 2;
                tickerPanel.repaint();
                timerTicker.stop();
                timerTicker.start();
                System.out.println("Another Ticker Panel Size " + tickerPanelWidth + "x" + tickerPanelHeight);
            }
        });

        bottomPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = e.getComponent().getBounds();
                bottomPanelWidth = (int)r.getWidth();
                bottomPanelHeight = (int)r.getHeight();
                relocateBottomComponents();
            }
        });

        rootPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println(e.getKeyCode());
                submitEvent(e.getKeyCode());
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

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //start blinking bottom line
                timerBottomLine.start();
            }
        });

        rootPanel.setFocusable(true);
        rootPanel.requestFocusInWindow();

        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);
        /*examples.MyLayoutManager.MouseDragger mouseDragger = mgr.new MouseDragger();
        mouseDragger.makeDraggable(canvas);*/

        videoPanel.add(canvas, BorderLayout.CENTER);

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
        EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
        mediaPlayer.setVideoSurface(videoSurface);
        mediaPlayer.playMedia(APP.VIDEO_PATH);

        setVisible(true);

    }

    private void submitEvent(int keyCode) {
        if(keyCode>=49 && keyCode<=57){
            int terminalNumber = keyCode - TERMINAL_BASE + 1;
            if (terminalNumber <= TERMINAL_QUANTITY) {
                assignTerminal(keyCode);
            }
        }else if((keyCode>=112 && keyCode<=123) || keyCode == 36){
            executeSystemCommand(keyCode);
        }
    }

    private void executeSystemCommand(int keyCode) {
        switch (keyCode) {
            case RESET_SYSTEM:
                total = 1;
                lastClient = 0;
                nextClient = 0;
                relocateTitles();
                for (int i=0; i<clients.size(); i++){
                    clientValues[i] = 0;
                    relocateClientComponent(i);
                }

                SERVICE_STOPPED = false;
                triggerService(SERVICE_STOPPED, true);

                batteryCheck();
                variables.setLastClient(lastClient);
                variables.setNextClient(nextClient);
                for (int i=0; i<clients.size(); i++){
                    variables.setClientAsigned(i+1, 0);
                }
                break;
            case PRINTER_ERROR_ON:
                if (!SERVICE_STOPPED) {
                    PRINTER_ERROR = true;
                    ticketsPrinted = 0;
                    variables.setTicketsPrinted(ticketsPrinted);
                    relocateBottomComponents();
                    timerBottomLine.stop();
                    timerError.start();
                    notificationSound.Stop();
                    errorSound.Play();
                    notificationSound.Reset();
                    lastClient++;
                    total = lastClient + 1;
                    variables.setLastClient(lastClient);
                    if (nextClient == 0) {
                        nextClient = lastClient;
                    }
                    variables.setNextClient(nextClient);
                }
                break;
            case PRINTER_ERROR_OFF:
                if (!SERVICE_STOPPED) {
                    timerError.stop();
                    timerBottomLine.start();
                    PRINTER_ERROR = false;
                    printer.Print(total);
                    relocateBottomComponents();
                }
                break;
            case STOP_SERVICE:
                SERVICE_STOPPED = true;
                triggerService(SERVICE_STOPPED);
                break;
            case RESET_SERVICE:
                SERVICE_STOPPED = false;
                triggerService(SERVICE_STOPPED);
                break;
            case TRIGGER_SERVICE:
                SERVICE_STOPPED = !SERVICE_STOPPED;
                triggerService(SERVICE_STOPPED);
                break;
            case PRINT_TICKET:
                if(!TICKET_IS_PRINTING) {
                    total++;
                    lastClient = total - 1;
                    if (nextClient == 0) {
                        nextClient = lastClient;
                    }
                    relocateBottomComponents();
                    variables.setLastClient(lastClient);
                    variables.setNextClient(nextClient);
                    printTicket();
                }
                break;
            default:
                break;
        }
    }

    private void assignTerminal(int keyCode) {

        int terminalIndex = keyCode - TERMINAL_BASE;
        if (nextClient > 0) {
            clientValues[terminalIndex] = nextClient;
            if (nextClient < lastClient) {
                nextClient ++;
            } else{
                nextClient = 0;
            }
            relocateClientComponent(terminalIndex);
            relocateBottomComponents();
            clientTimers.get(terminalIndex).start();
            notificationSound.Play();
            variables.setClientAsigned(terminalIndex + 1, clientValues[terminalIndex]);
            variables.setNextClient(nextClient);
        }
        buttonClicked++;
        variables.setButtonClicked(buttonClicked);
    }

    /**
     * By Service means possibility to accept new clients
     * @param turnOn Flag that indicates whether Service must be stopped
     * @param flags [Optional] Flags array, now consists only of one item that
     * indicates whether System is resetting to 0 values
     */
    private void triggerService (boolean turnOn, boolean... flags){
        boolean resettingSystem = false;
        if (flags.length>0){
            resettingSystem = flags[0];
        }
        if (turnOn){
            if (PRINTER_ERROR){
                timerError.stop();
            }else {
                timerBottomLine.stop();
            }
            relocateBottomComponents();
            timerServiceStopped.start();
        }else{
            timerServiceStopped.stop();
            relocateBottomComponents();
            if (PRINTER_ERROR){
                timerError.start();
            }else {
                timerBottomLine.start();
                if (TICKET_TAKEN || resettingSystem) {
                    printTicket();
                }
            }
        }
    }

    private void batteryCheck() {
        if (buttonClicked > clicksToChangeBattery) {
            errorSound.Play();
            //The next code will be invoked after main form is finally resized.
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    int width = mediaContentPanel.getSize().width;
                    int height = mediaContentPanel.getSize().height;
                    SystemMessageForm f = new SystemMessageForm(width, height);
                    f.addMessageFormListener(new SystemMessageFormListener() {
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

    private void initObjects() {

        clients = Arrays.asList(l_client1, l_client2, l_client3, l_client4, l_client5);
        arrows = Arrays.asList(l_client1_arrow, l_client2_arrow, l_client3_arrow, l_client4_arrow, l_client5_arrow);
        terminals = Arrays.asList(l_terminal1, l_terminal2, l_terminal3, l_terminal4, l_terminal5);
        clientTimers = new ArrayList<Timer>();

        for(int i=0; i< clients.size(); i++){
            //clients.get(i).setText(String.valueOf(i+1));
            Timer timer = new Timer(standardBlinkRate, new ClientTimerListener(i, i, clients.get(i), arrows.get(i)));
            timer.setInitialDelay(0);
            clientTimers.add(timer);
        }

        timerBottomLine = new Timer(takeTicketBlinkRate, new SystemTimerListener(0));
        timerBottomLine.setInitialDelay(0);
        timerError = new Timer(standardBlinkRate, new SystemTimerListener(3));
        timerError.setInitialDelay(0);
        timerServiceStopped = new Timer(takeTicketBlinkRate, new SystemTimerListener(4));
        timerServiceStopped.setInitialDelay(0);
        timerPrinter = new Timer(1000, new SystemTimerListener(5));
        timerPrinter.setInitialDelay(1000);

        timerTicker = new Timer(20, new TimerTicker(l_ticker));
        timerTicker.setInitialDelay(0);

        /*SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("TickerPanel Size " + bgStringX + "x" + bgStringY);
                new Timer(20, new TimerTicker(l_ticker)).start();
            }
        });*/

        printer = new POS_PRINTER();
        errorSound = new Audio("/resources/notify.wav");
        notificationSound = new Audio("/resources/chimes.wav");
    }

    private void initVariables() {

        variables = new XMLVARIABLES(APP.VARIABLES_PATH);

        variables.setLastClient(variables.getLastClient() + 1);
        lastClient = variables.getLastClient();

        clientValues = new int[TERMINAL_QUANTITY];

        for (int i=0; i<TERMINAL_QUANTITY; i++){
            clientValues[i] = variables.getClientAsigned(i+1);
        }

        buttonClicked = variables.getButtonClicked();
        ticketsPrinted = variables.getTicketsPrinted();
        nextClient = variables.getNextClient();
        tickerMessages = variables.getMessages();

        clicksToChangeBattery = variables.getClicksToChangeBattery();
        ticketsToInsertPaper = variables.getTicketsToInsertPaper();
        standardBlinkRate = variables.getStandardBlinkRate();
        takeTicketBlinkRate = variables.getTakeTicketBlinkRate();

        if (nextClient == 0) {
            nextClient = lastClient;
        }

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

    private void relocateTitles() {

        int titleHeight = h_percent * 8;

        int w_loc;
        int h_loc;

        String labelText;
        int stringWidth;
        String fontName = l_clientTitle.getFont().getName();


        l_clientTitle.setFont(new Font(fontName, Font.PLAIN, titleHeight));
        labelText = l_clientTitle.getText();
        stringWidth = l_clientTitle.getFontMetrics(l_clientTitle.getFont()).stringWidth(labelText);
        w_loc = (w_percent * widthOffsets[0]) - (stringWidth / 2);
        h_loc = h_percent;
        l_clientTitle.setLocation(w_loc, h_loc);
        l_clientTitle.setSize(stringWidth, titleHeight - h_percent * 2);

        l_terminalTitle.setFont(new Font(fontName, Font.PLAIN, titleHeight));
        labelText = l_terminalTitle.getText();
        stringWidth = l_terminalTitle.getFontMetrics(l_terminalTitle.getFont()).stringWidth(labelText);
        w_loc = (w_percent * widthOffsets[2]) - (stringWidth / 2);
        h_loc = h_percent;
        l_terminalTitle.setLocation(w_loc, h_loc);
        l_terminalTitle.setSize(stringWidth, titleHeight - h_percent * 2);
        redrawLines();
    }

    private void relocateClientComponent(int clientIndex){
        JLabel client = clients.get(clientIndex);
        JLabel arrow = arrows.get(clientIndex);
        JLabel terminal = terminals.get(clientIndex);

        int val = clientValues[clientIndex];


        int fontHeight = h_percent * 16;
        int h_offset = clientHeightOffsets[clientIndex];

        int w_loc;
        int h_loc;

        String labelText;
        int stringWidth;
        String fontName = client.getFont().getName();

        client.setText(String.valueOf(val));
        client.setFont(new Font(fontName, Font.PLAIN, fontHeight));
        labelText = client.getText();
        stringWidth = client.getFontMetrics(client.getFont()).stringWidth(labelText);
        w_loc = (w_percent * widthOffsets[0]) - (stringWidth / 2);
        h_loc = h_percent * h_offset;
        client.setLocation(w_loc, h_loc);
        client.setSize(stringWidth, fontHeight - h_percent * 3);

        arrow.setText(">");
        arrow.setFont(new Font(fontName, Font.PLAIN, fontHeight));
        labelText = arrow.getText();
        stringWidth = arrow.getFontMetrics(arrow.getFont()).stringWidth(labelText);
        w_loc = (w_percent * widthOffsets[1]) - (stringWidth / 2);
        h_loc = h_percent * h_offset;
        arrow.setLocation(w_loc, h_loc);
        arrow.setSize(stringWidth, fontHeight - h_percent * 3);

        terminal.setText( String.valueOf(clientIndex +1));
        terminal.setFont(new Font(fontName, Font.PLAIN, fontHeight));
        labelText = terminal.getText();
        stringWidth = terminal.getFontMetrics(terminal.getFont()).stringWidth(labelText);
        w_loc = (w_percent * widthOffsets[2]) - (stringWidth / 2);
        h_loc = h_percent * h_offset;
        terminal.setLocation(w_loc, h_loc);
        terminal.setSize(stringWidth, fontHeight - h_percent * 3);

        if (val == 0) {
            client.setText("");
            arrow.setText("");
            terminal.setText("");
        }
    }

    private void relocateBottomComponents() {

        int h_percent = bottomPanelHeight / 100;
        int w_percent = bottomPanelWidth / 100;
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

        if (PRINTER_ERROR || SERVICE_STOPPED) {
            l_total.setText("");
            l_totalTitle.setText("");
        }

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
    }

    private void redrawLines() {
        int left = 25;
        int right = uiPanelWidth -25;

        int correction = 40;

        hor_line1_p1 = new Point(left, correction + l_clientTitle.getHeight());
        hor_line1_p2 = new Point(right, correction + l_clientTitle.getHeight());

        mainUIPanel.repaint();
    }

    /**
     * Overridden method-placeholder for components instantiation
     * <br>
     * (If you want to override component instantiation, than do it here)
     */
    private void createUIComponents() {
        mainUIPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.white);
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(8,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                Line2D h_lin1 = new Line2D.Float(hor_line1_p1.x, hor_line1_p1.y, hor_line1_p2.x, hor_line1_p2.y);
                g2.draw(h_lin1);
            }
        };

        tickerPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g.setFont(BG_STRING_FONT);
                g.setColor(Color.WHITE);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.drawString(tickerMessages.get(tickerMessagesItem), bgStringX, bgStringY);
            }
        };
    }

    private class ClientTimerListener implements ActionListener{

        private int client;
        private int terminal;
        private JLabel label_client;
        private JLabel label_arrow;
        private Color bg;
        private Color fg;
        private boolean isForeground = true;
        private final static int maxBlinking = 4;
        private int alreadyBlinked = 0;
        private boolean clientMessageFormIsShown = false;
        ClientMessageForm form;

        private ClientTimerListener(int client, int terminal, JLabel label1, JLabel label2) {
            this.client = client;
            this.terminal = terminal;
            this.label_client = label1;
            this.label_arrow = label2;
            this.fg = label1.getForeground();
            this.bg = label1.getBackground();
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            if (alreadyBlinked <= maxBlinking * 2) {
                alreadyBlinked++;
                if (isForeground) {
                    label_client.setForeground(fg);
                    label_arrow.setForeground(fg);
                } else {
                    label_client.setForeground(bg);
                    label_arrow.setForeground(bg);
                }
                isForeground = !isForeground;
                if (!clientMessageFormIsShown){
                    clientMessageFormIsShown = true;
                    int width = mediaContentPanel.getSize().width;
                    int height = mediaContentPanel.getSize().height;
                    System.out.println("ClientMessageForm dimensions are " + width + "x" + height);
                    form = new ClientMessageForm(width, height, label_client.getText(), String.valueOf(terminal+1));
                }
            } else {
                alreadyBlinked = 0;
                isForeground = true;
                clientTimers.get(client).stop();
                clientMessageFormIsShown = false;
                form.dispose();
            }
        }
    }

    private class SystemTimerListener implements ActionListener {
        private JLabel label1;
        private JLabel label2;
        private JLabel label3;
        private boolean option1 = true;
        private int _optionNumber;

        public SystemTimerListener(int optionNumber) {
            switch (optionNumber) {
                case 0: //initialize bottom line blinking
                    this.label1 = l_total;
                    this.label2 = l_totalTitle;
                    this.label3 = l_takeTicket;
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
                    break;
            }
        }
    }

    private class TimerTicker implements ActionListener{

        JLabel label;
        int stringWidth;
        int x = 1000; //innitial value
        int tickerMessQuant;

        public TimerTicker(JLabel _label){
            this.label = _label;

            FontMetrics fontMetrics = getFontMetrics(BG_STRING_FONT);
            stringWidth = fontMetrics.stringWidth(tickerMessages.get(tickerMessagesItem));

            this.tickerMessQuant = tickerMessages.size();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            x-= 5;
            if((stringWidth + x) >= -1) {
                bgStringX = x;
                tickerPanel.repaint();
            }else{
                x = tickerPanelWidth + 10;
                if (tickerMessagesItem<tickerMessQuant - 1) {
                    tickerMessagesItem++;
                    FontMetrics fontMetrics = getFontMetrics(BG_STRING_FONT);
                    stringWidth = fontMetrics.stringWidth(tickerMessages.get(tickerMessagesItem));
                }else{
                    tickerMessagesItem = 0;
                    FontMetrics fontMetrics = getFontMetrics(BG_STRING_FONT);
                    stringWidth = fontMetrics.stringWidth(tickerMessages.get(tickerMessagesItem));
                }
                tickerPanel.repaint();
            }
        }
    }
}

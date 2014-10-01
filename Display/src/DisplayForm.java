/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import display.TerminalData;
import interfaces.ClientMessageFormListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

/**
 * Created by forando on 06.04.14.
 * This is Main Form for Application
 */
public class DisplayForm extends JFrame implements ClientServer.ClientServerListener {

    private int id = -1;

    final static int[] terminalHeightOffsets = {27, 44, 61, 78, 95};
    final static int[] widthOffsets = {30, 60, 85};
    private int standardBlinkRate;
    private int takeTicketBlinkRate;

    HashMap<String, String> currentVideo;

    //Terminal Commands:
    final static int TERMINAL_BASE = 49;

    private static final Font BG_STRING_FONT = new Font(Font.SANS_SERIF,
            Font.BOLD, 72);

    private Font TABLE_FONT = new Font(Font.DIALOG,
            Font.PLAIN, 42);

    private ClientServer clientServer;


    private int bgStringX;
    private int bgStringY;

    Timer timerTicker;
    Timer timerBottomLine;
    Timer timerError;
    Timer timerServiceStopped;
    Timer timerPrinter;
    Audio notificationSound;
    Audio errorSound;
    //POS_PRINTER printer;

    private boolean PRINTER_ERROR = false;
    private boolean SERVICE_STOPPED = false;

    private JPanel rootPanel;
    private JLabel l_clientTitle;
    private JLabel l_terminalTitle;
    private JLabel l_totalTitle;
    private JLabel l_total;
    private JLabel l_takeTicket;
    public MainUIPanel mainUIPanel;
    private JLabel l_serviceStopped;
    private JPanel bottomPanel;
    private JPanel mediaContentPanel;
    private JPanel videoPanel;
    private JPanel tickerPanel;
    private JLabel l_ticker;

    private Canvas canvas;

    List<String> tickerMessages;
    int tickerMessagesItem = 0;

    private int bottomPanelWidth;
    private int bottomPanelHeight;
    private int uiPanelWidth;
    private int uiPanelHeight;
    private int tickerPanelWidth;
    private int tickerPanelHeight;
    private int w_percent_uiPanel;
    private int h_percent_uiPanel;
    private Point hor_line1_p1 = new Point(100, 100);
    private Point hor_line1_p2 = new Point(200, 200);

    public DisplayForm(){
        //Form Title
        super("Продукт Компании \"ВЕРСИЯ\"");
        startClientServer();
    }

    public void initForm(List<TerminalData> terminals, int restOfClients) {



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

        //initVariables();
        List<DisplayForm.MainUIPanel.TerminalRow> table = mainUIPanel.getTable();
        XMLVARIABLES variables = new XMLVARIABLES(APP.VARIABLES_PATH);
        standardBlinkRate = variables.getStandardBlinkRate();
        takeTicketBlinkRate = variables.getTakeTicketBlinkRate();
        tickerMessages = variables.getMessages();
        currentVideo = variables.getCurrentVieoData();
        initObjects();

        /*//Print first ticket
        total = lastClient + 1;
        printer.Print(total);*/

        mainUIPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = e.getComponent().getBounds();
                uiPanelWidth = (int) r.getWidth();
                uiPanelHeight = (int) r.getHeight();
                w_percent_uiPanel = uiPanelWidth / 100;
                h_percent_uiPanel = uiPanelHeight / 100;

                relocateTitles();
                relocateResizedTerminalRorws();
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

                int keyCode = e.getKeyCode();
                if(keyCode>=49 && keyCode<=57){
                    int terminalNumber = keyCode - TERMINAL_BASE + 1;
                    if (terminalNumber <= APP.TERMINAL_QUANTITY) {
                        keyCode = keyCode - TERMINAL_BASE;
                    }
                }
                submitEvent(keyCode);
            }
        });

        //subscribing to Window(or JFrame) Opened Event
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                //batteryCheck();
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
        mediaPlayer.playMedia(currentVideo.get("path"));

        mainUIPanel.restOfClients = restOfClients;
        mainUIPanel.initClients(terminals);

        setVisible(true);
    }

    private void startClientServer(){
        clientServer = new ClientServer(APP.IP, APP.PORT, SocketMessage.DISPLAY, id);
        clientServer.addClientServerListener(this);
        clientServer.startClient();
    }

    private void restartClientServer(){
        clientServer.stopClient();
        clientServer = null;
        clientServer = new ClientServer(APP.IP, APP.PORT, SocketMessage.DISPLAY, id);
        clientServer.addClientServerListener(this);
        clientServer.startClient();
    }

    private void stopClientServer(){
        clientServer.stopClient();
        clientServer = null;
    }

    public void submitEvent(int keyCode) {
        if(keyCode>=0 && keyCode<=APP.MAX_TERMINAL_QUANTITY){
            int terminalNumber = keyCode - TERMINAL_BASE + 1;
            if (terminalNumber <= APP.TERMINAL_QUANTITY) {
                assignTerminal(keyCode);
            }
        }else if((keyCode>=112 && keyCode<=123) || keyCode == 36){
            int command = -1;
            switch (keyCode){
                case 112://F1
                    command = APP.RESET_SYSTEM;
                    break;
                case 113://F2
                    command = APP.PRINTER_ERROR_ON;
                    break;
                case 114://F3
                    command = APP.PRINTER_ERROR_OFF;
                    break;
                case 115://F4
                    command = APP.STOP_SERVICE;
                    break;
                case 116://F5
                    command = APP.RESET_SERVICE;
                    break;
                case 117://F6
                    command = APP.TRIGGER_SERVICE;
                    break;
                case 36://HOME
                    command = APP.PRINT_TICKET;
                    break;
                default:
                    break;
            }
            executeSystemCommand(command);
        }
    }

    private void executeSystemCommand(int command) {
        switch (command) {
            /*case APP.RESET_SYSTEM:
                total = 1;
                lastClient = 0;
                nextClient = 0;
                relocateTitles();
                for (int i=0; i< APP.TERMINAL_QUANTITY; i++){
                    clientValues[i] = 0;
                    //relocateTerminalRows();
                }
                relocateTerminalRows();

                SERVICE_STOPPED = false;
                triggerService(SERVICE_STOPPED, true);

                batteryCheck();
                variables.setLastClient(lastClient);
                variables.setNextClient(nextClient);
                for (int i=0; i< APP.TERMINAL_QUANTITY; i++){
                    MainUIPanel.TerminalRow row = mainUIPanel.getTable().get(i);
                    row.clientNumber = 0;
                    row.saveToXML();
                }
                break;*/
            case APP.PRINTER_ERROR_ON:
                if (!SERVICE_STOPPED) {
                    PRINTER_ERROR = true;
                    /*ticketsPrinted = 0;
                    variables.setTicketsPrinted(ticketsPrinted);*/
                    relocateBottomComponents();
                    timerBottomLine.stop();
                    timerError.start();
                    notificationSound.Stop();
                    errorSound.Play();
                    notificationSound.Reset();
                    /*lastClient++;
                    total = lastClient + 1;
                    variables.setLastClient(lastClient);
                    if (nextClient == 0) {
                        nextClient = lastClient;
                    }
                    variables.setNextClient(nextClient);*/
                }
                break;
            case APP.PRINTER_ERROR_OFF:
                if (!SERVICE_STOPPED) {
                    timerError.stop();
                    timerBottomLine.start();
                    PRINTER_ERROR = false;
                    //printer.Print(total);
                    relocateBottomComponents();
                }
                break;
            case APP.STOP_SERVICE:
                SERVICE_STOPPED = true;
                //triggerService(SERVICE_STOPPED);
                stopService();
                break;
            case APP.RESET_SERVICE:
                SERVICE_STOPPED = false;
                //triggerService(SERVICE_STOPPED);
                resetService();
                break;
            /*case APP.TRIGGER_SERVICE:
                SERVICE_STOPPED = !SERVICE_STOPPED;
                triggerService(SERVICE_STOPPED);
                break;
            case APP.PRINT_TICKET:
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
                break;*/
            default:
                break;
        }
    }

    private void assignTerminal(int keyCode) {


        int terminalIndex = keyCode;
        MainUIPanel.TerminalRow row = mainUIPanel.getTerminalRow(terminalIndex);
        System.out.println("assignTerminal keyCode = " + keyCode);

        if (row.state == MainUIPanel.TerminalRow.ACCEPTED) {
            List<TerminalData> terminals = new ArrayList<>();
            terminals.add(new TerminalData(row.levelIndex, row.clientNumber,
                    row.terminalNumber, row.visible, row.state));
            clientServer.send(new DisplayMessage(this.id, DisplayMessage.ADD_ROW,
                    terminals, 0, new Date(), true));
            /*if (nextClient > 0) {
                clientValues[terminalIndex] = nextClient;
                if (nextClient < lastClient) {
                    nextClient++;
                } else {
                    nextClient = 0;
                }
                row.clientNumber = clientValues[terminalIndex];
                row.saveToXML();
                row.resetFromXML();
                relocateTerminalRows();
                row.performAnimation();
                relocateBottomComponents();
                notificationSound.Play();
                variables.setNextClient(nextClient);
            }*/
        }else if (row.state == MainUIPanel.TerminalRow.WAITING){
            //row.performAnimation();
            List<TerminalData> terminals = new ArrayList<>();
            terminals.add(new TerminalData(row.levelIndex, row.clientNumber,
                    row.terminalNumber, row.visible, row.state));
            clientServer.send(new DisplayMessage(this.id, DisplayMessage.DELETE_ROW,
                    terminals, 0, new Date(), true));
        }
        /*buttonClicked++;
        variables.setButtonClicked(buttonClicked);*/
    }

    private void addRow(TerminalData terminalRowData){
        MainUIPanel.TerminalRow row = mainUIPanel.getTerminalRow(terminalRowData.terminalNumber);
        if (row.state != MainUIPanel.TerminalRow.ACCEPTED){
            row.state = MainUIPanel.TerminalRow.ACCEPTED;
            System.err.println("TerminalRow with terminalNumber=" + row.terminalNumber +
                    "was asked to perform SlideUp animation. But its state was not equal to: ACCEPTED. " +
                    "We've set this value manually" );

        }
        row.clientNumber = terminalRowData.clientNumber;
        relocateTerminalRows();
        row.performAnimation();
        relocateBottomComponents();
        notificationSound.Play();
    }

    private void deleteRow(TerminalData terminalRowData){
        MainUIPanel.TerminalRow row = mainUIPanel.getTerminalRow(terminalRowData.terminalNumber);
        if (row.state != MainUIPanel.TerminalRow.WAITING){
            row.state = MainUIPanel.TerminalRow.WAITING;
            System.err.println("TerminalRow with terminalNumber=" + row.terminalNumber +
                    "was asked to perform SlideAside animation. But its state was not equal to: WAITING. " +
                    "We've set this value manually" );

        }
        row.performAnimation();
    }
/*
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
            }*//*else {
                timerBottomLine.start();
                if (TICKET_TAKEN || resettingSystem) {
                    printTicket();
                }
            }*//*
        }
    }*/

    private void stopService(){
        if (PRINTER_ERROR){
            timerError.stop();
        }else {
            timerBottomLine.stop();
        }
        relocateBottomComponents();
        timerServiceStopped.start();
    }

    private void resetService(){
        timerServiceStopped.stop();
        relocateBottomComponents();
        if (PRINTER_ERROR){
            timerError.start();
        }else{
            timerBottomLine.start();
        }
    }

    /*private void printTicket(){
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
    }*/

    private void initObjects() {

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

        errorSound = new Audio("/files/notify.wav");
        notificationSound = new Audio("/files/chimes.wav");
    }

    private void relocateTitles() {

        int titleHeight = h_percent_uiPanel * 8;

        int w_loc;
        int h_loc;

        String labelText;
        int stringWidth;
        String fontName = l_clientTitle.getFont().getName();


        l_clientTitle.setFont(new Font(fontName, Font.PLAIN, titleHeight));
        labelText = l_clientTitle.getText();
        stringWidth = l_clientTitle.getFontMetrics(l_clientTitle.getFont()).stringWidth(labelText);
        w_loc = (w_percent_uiPanel * widthOffsets[0]) - (stringWidth / 2);
        h_loc = h_percent_uiPanel;
        l_clientTitle.setLocation(w_loc, h_loc);
        l_clientTitle.setSize(stringWidth, titleHeight - h_percent_uiPanel * 2);

        l_terminalTitle.setFont(new Font(fontName, Font.PLAIN, titleHeight));
        labelText = l_terminalTitle.getText();
        stringWidth = l_terminalTitle.getFontMetrics(l_terminalTitle.getFont()).stringWidth(labelText);
        w_loc = (w_percent_uiPanel * widthOffsets[2]) - (stringWidth / 2);
        h_loc = h_percent_uiPanel;
        l_terminalTitle.setLocation(w_loc, h_loc);
        l_terminalTitle.setSize(stringWidth, titleHeight - h_percent_uiPanel * 2);
        redrawLines();
    }

    private void relocateTerminalRows(){

        int fontHeight = h_percent_uiPanel * 16;

        TABLE_FONT = new Font(Font.DIALOG, Font.PLAIN, fontHeight);
        FontMetrics fontMetrics = getFontMetrics(TABLE_FONT);

        for (MainUIPanel.TerminalRow r : mainUIPanel.getTable()){
            int[] xpos = new int[3];
            int stringWidth = fontMetrics.stringWidth(String.valueOf(r.clientNumber));
            xpos[0] = (w_percent_uiPanel * widthOffsets[0]) - (stringWidth / 2);
            stringWidth = fontMetrics.stringWidth(">");
            xpos[1] = (w_percent_uiPanel * widthOffsets[1]) - (stringWidth / 2);
            stringWidth = fontMetrics.stringWidth(String.valueOf(r.terminalNumber));
            xpos[2] = (w_percent_uiPanel * widthOffsets[2]) - (stringWidth / 2);
            r.xpos = xpos;
        }
        mainUIPanel.repaint();
    }

    private void relocateResizedTerminalRorws(){
        int fontHeight = h_percent_uiPanel * 16;

        TABLE_FONT = new Font(Font.DIALOG, Font.PLAIN, fontHeight);
        FontMetrics fontMetrics = getFontMetrics(TABLE_FONT);

        for (MainUIPanel.TerminalRow r : mainUIPanel.getTable()){
            if (r.visible) {
                int h_offset = terminalHeightOffsets[r.levelIndex];
                r.ypos = h_percent_uiPanel * h_offset;
            }
            int[] xpos = new int[3];
            int stringWidth = fontMetrics.stringWidth(String.valueOf(r.clientNumber));
            xpos[0] = (w_percent_uiPanel * widthOffsets[0]) - (stringWidth / 2);
            stringWidth = fontMetrics.stringWidth(">");
            xpos[1] = (w_percent_uiPanel * widthOffsets[1]) - (stringWidth / 2);
            stringWidth = fontMetrics.stringWidth(String.valueOf(r.terminalNumber));
            xpos[2] = (w_percent_uiPanel * widthOffsets[2]) - (stringWidth / 2);
            r.xpos = xpos;
        }
        mainUIPanel.repaint();
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
        //w_loc = l_total.getLocation().x - (totalTitle_stringWidth) - (w_percent_uiPanel * 4);
        h_loc = h_percent * 30;
        l_totalTitle.setLocation(w_loc, h_loc);
        l_totalTitle.setSize(totalTitle_stringWidth, totalDataHeight + h_percent * 3);

        l_total.setText(String.valueOf(mainUIPanel.restOfClients));
        l_total.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_total.getText();
        stringWidth = l_total.getFontMetrics(l_total.getFont()).stringWidth(labelText);
        w_loc = l_totalTitle.getLocation().x + totalTitle_stringWidth + w_percent;
        //w_loc = (w_percent_uiPanel * 101) - stringWidth - (w_percent_uiPanel * 2);
        //w_loc = width - stringWidth - (h_percent_uiPanel*4);
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
        //h_loc = h_percent_uiPanel * 85;
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
        //h_loc = h_percent_uiPanel * 85;
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
        mainUIPanel = new MainUIPanel();
        System.out.println("createUIComponents!!!");

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
                /*case 5: //ticket is printing
                    //no neccessity to init something (we do it just to escape an error):
                    this.label1 = l_takeTicket;
                    this.label2 = l_serviceStopped;
                    break;*/
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
                        label1.setText(String.valueOf(mainUIPanel.restOfClients));
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
                /*case 5://ticket has been printed
                    TICKET_IS_PRINTING = false;
                    timerPrinter.stop();
                    break;*/
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

    public class MainUIPanel extends JPanel{
        private List<TerminalRow> table;
        private boolean tableIsValid = false;

        private int USEDLevels = 0;
        private int levelsToBeUSED = 0;

        private int restOfClients = 0;

        private boolean isRowsSliding = false;
        private boolean isOnHoldTerminals = false;

        private ClientMessageForm form;

        /*private MainUIPanel() {
            initClients();
        }*/

        private void initClients(List<TerminalData> terminalRows){
            //XMLVARIABLES variables = new XMLVARIABLES(APP.VARIABLES_PATH);

            //USEDLevels = variables.getUSEDlevels();
            table = new ArrayList<>();

            for(int i=0; i< APP.TERMINAL_QUANTITY; i++){
                TerminalData terminalData = terminalRows.get(i);
                TerminalRow terminalRow = new TerminalRow(terminalData);
                terminalRow.addTerminalRowListener(new TerminalRowListener() {
                    @Override
                    public void onTransitionCompleted(TerminalRow row) {
                        (new RowsSlideUPRunner(row)).start();
                        for (DisplayFormListener l : displayFormListeners){
                            l.onAcceptClient(row.terminalNumber, row.clientNumber);
                            System.out.println("MainFormListener onAcceptClient");
                        }
                    }

                    @Override
                    public void onShowMessageForm(TerminalRow row) {
                        int width = mediaContentPanel.getSize().width;
                        int height = mediaContentPanel.getSize().height;
                        if (form == null) {
                            form = new ClientMessageForm(width, height, row.clientNumber, row.terminalNumber + 1);
                            form.addClientMessageFormListener(new ClientMessageFormListener() {
                                @Override
                                public void onClose() {
                                    form.dispose();
                                    form = null;
                                }

                                @Override
                                public void onKeyPressed(int keyCode) {
                                    if(keyCode>=49 && keyCode<=57){
                                        int terminalNumber = keyCode - TERMINAL_BASE + 1;
                                        if (terminalNumber <= APP.TERMINAL_QUANTITY) {
                                            keyCode = keyCode - TERMINAL_BASE;
                                        }
                                    }
                                    submitEvent(keyCode);
                                }
                            });
                        }else {
                            form.addMessage(row.clientNumber, row.terminalNumber + 1);
                        }
                        for (DisplayFormListener l : displayFormListeners){
                            l.onAssignClient(row.terminalNumber, row.clientNumber);
                            System.out.println("MainFormListener onAssignClient");
                        }
                    }

                    @Override
                    public void onDisposeMessageForm(TerminalRow row) {
                        if (form != null) {
                            form.removeMessage(row.terminalNumber + 1);
                        }
                    }
                });
                table.add(terminalRow);
                if (terminalRow.levelIndex>=0) USEDLevels++;
            }
            initialTerminalAssignmentCheck();
            tableIsValid = true;
            System.out.println("USED LEVELS = " + getUSEDLevels());
        }

        public void reAssignTerminals(List<TerminalData> terminalRows){
            for (TerminalRow row : table){
                int terminal = row.terminalNumber;
                TerminalData terminalData = terminalRows.get(terminal);
                row.levelIndex = terminalData.levelIndex;
                row.clientNumber = terminalData.clientNumber;
                row.terminalNumber = terminalData.terminalNumber;
                row.visible = terminalData.visible;
                row.state = terminalData.state;
            }
        }

        public List<TerminalRow> getTable(){return table;}

        public TerminalRow getTerminalRow(int terminal){
            for (TerminalRow row : table){
                if (row.terminalNumber == terminal) return row;
            }
            return null;
        }

        public synchronized void setUSEDLevels(int val){
            USEDLevels = val;
            //variables.setUSEDlevels(val);
        }

        public synchronized int getUSEDLevels(){
            return USEDLevels;
        }

        public void initialTerminalAssignmentCheck(){
            if (USEDLevels >= APP.LEVEL_QUANTITY){
                sendOnHoldTerminals(1);
            }
            levelsToBeUSED = USEDLevels;
        }

        private synchronized void checkForTerminalsOnHoldSet(){
            levelsToBeUSED++;
            System.out.println("checkForTerminalsOnHoldSet levelsToBeUSED = " + levelsToBeUSED +
                    " LEVEL_QUANTITY = " + APP.LEVEL_QUANTITY);
            System.out.println("isOnHoldTerminals = " + isOnHoldTerminals);
            if (levelsToBeUSED >= APP.LEVEL_QUANTITY && !isOnHoldTerminals) {
                isOnHoldTerminals = true;
                System.out.println("checkForTerminalsOnHoldSet sendOnHoldTerminals val = " + 1);
                sendOnHoldTerminals(1);
            }
        }

        public synchronized void checkForTerminalsOnHoldRelease(){
            levelsToBeUSED--;
            System.out.println("checkForTerminalsOnHoldRelease levelsToBeUSED = " + levelsToBeUSED +
            " LEVEL_QUANTITY = " + APP.LEVEL_QUANTITY);
            System.out.println("isOnHoldTerminals = " + isOnHoldTerminals);
            if (levelsToBeUSED < APP.LEVEL_QUANTITY && isOnHoldTerminals) {
                isOnHoldTerminals = false;
                System.out.println("checkForTerminalsOnHoldRelease sendOnHoldTerminals val = " + 0);
                sendOnHoldTerminals(0);
            }
        }

        private void sendOnHoldTerminals(int val){
            List<TerminalData> listToSend = new ArrayList<>();
            for (TerminalRow row : table){
                if (row.state != TerminalRow.CALLING && row.state != TerminalRow.WAITING){
                    listToSend.add(new TerminalData(row.levelIndex, row.clientNumber,
                            row.terminalNumber, row.visible, row.state));
                }
            }
            //int[] terminals = new int[list.size()];
            System.out.println("sendOnHoldTerminals table.size() = " + table.size());
            System.out.println("sendOnHoldTerminals list.size() = " + listToSend.size());
            /*for (int i=0; i<list.size(); i++){
                terminals[i] = list.get(i);
            }*/
            /*for (MainFormListener l : mainFormListeners){
                l.onHoldTerminals(terminals, val);
            }*/
            if (val ==1) {
                clientServer.send(new DisplayMessage(0, SocketMessage.HOLD_CLIENT, listToSend, 0, new Date(), true));
            }else{
                clientServer.send(new DisplayMessage(0, SocketMessage.RELEASE_CLIENT, listToSend, 0, new Date(), true));
            }
        }

        /**
         * Provides a handler for different RowSets to perform a transition action.
         * @return Permission value for the specific rowSet to do sliding up transition
         */
        public synchronized boolean requestSliding(){
            if (isRowsSliding){
                return false;
            }else{
                isRowsSliding = true;
                return true;
            }
        }

        /**
         * Releases a handler to perform a sliding up transition.
         */
        public void releaseSlidingRequest(){
            isRowsSliding = false;
            System.out.println("USED LEVELS = " + getUSEDLevels());
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            repaintMyRows(graphics);
        }

        private void redrawMyComponents(){
            this.repaint();
        }

        private synchronized void repaintMyRows(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.white);
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(8,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
            Line2D h_lin1 = new Line2D.Float(hor_line1_p1.x, hor_line1_p1.y, hor_line1_p2.x, hor_line1_p2.y);
            g2.draw(h_lin1);


            if (tableIsValid) {
                g.setFont(TABLE_FONT);
                for (TerminalRow row : table) {
                    if (row.visible) {
                        int[] xoffsets = row.xpos;
                        if (!row.partlyVisible) {
                            g.setColor(Color.YELLOW);
                            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                            g.drawString(String.valueOf(row.clientNumber), xoffsets[0], row.ypos);
                            g.drawString(">", xoffsets[1], row.ypos);
                        }
                        g.setColor(Color.WHITE);
                        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        g.drawString(String.valueOf(row.terminalNumber + 1), xoffsets[2], row.ypos);
                    }
                }
            }
        }

        private class RowsSlideUPRunner extends Thread {
            TerminalRow row;
            //MainUIPanel uiPanel;

            private RowsSlideUPRunner(TerminalRow row) {
                this.row = row;
                //this.uiPanel = uiPanel;
            }

            @Override
            public void run() {
                while (!requestSliding()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                List<TerminalRow> slideUPRows = new ArrayList<>();
                for (TerminalRow r : table){
                    if (r.levelIndex > row.levelIndex){
                        slideUPRows.add(r);
                    }
                }

                if (slideUPRows.size()>0){
                    Collections.sort(slideUPRows);//This line is principle for next line correct work

                    (new Timer(10, new SlidingUPRowsTimerListener(slideUPRows, row))).start();
                }else{
                    row.levelIndex = -1;
                    setUSEDLevels(getUSEDLevels() - 1);
                    releaseSlidingRequest();
                }
            }
        }

        private class TerminalRow extends TerminalData implements Comparable{

            protected boolean partlyVisible;
            protected int ypos;
            protected int[] xpos;
            private Timer timerBlinking;

            private TerminalRow(TerminalData terminalData) {
                super(terminalData.levelIndex, terminalData.clientNumber
                        , terminalData.terminalNumber, terminalData.visible, terminalData.state);
                //this.terminalNumber = terminalNumber;
                //XMLVARIABLES variables = new XMLVARIABLES(APP.VARIABLES_PATH);

                //HashMap<String, Integer> terminalRowData = variables.getTerminalRowData(terminalNumber);
                /*this.levelIndex = terminalData.get("levelindex");
                this.clientNumber = terminalData.get("clientnumber");
                this.terminalNumber = terminalData.get("terminalnumber");
                int visibility = terminalData.get("visible");
                this.visible = visibility == 1;
                this.state = terminalData.get("state");*/
                this.xpos = new int[3];
                this.xpos[0] = 0;
                this.xpos[1] = 0;
                this.xpos[2] = 0;
                this.ypos = 0;
                //todo: write functionality to store standardBlinkRate locally
                timerBlinking = new Timer(500, new BlinkingTimerListener());
            }

            List<TerminalRowListener> listeners = new ArrayList<>();
            public void addTerminalRowListener(TerminalRowListener listener){
                listeners.add(listener);
            }

            private void transitionCompleted(){
                for (TerminalRowListener listener : listeners){
                    listener.onTransitionCompleted(this);
                }
            }

            private void showMessageForm(){
                for (TerminalRowListener listener : listeners){
                    listener.onShowMessageForm(this);
                }
            }

            private void disposeMessageForm(){
                for (TerminalRowListener listener : listeners){
                    listener.onDisposeMessageForm(this);
                }
            }

            protected synchronized void performAnimation(){
                if (state == ACCEPTED) {
                    state = CALLING;
                    visible = true;
                    checkForTerminalsOnHoldSet();
                    int usedLevels = getUSEDLevels();
                    (new Timer(10, new SlidingUPTimerListener(usedLevels))).start();
                }else if (state == WAITING){
                    state = ACCEPTING;
                    checkForTerminalsOnHoldRelease();
                    (new Timer(10, new SlidingAsideTimerListener(xpos))).start();
                    //setUSEDLevels(usedLevels - 1) is done in SlidingUPRowsTimerListener
                }
            }

            @Override
            public int compareTo(Object obj) {
                TerminalData dataToCompare = (TerminalData)obj;
                int retVal=0;
                if (levelIndex<dataToCompare.levelIndex){
                    retVal = -1;
                }else if (levelIndex>dataToCompare.levelIndex){
                    retVal = 1;
                }
                return retVal;
            }

            private class BlinkingTimerListener implements ActionListener{

                private boolean isForeground = true;
                private final static int maxBlinking = 4;
                private int alreadyBlinked = 0;
                private boolean clientMessageFormIsShown = false;


                @Override
                public void actionPerformed(ActionEvent e) {
                    if (alreadyBlinked <= maxBlinking * 2) {
                        alreadyBlinked++;
                        partlyVisible = !isForeground;
                        isForeground = !isForeground;
                        if (!clientMessageFormIsShown){
                            clientMessageFormIsShown = true;
                            showMessageForm();
                        }
                    } else {
                        alreadyBlinked = 0;
                        isForeground = true;
                        partlyVisible = false;
                        ((Timer)e.getSource()).stop();
                        state = WAITING;
                        //saveToXML();
                        clientMessageFormIsShown = false;
                        disposeMessageForm();
                    }
                    redrawMyComponents();
                }
            }

            private class SlidingAsideTimerListener implements ActionListener{
                private int[] initialXoffsets;

                private SlidingAsideTimerListener(int[] initialXoffsets) {
                    this.initialXoffsets = initialXoffsets;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (xpos[0]> uiPanelWidth + 40){
                        xpos = initialXoffsets;
                        visible = false;
                        state = ACCEPTED;
                        ((Timer)e.getSource()).stop();
                        transitionCompleted();
                    }else{
                        xpos[0] += 10;
                        xpos[1] += 10;
                        xpos[2] += 10;
                    }
                    redrawMyComponents();
                }
            }

            private class SlidingUPTimerListener implements ActionListener{
                private int Ydestination;

                private SlidingUPTimerListener(int levelDestination) {
                    ypos = uiPanelHeight + 40;
                    levelIndex = levelDestination;
                    setUSEDLevels(levelIndex + 1);
                    System.out.println("Destination Level = " + levelDestination);
                    this.Ydestination = h_percent_uiPanel * terminalHeightOffsets[levelDestination];
                    System.out.println("levelIndex = " + levelIndex);
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    ypos -= 10;
                    //System.out.println("levelIndex = " + levelIndex);
                    if (ypos < Ydestination + 20){
                        ypos = Ydestination;
                        //levelIndex = levelDestination;
                        //setUSEDLevels(levelIndex + 1);
                        System.out.println("USED levels" + getUSEDLevels());
                        ((Timer) e.getSource()).stop();
                        timerBlinking.start();
                    }
                    redrawMyComponents();
                }
            }
        }

        private class SlidingUPRowsTimerListener implements ActionListener{
            List<TerminalRow> slideUPRows;
            TerminalRow rowThatGone;

            private SlidingUPRowsTimerListener(List<TerminalRow> slideUPRows, TerminalRow rowThatGone) {
                this.slideUPRows = slideUPRows;
                this.rowThatGone = rowThatGone;
            }


            @Override
            public void actionPerformed(ActionEvent e) {
                for (TerminalRow r : slideUPRows){
                    r.ypos -= 10;
                }
                /*
                Before instantiate this listener we've sorted slideUPRows List against levelIndex values.
                That's why at the next line slideUPRows.get(0) returns TerminalRow with the smallest levelIndex
                 */
                int tableUPpos = slideUPRows.get(0).ypos;
                int rowThatGonePos = rowThatGone.ypos + 20;
                if (tableUPpos < rowThatGonePos){
                    ((Timer) e.getSource()).stop();
                    for (TerminalRow row : slideUPRows) {
                        int level = row.levelIndex;
                        row.ypos = h_percent_uiPanel * terminalHeightOffsets[level - 1];
                        row.levelIndex = level - 1;
                        //row.saveToXML();
                    }
                    rowThatGone.levelIndex = -1;
                    setUSEDLevels(getUSEDLevels() - 1);
                    //rowThatGone.saveToXML();
                    releaseSlidingRequest();
                }
                repaint();
            }
        }
    }
    private interface TerminalRowListener{
        public void onTransitionCompleted(MainUIPanel.TerminalRow row);
        public void onShowMessageForm(MainUIPanel.TerminalRow row);
        public void onDisposeMessageForm(MainUIPanel.TerminalRow row);
    }

    private List<DisplayFormListener> displayFormListeners = new ArrayList<>();

    public void addDisplayFormListener(DisplayFormListener listener){
        displayFormListeners.add(listener);
    }

    public interface DisplayFormListener {
        public void onAssignClient(int terminalIndex, int client);
        public void onAcceptClient(int terminalIndex, int client);
        public void onHoldTerminals(int[] terminals, int val);
    }

    @Override
    public void onRegister(int id) {
        this.id = id;
    }

    @Override
    public void onInputMessage(Object objMessage) {
        DisplayMessage message = (DisplayMessage)objMessage;
        switch (message.operation){
            case DisplayMessage.INIT_ROWS:
                System.out.println("INIT_ROWS!!!");
                initForm(message.terminals, message.restOfClients);
                message.received = true;
                clientServer.send(message);
                break;
            case DisplayMessage.ADD_ROW:
                mainUIPanel.restOfClients = message.restOfClients;
                addRow(message.terminals.get(0));
                break;
            case DisplayMessage.DELETE_ROW:
                mainUIPanel.restOfClients = message.restOfClients;
                deleteRow(message.terminals.get(0));
                break;
            /*case APP.RESET_SYSTEM:
                break;*/
            case APP.PRINTER_ERROR_ON:
                break;
            case APP.PRINTER_ERROR_OFF:
                break;
            case APP.STOP_SERVICE:
                break;
            case APP.RESET_SERVICE:
                if (message.terminals != null){
                    mainUIPanel.restOfClients = message.restOfClients;
                    mainUIPanel.reAssignTerminals(message.terminals);
                    //todo: reassign also bottom line
                }else{
                    executeSystemCommand(APP.RESET_SERVICE);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onCloseSocket() {

    }
}

/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import sockets.DisplayMessage;
import sockets.PrinterMessage;
import sockets.SocketMessage;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Created by forando on 06.04.14.
 * This is Main Form for Application
 */
public class DisplayForm extends JFrame implements ClientServer.ClientServerListener {

    private int id = -1;

    /*private int defaultBlinkRate;
    private int errorBlinkRate;*/

    HashMap<String, String> currentVideo;

    //Terminal Commands:
    final static int TERMINAL_BASE = 49;

    private static final Font BG_STRING_FONT = new Font(Font.SANS_SERIF,
            Font.BOLD, 72);

    private ClientServer clientServer;


    private int bgStringX;
    private int bgStringY;

    Timer timerTicker;
    Audio notificationSound;
    Audio errorSound;

    private boolean PRINTER_ERROR = false;
    private boolean SERVICE_STOPPED = false;

    private JPanel rootPanel;
    private JLabel l_clientTitle;
    private JLabel l_terminalTitle;
    private JLabel l_totalTitle;
    private JLabel l_total;
    private JLabel l_takeTicket;
    public TablePanel tablePanel;
    private JLabel l_serviceStopped;
    private BottomPanel bottomPanel;
    private JPanel mediaContentPanel;
    private JPanel videoPanel;
    private JPanel tickerPanel;
    private JLabel l_ticker;

    private Canvas canvas;

    List<String> tickerMessages;
    int tickerMessagesItem = 0;

    private int tickerPanelWidth;
    private int tickerPanelHeight;

    ClientServer.ClientServerListener clientServerListeners;

    public DisplayForm(){
        //Form Title
        super("Продукт Компании \"ВЕРСИЯ\"");
        startClientServer();
        this.clientServerListeners = this;
        initForm();

    }

    public void initForm() {



        setContentPane(rootPanel);
        //setUndecorated(true);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// locate Form in the center of the screen

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        //examples.MyLayoutManager mgr = new examples.MyLayoutManager();

        //on these panels we locating components pragmatically
        tablePanel.setLayout(null);
        bottomPanel.setLayout(null);
        tickerPanel.setLayout(null);

        XMLVARIABLES variables = new XMLVARIABLES(APP.VARIABLES_PATH);
        /*defaultBlinkRate = variables.getErrorBlinkRate();
        errorBlinkRate = variables.getDefaultBlinkRate();*/
        tickerMessages = variables.getMessages();
        currentVideo = variables.getCurrentVieoData();
        initObjects();

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

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //start blinking bottom line
                List<JLabel>labels = new ArrayList<>();
                labels.add(l_totalTitle);
                labels.add(l_total);
                labels.add(l_takeTicket);
                bottomPanel.startDefaultBlinker(labels);
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

        setVisible(true);
    }

    private void assignClientServer(ClientServer client){
        this.clientServer = client;
        this.id = client.id;
        this.clientServer.send(new DisplayMessage(id, DisplayMessage.SOCKET_READY, null, 0, new Date(), true));
    }

    private void startClientServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ClientConnectorProvider clientConnectorProvider = new ClientConnectorProvider(clientServerListeners, SocketMessage.DISPLAY, id);
                try {
                    clientConnectorProvider.addClientConnectorListener(new ClientConnectorProvider.ClientConnectorListener() {
                        @Override
                        public void onClientConnected(ClientServer client) {
                            assignClientServer(client);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stopClientServer(){
        if (clientServer != null) {
            clientServer.stopClient();
            clientServer = null;
        }
    }

    public void submitEvent(int keyCode) {
        if(keyCode>=0 && keyCode<=APP.MAX_TERMINAL_QUANTITY){
            int terminalNumber = keyCode - TERMINAL_BASE + 1;
            if (terminalNumber <= APP.TERMINAL_QUANTITY) {
                tablePanel.assignTerminal(keyCode);
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
            case APP.RESET_SYSTEM:
                sendToHostServer(new DisplayMessage(this.id, APP.RESET_SYSTEM,
                        null, 0, new Date(), true));
                break;
            case APP.PRINTER_ERROR_ON:
                /*if (!SERVICE_STOPPED) {
                    PRINTER_ERROR = true;
                    bottomPanel.startPrinterErrorBlinker(l_takeTicket);
                    notificationSound.Stop();
                    errorSound.Play();
                    notificationSound.Reset();
                }*/
                sendToHostServer(new DisplayMessage(this.id, APP.PRINTER_ERROR_ON,
                        null, 0, new Date(), true));
                break;
            case APP.PRINTER_ERROR_OFF:
                /*if (!SERVICE_STOPPED) {
                    PRINTER_ERROR = false;
                    List<JLabel>labels = new ArrayList<>();
                    labels.add(l_totalTitle);
                    labels.add(l_total);
                    labels.add(l_takeTicket);
                    bottomPanel.startDefaultBlinker(labels);
                }*/
                sendToHostServer(new DisplayMessage(this.id, APP.PRINTER_ERROR_OFF,
                        null, 0, new Date(), true));
                break;
            case APP.STOP_SERVICE:
                sendToHostServer(new DisplayMessage(this.id, APP.STOP_SERVICE,
                        null, 0, new Date(), true));
                //triggerService(SERVICE_STOPPED);

                break;
            case APP.RESET_SERVICE:
                sendToHostServer(new DisplayMessage(this.id, APP.RESET_SERVICE,
                        null, 0, new Date(), true));
                //triggerService(SERVICE_STOPPED);

                break;
            case APP.TRIGGER_SERVICE:
                /*SERVICE_STOPPED = !SERVICE_STOPPED;
                triggerService(SERVICE_STOPPED);*/
                sendToHostServer(new DisplayMessage(this.id, APP.TRIGGER_SERVICE,
                        null, 0, new Date(), true));
                break;
            case APP.PRINT_TICKET:
                /*if(!TICKET_IS_PRINTING) {
                    total++;
                    lastClient = total - 1;
                    if (nextClient == 0) {
                        nextClient = lastClient;
                    }
                    relocateBottomPanelChildren();
                    variables.setLastClient(lastClient);
                    variables.setNextClient(nextClient);
                    printTicket();
                }*/
                sendToHostServer(new DisplayMessage(this.id, APP.PRINT_TICKET,
                        null, 0, new Date(), true));
                break;
            default:
                break;
        }
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
            relocateBottomPanelChildren();
            timerServiceStopped.start();
        }else{
            timerServiceStopped.stop();
            relocateBottomPanelChildren();
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
        SERVICE_STOPPED = true;
        List<JLabel>labels = new ArrayList<>();
        labels.add(l_takeTicket);
        labels.add(l_serviceStopped);
        bottomPanel.startStopServiceBlinker(labels);
    }

    private void resetService(){
        SERVICE_STOPPED = false;
        if (PRINTER_ERROR){
            bottomPanel.startPrinterErrorBlinker(l_takeTicket);
        }else{
            List<JLabel>labels = new ArrayList<>();
            labels.add(l_totalTitle);
            labels.add(l_total);
            labels.add(l_takeTicket);
            bottomPanel.startDefaultBlinker(labels);
        }
    }

    private void initObjects() {

        timerTicker = new Timer(20, new TimerTicker(l_ticker));
        timerTicker.setInitialDelay(0);

        errorSound = new Audio("/files/notify.wav");
        notificationSound = new Audio("/files/chimes.wav");
    }

    private void sendToHostServer(DisplayMessage message){
        clientServer.send(message);
    }

    /**
     * Overridden method-placeholder for components instantiation
     * <br>
     * (If you want to override component instantiation, than do it here)
     */
    private void createUIComponents() {
        tablePanel = new TablePanel(this.id);
        try {
            tablePanel.addTablePanelListener(new TablePanel.TablePanelListener() {
                @Override
                public void relocateBottomPanelChildren() {
                    bottomPanel.relocateBottomComponents();
                }

                @Override
                public void sendToServer(DisplayMessage message) {
                    sendToHostServer(message);
                }

                @Override
                public Dimension getMediaContentPanelSize() {
                    return mediaContentPanel.getSize();
                }

                @Override
                public void submitAction(int keyCode) {
                    submitEvent(keyCode);
                }

                @Override
                public void playNotificationSound() {
                    notificationSound.Play();
                }

                @Override
                public List<JLabel> getTableTitleLabels() {
                    List<JLabel> tableTitleLabels = new ArrayList<>();
                    tableTitleLabels.add(l_terminalTitle);
                    tableTitleLabels.add(l_clientTitle);
                    return tableTitleLabels;
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }

        bottomPanel = new BottomPanel();
        try{
            bottomPanel.addBottomPanelListener(new BottomPanel.BottomPanelListener() {
                @Override
                public String getFontName() {
                    return l_clientTitle.getFont().getName();
                }

                @Override
                public List<JLabel> getLabels() {
                    List<JLabel> labels = new ArrayList<>();
                    labels.add(l_totalTitle);
                    labels.add(l_total);
                    labels.add(l_takeTicket);
                    labels.add(l_serviceStopped);
                    return labels;
                }

                @Override
                public int getRestOfClients() {
                    return tablePanel.restOfClients;
                }

                @Override
                public List<Boolean> getFlags() {
                    List<Boolean> flags = new ArrayList<>();
                    flags.add(PRINTER_ERROR);
                    flags.add(SERVICE_STOPPED);
                    return flags;
                }

                @Override
                public List<Integer> getBlinkRates() {
                    List<Integer> blinkRates = new ArrayList<>();
                    XMLVARIABLES variables = new XMLVARIABLES(APP.VARIABLES_PATH);
                    int defaultBlinkRate = variables.getDefaultBlinkRate();
                    int errorBlinkRate = variables.getErrorBlinkRate();
                    blinkRates.add(defaultBlinkRate);
                    blinkRates.add(errorBlinkRate);
                    return blinkRates;
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }

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

    private List<DisplayFormListener> displayFormListeners = new ArrayList<>();

    public void addDisplayFormListener(DisplayFormListener listener){
        displayFormListeners.add(listener);
    }

    public interface DisplayFormListener {
        public void onAssignClient(int terminalIndex, int client);
        public void onAcceptClient(int terminalIndex, int client);
        public void onHoldTerminals(int[] terminals, int val);
    }

    /**
     * This callback is implemented in {@link ClientConnectorProvider}. Here it's never used.
     * @param id An ID of the client.
     */
    @Override
    public void onRegister(int id) {}

    @Override
    public void onInputMessage(Object objMessage) {
        DisplayMessage message = (DisplayMessage)objMessage;
        switch (message.operation){
            case DisplayMessage.INIT_ROWS:
                System.out.println("INIT_ROWS!!!");
                tablePanel.initTable(message.terminals, message.restOfClients);
                message.received = true;
                clientServer.send(message);
                break;
            case DisplayMessage.ADD_ROW:
                tablePanel.restOfClients = message.restOfClients;
                tablePanel.addRow(message.terminals.get(0));
                break;
            case DisplayMessage.DELETE_ROW:
                tablePanel.restOfClients = message.restOfClients;
                tablePanel.deleteRow(message.terminals.get(0));
                break;
            case APP.PRINT_TICKET:
                tablePanel.restOfClients = message.restOfClients;
                bottomPanel.relocateBottomComponents();
                break;
            case APP.PRINTER_ERROR_ON:
                if (!SERVICE_STOPPED) {
                    PRINTER_ERROR = true;
                    bottomPanel.startPrinterErrorBlinker(l_takeTicket);
                    notificationSound.Stop();
                    errorSound.Play();
                    notificationSound.Reset();
                }
                break;
            case APP.PRINTER_ERROR_OFF:
                if (!SERVICE_STOPPED) {
                    PRINTER_ERROR = false;
                    List<JLabel>labels = new ArrayList<>();
                    labels.add(l_totalTitle);
                    labels.add(l_total);
                    labels.add(l_takeTicket);
                    bottomPanel.startDefaultBlinker(labels);
                }
                break;
            case APP.STOP_SERVICE:
                stopService();
                break;
            case APP.RESET_SERVICE:
                if (message.terminals != null){
                    tablePanel.restOfClients = message.restOfClients;
                    tablePanel.reAssignTerminals(message.terminals);
                    //todo: reassign also bottom line
                }else{
                    resetService();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onCloseSocket() {
        startClientServer();
    }
}

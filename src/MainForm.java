import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

/**
 * Created by forando on 06.04.14.
 */
public class MainForm extends  JFrame {
    private static final int BLINKING_RATE = 500;
    boolean PRINTER_ERROR = false;
    int total = 0;
    int lastClient = 0;
    int nextClient = 0;
    int client1 = 0;
    int client2 = 0;
    int client3 = 0;
    int client4 = 0;
    int buttonClicked = 0;
    int ticketsPrinted = 0;
    Timer timerClient1;
    Timer timerClient2;
    Timer timerError;
    Audio notificationSound;
    POS_PRINTER printer;
    XMLVARIABLES variables;
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
    private Point ver_line1_p1 = new Point(100, 100);
    private Point ver_line1_p2 = new Point(200, 200);

    public MainForm(){
        super("Продукт Компании \"ВЕРСИЯ\"");

        setContentPane(rootPanel);
        setUndecorated(true);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// locate Form in the center of the screen

        this.setLayout(null);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        rootPanel.setLayout(null);

        timerClient1 = new Timer(BLINKING_RATE, new TimerListener(l_client1, 1));
        timerClient1.setInitialDelay(0);
        timerClient2 = new Timer(BLINKING_RATE, new TimerListener(l_client2, 2));
        timerClient2.setInitialDelay(0);
        timerError = new Timer(BLINKING_RATE, new TimerListener(l_totalTitle, 3));
        timerError.setInitialDelay(0);
        printer = new POS_PRINTER();
        notificationSound = new Audio("/resources/notify.wav");
        variables = new XMLVARIABLES(APP.VARIABLES_PATH);

        //init variables:
        lastClient = variables.getLastClient();
        client1 = variables.getClientAsigned(1);
        client2 = variables.getClientAsigned(2);
        buttonClicked = variables.getButtonClicked();
        ticketsPrinted = variables.getTicketsPrinted();
        nextClient = variables.getNextClient();

        client3 = nextClient;
        if (client3>0){
            if (client3 < lastClient) {
                client4 = client3 + 1;
            } else {
                client4 = 0;
            }
        }

        addComponentListener(new ComponentListener(

        ) {
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
                //System.out.println("Pressed " + e.getKeyCode());
                switch (e.getKeyCode()) {
                    case 112: //terminal1 Button Pressed
                        if (client3 > 0) {
                            variables.setClientAsigned(1, variables.getNextClient());
                            client1 = variables.getClientAsigned(1);
                            reasignClients34();
                            relocateMyComponents();
                            timerClient1.start();
                            notificationSound.Play();
                        }
                        buttonClicked++;
                        variables.setButtonClicked(buttonClicked);
                        break;
                    case 113: //terminal2 Button Pressed
                        if (client3 > 0) {
                            variables.setClientAsigned(2, variables.getNextClient());
                            client2 = variables.getClientAsigned(2);
                            reasignClients34();
                            relocateMyComponents();
                            timerClient2.start();
                            notificationSound.Play();
                        }
                        buttonClicked++;
                        variables.setButtonClicked(buttonClicked);
                        break;
                    case 114: //terminal2 Button Pressed
                        //reserve
                        break;
                    case 115: //terminal2 Button Pressed
                        //reserve
                        break;
                    case 116: //Printer ERROR ON
                        PRINTER_ERROR = true;
                        ticketsPrinted = 0;
                        variables.setTicketsPrinted(ticketsPrinted);
                        relocateMyComponents();
                        timerError.start();
                        notificationSound.Play();
                        break;
                    case 117: //Printer ERROR OFF
                        PRINTER_ERROR = false;
                        printer.Print(total);
                        break;
                    case 36://signal to print a ticket
                        if (!PRINTER_ERROR) {
                            if (total == 0) {
                                total = lastClient + 1;
                            } else {
                                total++;
                                variables.setLastClient(total - 1);
                                lastClient = variables.getLastClient();
                                if (client3 == 0) {
                                    variables.setNextClient(variables.getLastClient());
                                    nextClient = variables.getNextClient();
                                    client3 = nextClient;
                                } else if (client3 > 0 && client4 == 0) {
                                    client4 = variables.getLastClient();
                                }
                                relocateMyComponents();
                            }
                            printer.Print(total);
                            ticketsPrinted++;
                            variables.setTicketsPrinted(ticketsPrinted);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
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
        Line2D v_lin1 = new Line2D.Float(ver_line1_p1.x, ver_line1_p1.y, ver_line1_p2.x, ver_line1_p2.y);
        g2.draw(v_lin1);
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

        if (client1 == 0) {
            l_client1.setText("");
        }

        l_client2.setText(String.valueOf(client2));
        l_client2.setFont(new Font(fontName, Font.PLAIN, tableDataHeight));
        labelText = l_client2.getText();
        stringWidth = l_client2.getFontMetrics(l_client2.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        h_loc = h_percent * 48;
        l_client2.setLocation(w_loc, h_loc);
        l_client2.setSize(stringWidth, tableDataHeight - h_percent * 5);

        if (client2 == 0) {
            l_client2.setText("");
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

        if (client3 == 0) {
            l_client3.setText("");
        }

        l_client4.setText(String.valueOf(client4));
        l_client4.setFont(new Font(fontName, Font.PLAIN, dataHeight));
        labelText = l_client4.getText();
        stringWidth = l_client4.getFontMetrics(l_client4.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 25) - (stringWidth / 2);
        //h_loc = l_client3.getLocation().y + l_client3.getHeight() + h_percent * 2;
        h_loc = formHeight - l_client4.getHeight() - h_percent;
        l_client4.setLocation(w_loc, h_loc);
        l_client4.setSize(stringWidth, dataHeight - h_percent * 3);

        if (client4 == 0) {
            l_client4.setText("");
        }


        //=====================TOTAL DATA ========================================


        if (PRINTER_ERROR){
            l_totalTitle.setText("ВСТАВЬТЕ БУМАГУ!");
        }else {
            l_totalTitle.setText("ВСЕГО:");
        }
        l_totalTitle.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_totalTitle.getText();
        int totalTitle_stringWidth = l_totalTitle.getFontMetrics(l_totalTitle.getFont()).stringWidth(labelText);
        if (PRINTER_ERROR){
            w_loc = formWidth - totalTitle_stringWidth;
            h_loc = h_percent * 85;
        }else {
            w_loc = (w_percent * 60) - (stringWidth / 2);
            h_loc = h_percent * 85;
        }
        l_totalTitle.setLocation(w_loc, h_loc);
        l_totalTitle.setSize(totalTitle_stringWidth, totalDataHeight - h_percent * 2);

        int restOfClients;
        if (nextClient > 0){
            restOfClients = lastClient - nextClient +1;
        }else{
            restOfClients = 0;
        }
        l_total.setText(String.valueOf(restOfClients));
        l_total.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_total.getText();
        stringWidth = l_total.getFontMetrics(l_total.getFont()).stringWidth(labelText);
        //w_loc = l_totalTitle.getLocation().x + totalTitle_stringWidth + w_percent;
        w_loc = (w_percent * 101) - stringWidth;
        h_loc = h_percent * 85;
        l_total.setLocation(w_loc, h_loc);
        l_total.setSize(stringWidth, totalDataHeight - h_percent * 2);

        if (PRINTER_ERROR){
            l_total.setText("");
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

        ver_line1_p1 = new Point(w_percent * 50, correction + h_percent * 3);
        ver_line1_p2 = new Point(w_percent * 50, correction + l_client2.getLocation().y + l_client2.getHeight());

        repaint();
    }

    private void reasignClients34() {
        if (client4 > 0) {
            variables.setNextClient(client4);
            nextClient = variables.getNextClient();
            client3 = nextClient;
            if (client3 < lastClient) {
                client4++;
            } else {
                client4 = 0;
            }
        } else {
            variables.setNextClient(0);
            nextClient = variables.getNextClient();
            client3 = nextClient;
        }
    }

    private class TimerListener implements ActionListener {
        private final static int maxBlinking = 4;
        private JLabel _label;
        private Color bg;
        private Color fg;
        private boolean isForeground = true;
        private int alreadyBlinked = 0;
        private int _timerClientNumber;

        public TimerListener(JLabel label, int timerClientNumber) {
            this._label = label;
            fg = label.getForeground();
            bg = label.getBackground();
            this._timerClientNumber = timerClientNumber;
        }

        public void actionPerformed(ActionEvent e) {
            if (alreadyBlinked <= maxBlinking * 2) {
                alreadyBlinked++;
                if (isForeground) {
                    _label.setForeground(fg);
                } else {
                    _label.setForeground(bg);
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
                    case 3:
                        timerError.stop();
                        break;
                    default:
                        break;
                }
            }

        }
    }
}

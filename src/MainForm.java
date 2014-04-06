import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.net.URL;

/**
 * Created by forando on 06.04.14.
 */
public class MainForm extends  JFrame {
    private static final int BLINKING_RATE = 500;
    int total = 0;
    int client1 = 0;
    int client2 = 0;
    int client3 = 0;
    int client4 = 0;
    Timer timerClient1;
    Timer timerClient2;
    AudioFormat audioFormat;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;
    boolean stopPlayback = false;
    boolean playbackFinished = true;
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
    POS_PRINTER printer;

    public MainForm(){
        super("Hello World");

        setContentPane(rootPanel);
        setUndecorated(true);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// locate Form in the center of the screen

        this.setLayout(null);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        rootPanel.setLayout(null);

        timerClient1 = new Timer(BLINKING_RATE, new TimerListener_Client1(l_client1, 1));
        timerClient1.setInitialDelay(0);
        timerClient2 = new Timer(BLINKING_RATE, new TimerListener_Client1(l_client2, 2));
        timerClient2.setInitialDelay(0);
        printer = new POS_PRINTER();

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
                            client1 = client3;
                            reasignClients34();
                            relocateMyComponents();
                            timerClient1.start();
                            playAudio();
                        }
                        break;
                    case 113: //terminal2 Button Pressed
                        if (client3 > 0) {
                            client2 = client3;
                            reasignClients34();
                            relocateMyComponents();
                            timerClient2.start();
                            playAudio();
                        }
                        break;
                    case 36://signal to print a ticket
                        total++;

                        printer.Print(total);
                        if (client3 == 0) {
                            client3 = total;
                        } else if (client3 > 0 && client4 == 0) {
                            client4 = total;
                        }
                        relocateMyComponents();
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
        g2.setColor(Color.blue);
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
        h_loc = l_client4.getLocation().y - (h_percent * 2) - l_client3.getHeight();
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


        l_totalTitle.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_totalTitle.getText();
        int totalTitle_stringWidth = l_totalTitle.getFontMetrics(l_totalTitle.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 60) - (stringWidth / 2);
        h_loc = h_percent * 85;
        l_totalTitle.setLocation(w_loc, h_loc);
        l_totalTitle.setSize(totalTitle_stringWidth, totalDataHeight - h_percent * 2);

        l_total.setText(String.valueOf(total));
        l_total.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_total.getText();
        stringWidth = l_total.getFontMetrics(l_total.getFont()).stringWidth(labelText);
        //w_loc = l_totalTitle.getLocation().x + totalTitle_stringWidth + w_percent;
        w_loc = (w_percent * 101) - stringWidth;
        h_loc = h_percent * 85;
        l_total.setLocation(w_loc, h_loc);
        l_total.setSize(stringWidth, totalDataHeight - h_percent * 2);

        if (total == 0) {
            l_total.setText("");
        }

        redrawLines();
    }

    private void redrawLines() {
        int left = w_percent * 3;
        int right = w_percent * 100;

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
            client3 = client4;
            if (client4 < total) {
                client4++;
            } else {
                client4 = 0;
            }
        } else {
            client3 = 0;
        }
    }

    //This method plays back audio data from an
    // audio file whose name is specified in the
    // text field.
    private void playAudio() {
        if (playbackFinished) {
            try {
                URL myURL = ClassLoader.getSystemResource("resources/notify.wav");
                File soundFile =
                        new File(myURL.getPath());
                audioInputStream = AudioSystem.
                        getAudioInputStream(soundFile);
                audioFormat = audioInputStream.getFormat();
                System.out.println(audioFormat);

                DataLine.Info dataLineInfo =
                        new DataLine.Info(
                                SourceDataLine.class,
                                audioFormat);

                sourceDataLine =
                        (SourceDataLine) AudioSystem.getLine(
                                dataLineInfo);

                //Create a thread to play back the data and
                // start it running.  It will run until the
                // end of file, or the Stop button is
                // clicked, whichever occurs first.
                // Because of the data buffers involved,
                // there will normally be a delay between
                // the click on the Stop button and the
                // actual termination of playback.
                new PlayThread().start();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }//end catch
        }
    }//end playAudio

    private class TimerListener_Client1 implements ActionListener {
        private final static int maxBlinking = 4;
        private JLabel _label;
        private Color bg;
        private Color fg;
        private boolean isForeground = true;
        private int alreadyBlinked = 0;
        private int _timerClientNumber;

        public TimerListener_Client1(JLabel label, int timerClientNumber) {
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
                    default:
                        break;
                }
            }

        }
    }

    //=============================================//
//Inner class to play back the data from the
// audio file.
    class PlayThread extends Thread {
        byte tempBuffer[] = new byte[10000];
        int readFromInputStream;


        public void run() {
            playbackFinished = false;
            try {
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                //Keep looping until the input read method
                // returns -1 for empty stream or the
                // user clicks the Stop button causing
                // stopPlayback to switch from false to
                // true.
                while ((readFromInputStream = audioInputStream.read(
                        tempBuffer, 0, tempBuffer.length)) != -1
                        && !stopPlayback) {
                    if (readFromInputStream > 0) {
                        //Write data to the internal buffer of
                        // the data line where it will be
                        // delivered to the speaker.
                        sourceDataLine.write(
                                tempBuffer, 0, readFromInputStream);
                    }//end if
                }//end while
                //Block and wait for internal buffer of the
                // data line to empty.
                sourceDataLine.drain();
                sourceDataLine.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }//end catch

            playbackFinished = true;

            //Prepare to playback another file
            // stopBtn.setEnabled(false);
            //playBtn.setEnabled(true);
            stopPlayback = false;
        }//end run
    }//end inner class PlayThread
//===================================//
}

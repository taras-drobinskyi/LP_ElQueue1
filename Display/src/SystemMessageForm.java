import interfaces.SystemMessageFormListener;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SystemMessageForm extends JFrame {
    //a list of catchers
    List<SystemMessageFormListener> listeners = new ArrayList<SystemMessageFormListener>();
    private JPanel rootPanel;
    private JPanel messagePanel;
    private JLabel l_message1;
    private JLabel l_message3;
    private JLabel l_message2;
    Timer timer;
    static int standardBlinkRate;
    XMLVARIABLES variables;

    public SystemMessageForm(int width, int height) {
        //Form Title
        super("Сообщение");

        setContentPane(rootPanel);
        setUndecorated(true);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setLocationRelativeTo(null);// locate Form in the center of the screen

        initVariables();
        initObjects();

        rootPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 36://signal to print a ticket
                        //1 or more times, a Notification that an event happened is fired.
                        for (SystemMessageFormListener listener : listeners) listener.onPrintTicket();
                        timer.stop();
                        setVisible(false);
                        dispose();
                        break;
                    case 115://Reset buttonClicked
                        //1 or more times, a Notification that an event happened is fired.
                        for (SystemMessageFormListener listener : listeners) listener.onReset();
                        timer.stop();
                        setVisible(false);
                        dispose();
                        break;
                    case 123://F12 - Screen Update
                        //ignoring this signal
                        break;
                    default:
                        for (SystemMessageFormListener listener : listeners) listener.onClose();
                        timer.stop();
                        setVisible(false);
                        dispose();
                        break;
                }
            }
        });

        //start blinking label
        timer.start();

        rootPanel.setFocusable(true);
        rootPanel.requestFocusInWindow();

        setSize(width, height);
        setVisible(true);
    }

    private void initObjects() {
        timer = new Timer(standardBlinkRate, new TimerListener(1));
        timer.setInitialDelay(0);
    }

    private void initVariables() {

        variables = new XMLVARIABLES(APP.VARIABLES_PATH);

        standardBlinkRate = variables.getStandardBlinkRate();
    }

    private class TimerListener implements ActionListener {
        private final static int maxBlinking = 4;
        private JLabel label1;
        private Color bg;
        private Color fg;
        private boolean isForeground = true;
        private int alreadyBlinked = 0;
        private int _optionNumber;

        public TimerListener(int optionNumber) {
            switch (optionNumber) {
                case 1:
                    this.label1 = l_message1;
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
                case 1:
                    if (alreadyBlinked <= maxBlinking * 2) {
                        alreadyBlinked++;
                        if (isForeground) {
                            label1.setForeground(fg);
                        } else {
                            label1.setForeground(bg);
                        }
                        isForeground = !isForeground;
                    } else {
                        alreadyBlinked = 0;
                        isForeground = true;
                    }
                default:
                    break;
            }
        }
    }

    //a way to add someone to the list of catchers
    public void addMessageFormListener(SystemMessageFormListener listener) {
        listeners.add(listener);
    }
}

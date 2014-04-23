/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 23.04.14.
 * This class operates with ServiceMessageForm
 */

//a declaration of the events that can caught by a catcher
interface MessageFormListener {
    public void onReset();
}

public class MessageForm extends JFrame {
    //a list of catchers
    List<MessageFormListener> listeners = new ArrayList<MessageFormListener>();
    private JPanel rootPanel;
    private JPanel messagePanel;
    private JLabel l_message1;
    private JLabel l_message2;

    public MessageForm() {
        //Form Title
        super("Сообщение");

        setContentPane(rootPanel);
        setUndecorated(true);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// locate Form in the center of the screen

        rootPanel.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 115://Reset buttonClicked
                        //1 or more times, a Notification that an event happened is fired.
                        for (MessageFormListener listener : listeners) listener.onReset();
                        setVisible(false);
                        dispose();
                        break;
                    default:
                        break;
                }
            }
        });

        rootPanel.setFocusable(true);
        rootPanel.requestFocusInWindow();

        setVisible(true);
    }

    //a way to add someone to the list of catchers
    public void addResetListener(MessageFormListener listener) {
        listeners.add(listener);
    }
}

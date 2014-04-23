/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by forando on 23.04.14.
 */
public class MessageForm extends JFrame {
    private JPanel rootPanel;
    private JPanel messagePanel;
    private JLabel l_message1;
    private JLabel l_message2;

    public MessageForm(){
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
                    case 118:
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
    }

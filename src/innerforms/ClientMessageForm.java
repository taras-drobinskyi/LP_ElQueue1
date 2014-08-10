/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package innerforms;

import innerforms.interfaces.ClientMessageFormListener;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 10.08.14.
 */
public class ClientMessageForm extends JFrame {
    private JPanel rootPanel;
    private JLabel l_clientTitle;
    private JLabel l_client;
    private JLabel l_arrow;
    private JLabel l_terminalTitle;
    private JLabel l_terminal;

    List<ClientMessageFormListener> listeners = new ArrayList<ClientMessageFormListener>();


    public ClientMessageForm(int width, int height, String client, String terminal) {
        //Form Title
        super("Вызов Клиента");

        setContentPane(rootPanel);
        setUndecorated(true);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        l_client.setText(client);
        l_terminal.setText(terminal);

        setSize(width, height);
        setVisible(true);
    }
}

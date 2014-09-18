/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import interfaces.ClientMessageFormListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 10.08.14.
 */
public class ClientMessageForm extends JFrame {
    private JPanel rootPanel;

    private MessageListOrganizer organizer;

    List<ClientMessageFormListener> listeners = new ArrayList<>();


    public ClientMessageForm(int width, int height, int client, int terminal) {
        //Form Title
        super("Вызов Клиента");

        setContentPane(rootPanel);
        setUndecorated(true);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //on this panels we locating components pragmatically
        rootPanel.setLayout(null);

        setSize(width, height);

        organizer = new MessageListOrganizer(client, terminal, width, height);

        rootPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println(e.getKeyCode());
                for (ClientMessageFormListener l : listeners){
                    l.onKeyPressed(e.getKeyCode());
                }
            }
        });

        rootPanel.setFocusable(true);
        rootPanel.requestFocusInWindow();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                rootPanel.repaint();
            }
        });

        setVisible(true);
    }

    public void addMessage(int client, int terminal){
        organizer.addRow(client, terminal);
        rootPanel.repaint();
    }

    public void removeMessage(int terminal){
        if(organizer.table.size()==1){
            for (ClientMessageFormListener l : listeners){
                l.onClose();
            }
        }else {
            organizer.removeRow(terminal);
            rootPanel.repaint();
        }
    }

    private void createUIComponents() {
        rootPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g.setFont(organizer.TABLE_FONT);
                for (MessageListOrganizer.Row row : organizer.table){
                    g.setColor(Color.WHITE);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.drawString(String.valueOf(row.message), row.xpos, row.ypos);
                }

            }
        };
    }

    private class MessageListOrganizer {
        /**
         * Indicates the maximum message rows allowed to be shown simultaneously.
         */
        public static final int MAX_ROWS = 5;
        private List <Row> table;
        private int levelsQuant;
        private int[] levels;
        private Font TABLE_FONT;
        private int fontHeight;
        private FontMetrics fontMetrics;
        private int screenWidth;
        private int screenHeight;

        private MessageListOrganizer(int client, int terminal, int width, int height) {
            this.table = new ArrayList<>();
            this.screenWidth = width;
            this.screenHeight = height;
            fontHeight = (height/100) * 12;
            TABLE_FONT = new Font(Font.DIALOG, Font.PLAIN, fontHeight);
            fontMetrics = getFontMetrics(TABLE_FONT);
            addRow(client, terminal);

        }

        private void assignLevels(){
            int oddOffset = fontHeight*2/3;
            int evenOffset = (fontHeight + oddOffset)/2;
            switch (levelsQuant){
                case 2:
                    levels = new int[2];
                    levels[0] = screenHeight/2 - oddOffset;
                    levels[1] = screenHeight/2 + oddOffset;
                    break;
                case 3:
                    levels = new int[3];
                    levels[0] = screenHeight/2 - fontHeight - oddOffset + oddOffset/3;
                    levels[1] = screenHeight/2;
                    levels[2] = screenHeight/2 + fontHeight + oddOffset - oddOffset/3;
                    break;
                case 4:
                    levels = new int[4];
                    levels[0] = screenHeight/2 - fontHeight - evenOffset;
                    levels[1] = screenHeight/2 - oddOffset;
                    levels[2] = screenHeight/2 + oddOffset;
                    levels[3] = screenHeight/2 + fontHeight + evenOffset;
                    break;
                case 5:
                    levels = new int[5];
                    levels[1] = screenHeight/2 - fontHeight - oddOffset + oddOffset/3;
                    levels[0] = levels[1] + oddOffset - fontHeight - evenOffset;
                    levels[2] = screenHeight/2;
                    levels[3] = screenHeight/2 + fontHeight + oddOffset - oddOffset/3;
                    levels[4] = levels[3] - oddOffset + fontHeight + evenOffset;
                    break;
                default:
                    levels = new int[1];
                    levels[0] = screenHeight/2;
                    break;
            }
        }

        /**
         * Adds a message row to a rowsSet.<br>
         * Currently rowsSet can contain not more than {@value #MAX_ROWS} rows.
         * Otherwise ArrayIndexOutOfBoundsException will be thrown.
         * @param client The client number
         * @param terminal The id number
         */
        private void addRow(int client, int terminal){
            levelsQuant++;
            assignLevels();
            for (int i=0;i<table.size();i++){
                Row r = table.get(i);
                try {
                    r.ypos = levels[i];
                }catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("ArrayIndexOutOfBoundsException " + e.getMessage());
                    r.ypos = -100;
                }
            }
            table.add(new Row(client, terminal, levelsQuant - 1));

        }

        /**
         * Removes a message row from a rowsSet.<br>
         * Currently rowsSet can contain not more than {@value #MAX_ROWS} rows.
         * Otherwise ArrayIndexOutOfBoundsException will be thrown.
         * @param terminal The id number
         */
        private void removeRow(int terminal){
            for (int i=0; i< levelsQuant; i++){
                if (table.get(i).terminalNumber == terminal){
                    table.remove(i);
                    i=levelsQuant;//this is for exiting the loop
                    levelsQuant--;
                    assignLevels();
                }
            }
            for (int i=0;i<table.size();i++){
                Row r = table.get(i);
                try {
                    r.ypos = levels[i];
                }catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("ArrayIndexOutOfBoundsException " + e.getMessage());
                    r.ypos = -100;
                }
            }
        }

        private class Row{
            private int terminalNumber;
            private String message;
            protected int ypos;
            protected int xpos;

            private Row(int clientNumber, int terminalNumber, int levelIndex) {
                this.terminalNumber = terminalNumber;
                this.message = "Талон " + clientNumber + "-->Касса " + terminalNumber;
                int stringWidth = fontMetrics.stringWidth(String.valueOf(message));
                this.xpos = screenWidth/2 - stringWidth/2;
                this.ypos = levels[levelIndex];
            }
        }
    }

    //a way to add someone to the list of catchers
    public void addClientMessageFormListener(ClientMessageFormListener listener) {
        listeners.add(listener);
    }
}

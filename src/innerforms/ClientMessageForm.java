/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package innerforms;

import innerforms.interfaces.ClientMessageFormListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 10.08.14.
 */
public class ClientMessageForm extends JFrame {
    private JPanel rootPanel;

    private int width;
    private int height;

    private Font TABLE_FONT;
    FontMetrics fontMetrics;

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

        this.width = width;
        this.height = height;

        organizer = new MessageListOrganizer(client, terminal);

        int fontHeight = (height/100) * 13;

        TABLE_FONT = new Font(Font.DIALOG, Font.PLAIN, fontHeight);
        fontMetrics = getFontMetrics(TABLE_FONT);

        setVisible(true);
    }

    public ClientMessageForm(int width, int height){
        //Form Title
        super("Вызов Клиента");

        setContentPane(rootPanel);
        setUndecorated(true);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //on this panels we locating components pragmatically
        rootPanel.setLayout(null);

        setSize(width, height);

        this.width = width;
        this.height = height;
    }

    public void addMessage(int client, int terminal){
        organizer.addRow(client, terminal);
        rootPanel.repaint();
    }

    public void removeMessage(int terminal){
        if(organizer.table.size()==1){
            this.dispose();
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
                g.setFont(TABLE_FONT);
                for (MessageListOrganizer.Row row : organizer.table){
                    g.setColor(Color.WHITE);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.drawString(String.valueOf(row.clientNumber), row.xpos, row.ypos);
                }

            }
        };
    }

    private class MessageListOrganizer {
        private List <Row> table;
        private int levels;

        private MessageListOrganizer(int client, int terminal) {
            this.table = new ArrayList<>();
            this.levels = 1;
            table.add(new Row(levels - 1, client, terminal, 0, 0));
        }

        private void addRow(int client, int terminal){
            levels++;
            table.add(new Row(levels-1, client, terminal, 0, 0));
        }

        private void removeRow(int terminal){
            for (int i=0; i<levels; i++){
                if (table.get(i).terminalNumber == terminal){
                    table.remove(i);
                    levels--;
                    return;
                }
            }
        }

        private class Row{
            private int levelIndex;
            private int clientNumber;
            private int terminalNumber;
            protected int ypos;
            protected int xpos;

            private Row(int levelIndex, int clientNumber, int terminalNumber, int ypos, int xpos) {
                this.levelIndex = levelIndex;
                this.clientNumber = clientNumber;
                this.terminalNumber = terminalNumber;
                this.ypos = ypos;
                this.xpos = xpos;
            }
        }
    }
}

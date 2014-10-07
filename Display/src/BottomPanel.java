/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

/**
 * Created by forando on 05.10.14.
 */
public class BottomPanel extends JPanel {

    int bottomPanelWidth;
    int bottomPanelHeight;

    private Timer defaultBlinker;
    private Timer errorBlinker;
    private Timer stopServiceBlinker;

    public BottomPanel(){
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = e.getComponent().getBounds();
                bottomPanelWidth = (int) r.getWidth();
                bottomPanelHeight = (int) r.getHeight();
                relocateBottomComponents();
            }
        });
    }


    protected void relocateBottomComponents() {
        List<JLabel> labels = listener.getLabels();
        int restOfClients = listener.getRestOfClients();
        java.util.List<Boolean> flags = listener.getFlags();

        int h_percent = bottomPanelHeight / 100;
        int w_percent = bottomPanelWidth / 100;
        int totalDataHeight = h_percent * 90;

        int w_loc;
        int h_loc;

        String labelText;
        int stringWidth;

        boolean PRINTER_ERROR = flags.get(0);
        boolean SERVICE_STOPPED = flags.get(1);

        String fontName = listener.getFontName();

        //=====================TOTAL DATA ========================================

        JLabel l_totalTitle = labels.get(0);
        l_totalTitle.setText("ВСЕГО  В  ОЧЕРЕДИ:");
        l_totalTitle.setFont(new Font(fontName, Font.PLAIN, totalDataHeight));
        labelText = l_totalTitle.getText();
        int totalTitle_stringWidth = l_totalTitle.getFontMetrics(l_totalTitle.getFont()).stringWidth(labelText);
        w_loc = (w_percent * 50) - (totalTitle_stringWidth / 2);
        //w_loc = l_total.getLocation().x - (totalTitle_stringWidth) - (w_percent_uiPanel * 4);
        h_loc = h_percent * 30;
        l_totalTitle.setLocation(w_loc, h_loc);
        l_totalTitle.setSize(totalTitle_stringWidth, totalDataHeight + h_percent * 3);

        JLabel l_total = labels.get(1);
        l_total.setText(String.valueOf(restOfClients));
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

        JLabel l_takeTicket = labels.get(2);
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

        JLabel l_serviceStopped = labels.get(3);
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

    protected void startDefaultBlinker(List<JLabel> labels){
        defaultBlinker = new Timer(listener.getBlinkRates().get(0), new DefaultTimerListener(labels));
        if (errorBlinker != null) {
            errorBlinker.stop();
            errorBlinker = null;
        }
        if (stopServiceBlinker != null) {
            stopServiceBlinker.stop();
            stopServiceBlinker = null;
        }
        relocateBottomComponents();
        defaultBlinker.start();
    }

    protected void startPrinterErrorBlinker(JLabel label){
        errorBlinker = new Timer(listener.getBlinkRates().get(1), new PrinterErrorTimerListener(label));
        if (defaultBlinker != null) {
            defaultBlinker.stop();
            defaultBlinker = null;
        }
        if (stopServiceBlinker != null) {
            stopServiceBlinker.stop();
            stopServiceBlinker = null;
        }
        relocateBottomComponents();
        errorBlinker.start();
    }

    protected void startStopServiceBlinker(List<JLabel> labels){
        stopServiceBlinker = new Timer(listener.getBlinkRates().get(0), new StopServiceListener(labels));
        if (defaultBlinker != null) {
            defaultBlinker.stop();
            defaultBlinker = null;
        }
        if (errorBlinker != null) {
            errorBlinker.stop();
            errorBlinker = null;
        }
        relocateBottomComponents();
        stopServiceBlinker.start();
    }

    protected class DefaultTimerListener implements ActionListener {
        private JLabel l_total;
        private JLabel l_totalTitle;
        private JLabel l_takeTicket;
        private boolean isTakeTicket = true;

        public DefaultTimerListener(List<JLabel> labels) {
            this.l_totalTitle = labels.get(0);
            this.l_total = labels.get(1);
            this.l_takeTicket = labels.get(2);
        }

        public void actionPerformed(ActionEvent e) {

            if (!isTakeTicket) {
                this.l_takeTicket.setText("");
                this.l_total.setText(String.valueOf(listener.getRestOfClients()));
                this.l_totalTitle.setText("ВСЕГО  В  ОЧЕРЕДИ:");
            } else {
                this.l_total.setText("");
                this.l_totalTitle.setText("");
                this.l_takeTicket.setText("ВОЗЬМИТЕ  ТАЛОН");
            }
            isTakeTicket = !isTakeTicket;
        }
    }

    protected class PrinterErrorTimerListener implements ActionListener {
        private JLabel l_takeTicket;
        private boolean isEmpty = true;

        public PrinterErrorTimerListener(JLabel label) {
            this.l_takeTicket = label;
        }

        public void actionPerformed(ActionEvent e) {
            if (isEmpty) {
                l_takeTicket.setText("");
            } else {
                l_takeTicket.setText("ВСТАВЬТЕ  БУМАГУ!");
            }
            isEmpty = !isEmpty;
        }
    }

    protected class StopServiceListener implements ActionListener {
        private JLabel l_takeTicket;
        private JLabel l_serviceStopped;
        private boolean isOutOfService = true;

        public StopServiceListener(List<JLabel> labels) {
                    this.l_takeTicket = labels.get(0);
                    this.l_serviceStopped = labels.get(1);
        }

        public void actionPerformed(ActionEvent e) {
                    if (isOutOfService) {
                        l_takeTicket.setText("");
                        l_serviceStopped.setText("ТЕХНИЧЕСКИЙ  ПЕРЕРЫВ");
                    } else {
                        l_serviceStopped.setText("");
                        l_takeTicket.setText("ТАЛОНОВ  НЕТ");
                    }
                    isOutOfService = !isOutOfService;
        }
    }

    BottomPanelListener listener;

    public void addBottomPanelListener(BottomPanelListener listener)throws Exception{
        if (this.listener != null) throw new Exception("BottomPanelListener has been already assigned");
        this.listener = listener;
    }

    public interface BottomPanelListener{
        public String getFontName();
        public List<JLabel> getLabels();
        public int getRestOfClients();
        public List<Boolean> getFlags();
        public List<Integer> getBlinkRates();
    }
}

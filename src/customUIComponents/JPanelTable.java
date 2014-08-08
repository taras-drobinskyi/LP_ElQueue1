/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package customUIComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Created by forando on 08.08.14.
 */
public class JPanelTable extends JPanel {

    private Point hor_line1_p1 = new Point(10, 10);
    private Point hor_line1_p2 = new Point(20, 20);
    private Point hor_line2_p1 = new Point(100, 100);
    private Point hor_line2_p2 = new Point(200, 200);
    private Point hor_line3_p1 = new Point(100, 100);
    private Point hor_line3_p2 = new Point(200, 200);

    public JPanelTable() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
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
    }
}

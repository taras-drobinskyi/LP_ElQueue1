/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package examples;

/**
 * Created by forando on 13.08.14.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class SlidingPanelDemo
{
    private JLayeredPane layeredPane;
    private JButton button;
    private JPanel pnlSelection;
    private JPanel pnlDetails;
    private JPanel pnlGlass;
    private boolean selectionVisible;
    private MouseListener glassListener;

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new SlidingPanelDemo().createAndShowGUI();
            }
        });
    }

    public void createAndShowGUI()
    {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Sliding Panel Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(createContentPane());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private Container createContentPane()
    {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setOpaque(true);

        pnlSelection = createSelectionPanel();
        pnlDetails = createDetailsPanel();
        pnlGlass = createGlassPanel();

        glassListener = new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent event)
            {
                adjustSelectionPanel();
            }
        };

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(pnlDetails.getSize());
        layeredPane.add(pnlSelection, JLayeredPane.DEFAULT_LAYER, 0);
        layeredPane.add(pnlGlass, JLayeredPane.DEFAULT_LAYER, 0);
        layeredPane.add(pnlDetails, JLayeredPane.DEFAULT_LAYER, 0);

        button = new JButton(">>");
        button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event)
            {
                adjustSelectionPanel();
            }
        });

        contentPane.add(layeredPane, BorderLayout.CENTER);
        contentPane.add(button, BorderLayout.WEST);

        showLayers();

        return contentPane;
    }

    private JPanel createSelectionPanel()
    {
        JPanel panel = new JPanel();
        panel.setName("selection");
        panel.setBackground(Color.YELLOW);
        Border b1 = BorderFactory.createTitledBorder("Selection");
        Border b2 = BorderFactory.createLineBorder(Color.BLUE, 2);
        panel.setBorder(BorderFactory.createCompoundBorder(b2, b1));
        panel.add(new JLabel("Criteria 1"));
        panel.add(new JLabel("Criteria 2"));
        panel.setSize(400, 600);

        return panel;
    }

    private JPanel createDetailsPanel()
    {
        JPanel panel = new JPanel();
        panel.setName("details");
        panel.setBackground(Color.GREEN);
        panel.setBorder(BorderFactory.createTitledBorder("Details"));
        panel.add(new JLabel("Field 1"));
        panel.add(new JTextField(20));
        panel.add(new JLabel("Field 2"));
        panel.add(new JTextField(20));
        panel.setSize(1000, 600);

        return panel;
    }

    private JPanel createGlassPanel()
    {
        JPanel panel = new JPanel();
        panel.setName("glass");
        panel.setOpaque(false);
        panel.setSize(1000, 600);
        return panel;
    }

    private void adjustSelectionPanel()
    {
        int layer = JLayeredPane.DEFAULT_LAYER.intValue();
        // if selection is visible, hide it
        if (selectionVisible)
        {
            layeredPane.setLayer(pnlDetails, layer, 0);
            layeredPane.setLayer(pnlSelection, layer,1);
            layeredPane.setLayer(pnlGlass, layer, 2);
            button.setText(">>");
            pnlGlass.removeMouseListener(glassListener);
        }
        else // show the selection
        {
            layeredPane.setLayer(pnlSelection, layer, 0);
            layeredPane.setLayer(pnlGlass, layer, 1);
            layeredPane.setLayer(pnlDetails, layer, 2);
            button.setText("<<");
            pnlGlass.addMouseListener(glassListener);
        }

        // flip state
        selectionVisible = !selectionVisible;
        showLayers();
    }

    private void showLayers()
    {
        int layer = JLayeredPane.DEFAULT_LAYER.intValue();
        System.out.println("--- Selection visible ? " + selectionVisible);

        Component[] comps = layeredPane.getComponentsInLayer(layer);
        for (Component comp : comps)
        {
            System.out.println("Zorder=" + layeredPane.getComponentZOrder(comp) +
                    ": comp=" + comp.getName());
        }
    }
}

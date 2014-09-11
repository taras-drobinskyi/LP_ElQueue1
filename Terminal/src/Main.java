/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import javax.swing.*;

/**
 * Created by forando on 02.09.14.
 */
public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TerminalForm terminalForm = new TerminalForm();
            }
        });
    }
}

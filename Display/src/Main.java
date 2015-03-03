/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

/**
 * Created by forando on 26.09.14.
 */
public class Main {
    public static void main(String[] args) {

        new NativeDiscovery().discover();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DisplayForm();
            }
        });

        //new DisplayForm();
    }
}

/*
 * Copyright (c) 2015. This code is a LogosProg property. All Rights Reserved.
 */

import javax.swing.*;
import java.awt.*;

/**
 * Created by forando on 26.02.15.
 */
public class KioskDemo {
    public static void main(String[] args) {
        new Host();
        JFrame form = new DisplayForm();
        GraphicsEnvironment env = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice vc = env.getDefaultScreenDevice();
        vc.setFullScreenWindow(form);
    }
}

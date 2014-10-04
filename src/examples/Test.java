/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package examples;

/**
 * Created by forando on 28.08.14.
 */
public class Test {
    public static void main(String[] args) {
        int delay = 500;
        delay = delay*2;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("Iteration!!!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }
}

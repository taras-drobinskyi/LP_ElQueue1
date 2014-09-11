/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

package examples;

/**
 * Created by forando on 28.08.14.
 */
public class Test {
    public static void main(String[] args) {
        //int hex = 0x0a;
        byte b = (byte) 257;
        //System.out.println(hex);
        System.out.println(b);
        eHello();
    }

    private static void eHello() {
        int p = 10;
        hello(new MyParr(p));
    }

    static void hello(MyParr myParr){
        int i = myParr.getMyI();
        Object o = new Object();
    }

    private static class MyParr {
        private final int myI;

        private MyParr(int myI) {
            this.myI = myI;
        }

        public int getMyI() {
            return myI;
        }
    }
}

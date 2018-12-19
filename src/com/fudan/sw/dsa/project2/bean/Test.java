package com.fudan.sw.dsa.project2.bean;

import java.text.DecimalFormat;

public class Test {
    public static void main(String[] args) {
        DecimalFormat format = new DecimalFormat("#.####");
        double dou = 121.123456;
        System.out.println("format method: "+format.format(dou));
        int fo = (int)(dou*10000);
        System.out.println("int method: "+fo);

    }
}

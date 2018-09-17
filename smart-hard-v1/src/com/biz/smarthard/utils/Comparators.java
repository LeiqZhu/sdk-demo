package com.biz.smarthard.utils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Comparators {
    public static class OutTradeNoComparator implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {

            Long i1 = Long.parseLong(s1.substring(3));
            Long i2 = Long.parseLong(s2.substring(3));
            int flag = i1.compareTo(i2);
            return -flag;
        }

    }

    public static void main(String[] args) {
        String s1 = "app120180424095126730";
        String s2 = "app120180420174044930";

        List<String> list = new ArrayList<>();
        list.add(s1);
        list.add(s2);
        System.out.println(list);
        Collections.sort(list,new OutTradeNoComparator());
        System.out.println(list);

    }
}

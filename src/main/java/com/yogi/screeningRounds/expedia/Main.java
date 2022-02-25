package com.yogi.screeningRounds.expedia;

import java.util.Stack;

public class Main {
    enum Color{
        red,
        green,
        blue
    }

    public static void main(String[] args){
        Stack<Integer> stack = new Stack<>();

        stack.push(1);
        System.out.println(Color.red);
    }
}

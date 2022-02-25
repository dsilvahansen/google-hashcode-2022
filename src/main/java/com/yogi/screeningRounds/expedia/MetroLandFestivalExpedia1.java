package com.yogi.screeningRounds.expedia;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetroLandFestivalExpedia1 {
    public static void main(String[] args) {

        List<Integer> xx = Stream.of(5, 2, 3).collect(Collectors.toList());
        List<Integer> yy = Stream.of(3, 4, 7).collect(Collectors.toList());
        List<Integer> numPeople = Stream.of(1, 1, 1).collect(Collectors.toList());
        System.out.println(minimizeCost(numPeople, xx, yy));
    }

    public static int minimizeCost(List<Integer> numPeople, List<Integer> x, List<Integer> y) {
        List<Integer> xMedianArray = new ArrayList<>();
        List<Integer> yMedianArray = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            for (int j = 0; j < numPeople.get(i); j++) {
                xMedianArray.add(x.get(i));
                yMedianArray.add(y.get(i));
            }
        }
        xMedianArray.sort(Comparator.naturalOrder());
        yMedianArray.sort(Comparator.naturalOrder());

        int xFestival = xMedianArray.get(xMedianArray.size() / 2);
        int yFestival = yMedianArray.get(yMedianArray.size() / 2);

        int totalCost = 0;
        for (int i = 0; i < x.size(); i++) {
            totalCost += numPeople.get(i) * (Math.abs(x.get(i) - xFestival) + Math.abs(y.get(i) - yFestival));
        }
        return totalCost;
    }
}
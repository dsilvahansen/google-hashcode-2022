package com.yogi.screeningRounds.expedia;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeleteProductsExpedia1 {
    public static void main(String[] args){
        List<Integer> nums = Stream.of(1,2,3,1,2,2,1).collect(Collectors.toList());
        System.out.println(deleteProducts(nums, 2));
    }

    private static int deleteProducts(List<Integer> ids, int m) {
        HashMap<Integer, Integer> counts = new HashMap<>();
        for(int i:ids){
            counts.put(i, 1 + counts.getOrDefault(i, 0));
        }
        // sort in ascending order by counts
        List<Integer> uniqueNums = ids.stream().distinct()
                .sorted((num1, num2) ->
                        counts.getOrDefault(num1, 0) - counts.getOrDefault(num2, 0))
                .collect(Collectors.toList());

        int deleteLeft = m;
        for(int i=0; i<uniqueNums.size(); i++){
            if(counts.get(uniqueNums.get(i)) <= deleteLeft){
                deleteLeft -= counts.get(uniqueNums.get(i));
            }else{
                return uniqueNums.size() - i;
            }
        }
        return 0;
    }
}

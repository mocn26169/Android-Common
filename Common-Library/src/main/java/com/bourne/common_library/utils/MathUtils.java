package com.bourne.common_library.utils;

/**
 * Created by YXD002 on 2016/10/13.
 */
public class MathUtils {

    public static int getRandom(int begin,int end){
        double random = Math.random();
        return (int)(random*(end - begin)+begin);
    }
}

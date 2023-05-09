package com.voocoo.pet;

import static android.content.ContentValues.TAG;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

import com.voocoo.pet.common.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private List<Integer> petIds = new ArrayList<Integer>();

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void test(){
        petIds.add(1);
        petIds.add(2);
        System.out.println(StringUtils.join(petIds,","));

        String s = TimeUtil.dateToStamp("17:02");
        String s1 = TimeUtil.dateToStamp("16:58");
        System.out.println("test1: "+ s );
        System.out.println("test2: "+ s1 );
        int i = Integer.parseInt(s1) - Integer.parseInt(s);
        System.out.println("test3: " + i );
    }
    @Test
    public void testYangHui(){
        int rows = 5, coef = 1;
//        System.out.println("请输入要打印的行数：");


        for (int i = 0; i < rows; i++) {
//            for (int space = 1; space <= rows - i; space++) {
//                System.out.println("  ");
//            }
            for (int j = 0; j <= i; j++) {
                if (j == 0 || i == 0) {
                    coef = 1;
                } else {
                    coef = coef * (i - j + 1) / j;
                }
                System.out.print(coef);
            }
            System.out.print("\n");
        }

       int i = 0;
        System.out.println(i);
    }
}
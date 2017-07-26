package com.ckt.criminal_note;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd EE");


        Locale locale = new Locale("en","US");
//        java.text.DateFormat fullDateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, locale);

        String date1 =dateFormat.format(date);
//        String date2 =dateFormat1.format(date);

//        DateFormat fullDateFormat = DateFormat.getDateInstance(DateFormat.H);

//
//        System.out.println("date: "+date.toString());
        System.out.println("date: "+date1);
//        System.out.println("date: "+date2);
//        System.out.println("date: "+fullDateFormat.format(date));
    }

}
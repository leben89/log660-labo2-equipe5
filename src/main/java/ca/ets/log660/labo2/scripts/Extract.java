package ca.ets.log660.labo2.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Extract {

    public static String formatDate(String rawDate) {
        String year = rawDate.substring(0, 4);
        String rest = rawDate.substring(4);
        
        String month, day;
        
        if (rest.length() == 4) { // "1025" -> Month 10, Day 25
            month = rest.substring(0, 2);
            day = rest.substring(2);
        } else if (rest.length() == 2) { // "58" -> Month 5, Day 8
            month = rest.substring(0, 1);
            day = rest.substring(1);
        } else { // 3 numbers, "626" or "117"

            int tempMonth = Integer.parseInt(rest.substring(0, 2));

            if (tempMonth >= 1 && tempMonth <= 12) {
                month = rest.substring(0, 2);
                day = rest.substring(2);
            } else {
                month = rest.substring(0, 1);
                day = rest.substring(1);
            }
        }
        
        return String.format("%s/%02d/%02d", year, Integer.parseInt(month), Integer.parseInt(day));
    }
}

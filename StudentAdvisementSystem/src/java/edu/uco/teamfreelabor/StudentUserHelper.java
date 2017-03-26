package edu.uco.teamfreelabor;

import java.util.ArrayList;

/**
 *
 * Temp class to test a student user until the database is made
 */
public class StudentUserHelper {
    public static ArrayList<UCOClass> studentClasses = new ArrayList<>();
    
    public static ArrayList<UCOClass> studentSelectedClasses = new ArrayList<>();
    
    
    static{
        studentClasses.add(new UCOClass("CMSC", "0786", "Database"));
        studentClasses.add(new UCOClass("SE", "4374", "SE2"));
        studentClasses.add(new UCOClass("MATH", "2193", "Calc 4"));
        studentClasses.add(new UCOClass("CMSC", "4421", "Networking"));
        
        studentSelectedClasses.add(new UCOClass("CMSE", "2234", "MAP"));
        studentSelectedClasses.add(new UCOClass("STAT", "2256", "Basic Stat"));
        studentSelectedClasses.add(new UCOClass("MATH", "4221", "Hard Math"));
        studentSelectedClasses.add(new UCOClass("CMSC", "1002", "Networking"));
    }
}
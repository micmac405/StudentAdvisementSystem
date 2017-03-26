package edu.uco.teamfreelabor;

import java.util.ArrayList;

public class StudentUser extends User{
    private String id;
    private String major;
    
    private ArrayList<UCOClass> courses = new ArrayList<>();
    private ArrayList<UCOClass>  selectedCourses = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public ArrayList<UCOClass> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<UCOClass> courses) {
        this.courses = courses;
    }

    public ArrayList<UCOClass> getSelectedCourses() {
        return selectedCourses;
    }

    public void setSelectedCourses(ArrayList<UCOClass> selectedCourses) {
        this.selectedCourses = selectedCourses;
    }
}

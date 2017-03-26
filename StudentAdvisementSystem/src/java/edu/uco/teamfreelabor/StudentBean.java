package edu.uco.teamfreelabor;

import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@RequestScoped
@Named(value = "studentBean")
public class StudentBean implements Serializable{
    private String id;
    private String major;
    private String advisementStatus;
    
    private ArrayList<UCOClass> courses = StudentUserHelper.studentClasses; //new ArrayList<>();
    private ArrayList<UCOClass>  selectedCourses = StudentUserHelper.studentSelectedClasses;//new ArrayList<>();
    
//    @PostConstruct
//    public void init() {
//        super.setUsername("This is a test");
//    }
    
    public String cancelSettings() {
        System.out.println("**************** CANCEL PROFILE ******************");

        return "profile";
    }

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

    public String getAdvisementStatus() {
        return advisementStatus;
    }

    public void setAdvisementStatus(String advisementStatus) {
        this.advisementStatus = advisementStatus;
    }
}
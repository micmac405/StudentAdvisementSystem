package edu.uco.teamfreelabor;

import java.awt.image.BufferedImage;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    private String username;
    private String groups;

    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;

    private BufferedImage profilePhoto;

    private ArrayList<UCOClass> courses = StudentUserHelper.studentClasses; //new ArrayList<>();
    private ArrayList<UCOClass> selectedCourses = StudentUserHelper.studentSelectedClasses;//new ArrayList<>();

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        username = p.getName();
    }

    public BufferedImage getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(BufferedImage profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public void setGroups(String p) {
        this.groups = p;
    }

    public void setEmail(String e) {
        this.email = e;
    }

    public String getUsername() {
        return username;
    }

    public String getGroups() {
        return groups;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<UCOClass> getSelectedCourses() {
        return selectedCourses;
    }

    public void setSelectedCourses(ArrayList<UCOClass> selectedCourses) {
        this.selectedCourses = selectedCourses;
    }

    public ArrayList<UCOClass> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<UCOClass> courses) {
        this.courses = courses;
    }
}
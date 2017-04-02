package edu.uco.teamfreelabor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

/**
 *
 * @author teamfreelabor
 */
public class User {
    private String username;
    private String groups;
    
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String advisementStatus;
    
    private BufferedImage profilePhoto;

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
    
    public void setUsername(String u){
        this.username = u;
    }
    
    public void setGroups(String p){
        this.groups = p;
    }
    
    public void setEmail(String e){
        this.email = e;
    }
    
    public String getUsername(){
        return username;
    }
    
    public String getGroups(){
        return groups;
    }
    
    public String getEmail(){
        return email;
    }
    
    public static List<SelectItem> majorList() {
        return majors;
    }

    public String getAdvisementStatus() {
        return advisementStatus;
    }

    public void setAdvisementStatus(String advisementStatus) {
        this.advisementStatus = advisementStatus;
    }
    
        private final static List<SelectItem> majors;
    static
    {
        majors = new ArrayList<>();
        majors.add(new SelectItem("6100 - Computer Science"));
        majors.add(new SelectItem("6101 - Computer Science - Applied"));
        majors.add(new SelectItem("6102 - Computer Science - Information Science"));
        majors.add(new SelectItem("6110 - Software Engineering"));
        majors.add(new SelectItem("6660 - Applied Mathematics & Computer Science"));
    }
}
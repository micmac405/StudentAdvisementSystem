/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.teamfreelabor;

import java.awt.image.BufferedImage;
import java.util.List;

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
}


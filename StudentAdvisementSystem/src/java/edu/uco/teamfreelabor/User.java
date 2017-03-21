/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.mmckinnon;

import java.util.List;

/**
 *
 * @author alebel
 */
public class User {
    private String username;
    private String groups;
    private String email;
    
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


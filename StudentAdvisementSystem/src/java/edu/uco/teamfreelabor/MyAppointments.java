/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.teamfreelabor;

import java.util.Date;

/**
 *
 * @author alebel
 */
public class MyAppointments {
    //Need this ID to grab the picture *this is not UCO ID*
    private int ID;
    private String firstName;
    private String lastName;
    private Date time;
    
    public void MyAppointments(){
        this.ID = 0;
        this.firstName = "";
        this.lastName = "";
        
    }
    
    public void setID(int i){
        this.ID = i;
    }
    
    public int getID(){
        return ID;
    }
    
    public void setFirstName(String fn){
        this.firstName = fn;
    }
    
    public String getFirstName(){
        return firstName;
    }
    
    public void setLastName(String ln){
        this.lastName = ln;
    }
    
    public String getLastName(){
        return lastName;
    }
    
    public void setTime(Date t){
        this.time = t;
    }
    
    public Date getTime(){
        return time;
    }
    
    
    
}

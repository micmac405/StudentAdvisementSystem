package edu.uco.teamfreelabor;

public class UCOClass {
    private String title;
    private String number;
    private String type;
    
    public UCOClass(String type, String number, String title){
        this.title = title;
        this.number = number;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
package edu.uco.teamfreelabor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;

@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    private String username;
    private String groups;

    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String id;
    private String major;
    private String advisementStatus;

    private BufferedImage profilePhoto;

    private ArrayList<UCOClass> courses = StudentUserHelper.studentClasses; //new ArrayList<>();
    private ArrayList<UCOClass> selectedCourses = StudentUserHelper.studentSelectedClasses;//new ArrayList<>();

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        username = p.getName();

        try {
            loadUserInfo();
        } catch (SQLException ex) {
            Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadUserInfo() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM USERTABLE WHERE USERNAME = '" + username + "'"
            );

            // Get book data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                User u = new User();
                firstName = (result.getString("FIRST_NAME"));
                lastName = (result.getString("LAST_NAME"));
                email = (result.getString("EMAIL"));
                id = (result.getString("UCO_ID"));
                phoneNumber = (result.getString("PHONE_NUMBER"));
                major = (result.getString("MAJOR"));
                advisementStatus = (result.getString("ADVISEMENT_STATUS"));
                
//                Blob imageBlob = resultSet.getBlob(yourBlobColumnIndex);
//                InputStream binaryStream = imageBlob.getBinaryStream(0, imageBlob.length());
            }

        } finally {
            conn.close();
        }
    }

    public String cancelSettings() {
        //Reload the students data from the database so only the saved info will be shown
        try {
            loadUserInfo();
        } catch (SQLException ex) {
            Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "profile";
    }

    public String updateProfile() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            String statement = "update USERTABLE set FIRST_NAME = '" + firstName
                    + "', LAST_NAME = '" + lastName
                    + "' , EMAIL = '" + email
                    + "', UCO_ID = '" + id
                    + "' where USERNAME = '" + username + "'";

            PreparedStatement ps = conn.prepareStatement(statement);

            ps.executeUpdate();

        } finally {
            conn.close();
        }

        return "/studentFolder/profile";
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

    public String getAdvisementStatus() {
        return advisementStatus;
    }

    public void setAdvisementStatus(String advisementStatus) {
        this.advisementStatus = advisementStatus;
    }
}

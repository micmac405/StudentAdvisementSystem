package edu.uco.teamfreelabor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.mail.Session;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;



@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;
    
    //Resource for email already configured in glassfish
    @Resource(name = "mail/WSP")
    private Session session;
    

    private String groups;

    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String id;
    private String major;
    private String advisementStatus;

    private BufferedImage profilePhoto;

    private ArrayList<UCOClass> courses = StudentUserHelper.studentClasses; //new ArrayList<>();
    private ArrayList<UCOClass> selectedCourses = StudentUserHelper.studentSelectedClasses;//new ArrayList<>();
    
    //@NotNull(message = "Enter a Username!")
    @Size(min = 3, message = "Username  must be >= 3 characters!")
    @Pattern(regexp="[a-zA-Z]*", message = "Must be characters only.")
    private String username;
    
    //@NotNull(message = "Enter a Password!")
    @Size(min = 3, message = "Password must be >= 3 characters!")
    private String password;
    
    //@NotNull(message = "Enter an email!")
    @Pattern(regexp = ".{2,}@uco\\.edu$", message = "Must be xx@uco.edu where x is any character!")
    private String email;

    private ArrayList<User> users;


    @PostConstruct
    @Transactional
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if(fc.getExternalContext().getUserPrincipal() != null) {
        Principal p = fc.getExternalContext().getUserPrincipal();
        username = p.getName();
    }

  //start of Student_Profile merge conflict
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
    
    //insert user into db
    public String insert() throws SQLException
    {
        if(ds == null)
        {
            throw new SQLException("Cannot get DataSource. Insert Failed!");
        }
        
        Connection conn = ds.getConnection();
        if(conn == null)
        {
            throw new SQLException("Cannot get Connection. Insert Failed!");
        }        
        try{
            password = encrypt();
            String userTable = "insert into USERTABLE(username, password, email, first_name, last_name,"
                    + "uco_id, major, advisement_status, phone_number)"
                    + "values(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(userTable);
            //right now I am using email as username for temp purposes. I will eventually make a parsing function to get username = "username"@uco.edu
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, firstName);
            ps.setString(5, lastName);
            ps.setString(6, id);
            ps.setString(7, major);
            //advisementStatus = "Done";
            //we dont have advisement status on register.xhtml right now so I am using a string literal
            ps.setString(8, advisementStatus);
            ps.setString(9, phoneNumber);
            ps.executeUpdate();
            
            String groupTable = "insert into GROUPTABLE(groupname, username)"
                    + "values(?,?)";
            PreparedStatement ps2 = conn.prepareStatement(groupTable);
            String customerGroup = "studentgroup";
            ps2.setString(1, customerGroup);
            //also temporarily using email as username
            ps2.setString(2, email);
            ps2.executeUpdate();
            System.out.print("Email about to send!");
            Email sendEmail = new Email();
            sendEmail.sendEmail(session, email);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            conn.close();
        }        
        return "/login";
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

    public void setGroups(String p) {
        this.groups = p;
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
    
    public String getPassword(){
        return password;
    }
    
    public void setUsername(String u){
        this.username = u;
    }
    
    public void setPassword(String p){
        this.password = p;
    }
    
    public void setEmail(String e){
        this.email = e;
    }
    
    public String encrypt(){
        String s = SHA256Encrypt.encrypt(password);
        if(s != null){
            return s;
        }
        else {
            return null;
        }
    }
    
    public ArrayList<User> getUsers(){
        return users;
    }
    
    
    //start of master merge conflict
  /*
        try{
            users = new ArrayList<>();
            users = getUserList();
        } catch(SQLException e){
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    public ArrayList<User> getUserList() throws SQLException{
        if(ds == null){
            throw new SQLException("Cannot get Data Source!");
        }
        
        Connection conn = ds.getConnection();
        if(conn == null){
            throw new SQLException("Cannot get Connection!");
        }
        
        ArrayList<User> users2 = new ArrayList<>();
        
        try{
            String sql = "SELECT usertable.username, usertable.email, grouptable.groupname FROM usertable JOIN grouptable on usertable.username = grouptable.username";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet result = ps.executeQuery();
            
            while(result.next()){
                boolean added = false;
                User newUser = new User();
                newUser.setUsername(result.getString("username"));
                newUser.setGroups(result.getString("groupname"));
                newUser.setEmail(result.getString("email"));
                for(int i = 0; i < users2.size(); i++){
                    User temp = new User();
                    temp = users2.get(i);
                    if(newUser.getUsername().equals(temp.getUsername())){
                        newUser.setGroups(newUser.getGroups() + ", " + temp.getGroups());
                        users2.set(i, newUser);
                        added = true;
                    }
                }
                if(users2.isEmpty() || !added){
                    users2.add(newUser);
                }                
            }            
        }
        finally{
            conn.close();
        }
        return users2;
    }
    
    
        
  */
}

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
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.Session;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.concurrent.ThreadLocalRandom;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;

@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    //Resource for email already configured in glassfish
    @Resource(name = "mail/WSP")

    //@EJB
    //private BackgroundJobManager clearDB;
    private Session session;

    //private String groups; Dont think we need this
    private String firstName;
    private String lastName;

    private String advisementStatus;
    private BufferedImage profilePhoto;

    private ArrayList<UCOClass> courses = StudentUserHelper.studentClasses; //new ArrayList<>();
    private ArrayList<UCOClass> selectedCourses = StudentUserHelper.studentSelectedClasses;//new ArrayList<>();

    @Size(min = 3, message = "Username  must be >= 3 characters!")
    @Pattern(regexp = "[a-zA-Z]*", message = "Must be characters only.")
    private String username;

    @Size(min = 3, message = "Password must be >= 3 characters!")
    private String password;

    @Pattern(regexp = ".{2,}@uco\\.edu$", message = "Must be xx@uco.edu where x is any character!")
    private String email;

    @Pattern(regexp = "\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$", message = "Incorrect Format! Ex:###-###-####")
    private String phoneNumber;

    @Pattern(regexp = "^\\d{8}", message = "UCO ID must be your 8 digit UCO ID Number!")
    private String id;

    private String major;

    //String for code
    private String code;

    private ArrayList<User> users;

    @PostConstruct
    @Transactional
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc.getExternalContext().getUserPrincipal() != null) {
            Principal p = fc.getExternalContext().getUserPrincipal();
            username = p.getName();
            
            //Pull the user group and only load the needed info **********************

            try {
                loadUserInfo(); //Change to loadStudentInfo *****************************
            } catch (SQLException ex) {
                Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
            }
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

            // Get user data from database
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
        return "/faces/studentFolder/profile";
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

    //insert user into tempdb
    public String insertTemp() throws SQLException {
        if (ds == null) {
            throw new SQLException("Cannot get DataSource. Insert Failed!");
        }

        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("Cannot get Connection. Insert Failed!");
        }
        try {

            //first let me check to see whether the user is in the table already
            PreparedStatement exist = conn.prepareStatement(
                    "SELECT * FROM TEMPUSERTABLE WHERE USERNAME = '" + email + "'"
            );

            ResultSet result = exist.executeQuery();
            //if result set returns false then nothing was found in query
            if (result.next() == true) {
                FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Oops email is already taken! Contact Dr.Sung if error!", null);
                FacesContext.getCurrentInstance().addMessage("registrationform:email", facesMsg);
                System.out.print("Inside if statement. Found user in temp table!");
                return "/registration";
            }
            System.out.print("user: " + email + "was not found in temp table! Complete function");

            //lets create random code for their email
            code = "";
            for (int i = 0; i < 5; i++) {
                code += randomDigitString();
            }

            //Now we are ready to insert user into DB
            String userTable = "insert into TEMPUSERTABLE(username, password, email, first_name, last_name,"
                    + "uco_id, major, advisement_status, phone_number, code)"
                    + "values(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(userTable);
            //right now I am using email as username for temp purposes. I will eventually make a parsing function to get username = "username"@uco.edu
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, firstName);
            ps.setString(5, lastName);
            ps.setString(6, id);
            ps.setString(7, major);
            advisementStatus = "Need Advisement!";
            //we dont have advisement status on register.xhtml right now so I am using a string literal
            ps.setString(8, advisementStatus);
            ps.setString(9, phoneNumber);
            ps.setString(10, code);
            major = major.trim();
            System.out.print(major);
            System.out.print("Got this far");
            ps.executeUpdate();
            System.out.print("If you see this it updated db");
            //email debugging to see if it gets this far
            System.out.print("Email about to send!");
            //code debugging
            System.out.print(code);

            Email sendEmail = new Email();
            String message = "Thank you for registering an account with UCO advisement!"
                    + "\n\nIn order to complete the registration process please enter your code"
                    + " on the advisement website.\n\nHere is your code: ";
            sendEmail.sendEmail(session, email, message + code);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        //going to return micah's page
        return "/validation";

    }

    //insert into permanent db
    public String insert() throws SQLException {
        if (ds == null) {
            throw new SQLException("Cannot get DataSource. Insert Failed!");
        }

        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("Cannot get Connection. Insert Failed!");
        }
        try {

            //first let me check to see whether the user is in the table already
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM TEMPUSERTABLE WHERE code = '" + code + "'"
            );

            ResultSet result = ps.executeQuery();

            //If result.next() returns true then the code was found
            if (result.next()) {
                //result.next();

                //Let's get the userinfo
                User u = new User();
                firstName = (result.getString("FIRST_NAME"));
                lastName = (result.getString("LAST_NAME"));
                email = (result.getString("EMAIL"));
                id = (result.getString("UCO_ID"));
                phoneNumber = (result.getString("PHONE_NUMBER"));
                major = (result.getString("MAJOR"));
                advisementStatus = (result.getString("ADVISEMENT_STATUS"));
                password = (result.getString("PASSWORD"));
                password = encrypt();

//                Blob imageBlob = resultSet.getBlob(yourBlobColumnIndex);
//                InputStream binaryStream = imageBlob.getBinaryStream(0, imageBlob.length());
            } //This means this code doesnt exist in the TEMPUSERTABLE we prob need to add error message
            else {
                FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Code! Try again!", null);
                FacesContext.getCurrentInstance().addMessage("validationform:code", facesMsg);
                //System.out.print("INSIDE CODE ERROR");
                return "/validation";
            }

            //Insert into usertable
            String userTable = "insert into USERTABLE(username, password, email, first_name, last_name,"
                    + "uco_id, major, advisement_status, phone_number)"
                    + "values(?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(userTable);
            //right now I am using email as username for temp purposes. I will eventually make a parsing function to get username = "username"@uco.edu
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, firstName);
            ps.setString(5, lastName);
            ps.setString(6, id);
            ps.setString(7, major);
            advisementStatus = "Need Advisement!";
            //we dont have advisement status on register.xhtml right now so I am using a string literal
            ps.setString(8, advisementStatus);
            ps.setString(9, phoneNumber);

            ps.executeUpdate();

            //Insert into grouptable
            String groupTable = "insert into GROUPTABLE(groupname, username) values(?,?)";
            ps = conn.prepareStatement(groupTable);
            ps.setString(1, "studentgroup");
            ps.setString(2, email);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return "/login";
    }

    //encrypt pw
    public String encrypt() {
        String s = SHA256Encrypt.encrypt(password);
        if (s != null) {
            return s;
        } else {
            return null;
        }
    }

    //create random digit and return as string
    public String randomDigitString() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 10);
        return Integer.toString(randomNum);
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

    //public void setGroups(String p) {this.groups = p;}
    //public String getGroups() {return groups;}
    public String getUsername() {
        return username;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String e) {
        this.email = e;
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

    public List<SelectItem> getMajors() {
        return User.majorList();
    }

    public String getAdvisementStatus() {
        return advisementStatus;
    }

    public void setAdvisementStatus(String advisementStatus) {
        this.advisementStatus = advisementStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public String getCode() {
        return "";
    }

    public void setCode(String c) {
        this.code = c;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

}

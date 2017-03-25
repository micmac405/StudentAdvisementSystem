package edu.uco.teamfreelabor;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.naming.InitialContext;
import javax.sql.DataSource;
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
    
    //@NotNull(message = "Enter a Username!")
    @Size(min = 3, message = "Username  must be >= 3 characters!")
    @Pattern(regexp="[a-zA-Z]*", message = "Must be characters only.")
    private String username;
    
    //@NotNull(message = "Enter a Password!")
    @Size(min = 3, message = "Password must be >= 3 characters!")
    private String password;
    
    //@NotNull(message = "Enter an email!")
    @Pattern(regexp = ".{2,}@gmail\\.com$", message = "Must be xx@uco.edu where x is any character!")
    private String email;

    private ArrayList<User> users;


    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        username = p.getName();
        */
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
            String userTable = "insert into USERTABLE(username, password, email)"
                    + "values(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(userTable);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.executeUpdate();
            
            String groupTable = "insert into GROUPTABLE(groupname, username)"
                    + "values(?,?)";
            PreparedStatement ps2 = conn.prepareStatement(groupTable);
            String customerGroup = "studentgroup";
            ps2.setString(1, customerGroup);
            ps2.setString(2, username);
            ps2.executeUpdate();
            System.out.print("Email about to send!");
            Email email = new Email();
            email.sendEmail(session);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            conn.close();
        }        
        return "/index";
        

    }

    public String getUsername() {
        return username;
    }
    
    public String getPassword(){
        return password;
    }
    
    public String getEmail(){
        return email;
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
    
}

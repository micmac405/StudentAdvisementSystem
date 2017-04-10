/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.teamfreelabor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.servlet.http.Part;
import javax.sql.DataSource;



@Named(value = "pictureBean")
@RequestScoped
public class PictureBean  {

    @Inject
    private UserBean userBean;
    
    private Part part;
    private List<PictureInfo> list;

    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    // Note: make getter as simple as possible for performance issues
    public List<PictureInfo> getList() throws SQLException {
        System.out.println("========= getList() ===");
        return list;
    }

    // JSF page: updateList() called explicitly before getter of list (getList())
    public void updateList() throws SQLException {
        System.out.println("========= updateList() ===");
        try {
            list = loadPictureList();
        } catch (SQLException ex) {
            Logger.getLogger(PictureBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<PictureInfo> loadPictureList() throws SQLException {
        
        List<PictureInfo> files = new ArrayList<>();
        Connection conn = ds.getConnection();

        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(
                    "SELECT FILE_ID, FILE_NAME, FILE_TYPE, FILE_SIZE FROM FILESTORAGE"
            );
            while (result.next()) {
                PictureInfo file = new PictureInfo();
                file.setId(result.getLong("FILE_ID"));
                file.setName(result.getString("FILE_NAME"));
                file.setType(result.getString("FILE_TYPE"));
                file.setSize(result.getLong("FILE_SIZE"));
                files.add(file);
            }
        } finally {
            conn.close();
        }
        return files;
    }

    public void uploadFile() throws IOException, SQLException {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Connection conn = ds.getConnection();

        InputStream inputStream;
        inputStream = null;
        try {
            inputStream = part.getInputStream();
            System.out.println("About to insert into FILESTORAGE. Here is USERID: " + userBean.getUserID());
            PreparedStatement insertQuery = conn.prepareStatement(
                    "INSERT INTO FILESTORAGE (FILE_ID, FILE_NAME, FILE_TYPE, FILE_SIZE, FILE_CONTENTS) "
                    + "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE FILE_NAME=VALUES(FILE_NAME), " 
                    + "FILE_TYPE=VALUES(FILE_TYPE), FILE_SIZE=VALUES(FILE_SIZE), FILE_CONTENTS=VALUES(FILE_CONTENTS)");
            insertQuery.setString(1, userBean.getUserID());
            insertQuery.setString(2, part.getSubmittedFileName());
            insertQuery.setString(3, part.getContentType());
            insertQuery.setLong(4, part.getSize());
            insertQuery.setBinaryStream(5, inputStream);
            
            int result = insertQuery.executeUpdate();
            if (result == 1) {
                facesContext.addMessage("uploadForm:upload",
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                part.getSubmittedFileName()
                                + ": uploaded successfuly !!", null));
            } else {
                // if not 1, it must be an error.
                facesContext.addMessage("uploadForm:upload",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                result + " file uploaded", null));
            }
        } catch (IOException e) {
            facesContext.addMessage("uploadForm:upload",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "File upload failed !!", null));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void validateFile(FacesContext ctx, UIComponent comp, Object value) {
        if (value == null) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Select a file to upload", null));
        }
        Part file = (Part) value;
        long size = file.getSize();
        if (size <= 0) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "the file is empty", null));
        }
        if (size > 1024 * 1024 * 10) { // 10 MB limit
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            size + "bytes: file too big (limit 10MB)", null));
        }
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

}
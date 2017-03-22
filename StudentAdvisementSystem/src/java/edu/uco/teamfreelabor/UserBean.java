package edu.uco.teamfreelabor;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    private String username;

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        username = p.getName();
    }

    public String getUsername() {
        return username;
    }

}

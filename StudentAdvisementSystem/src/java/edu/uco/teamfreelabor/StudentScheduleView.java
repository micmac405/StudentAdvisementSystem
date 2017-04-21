package edu.uco.teamfreelabor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@ManagedBean
@SessionScoped
public class StudentScheduleView implements Serializable {

    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    @Inject
    private UserBean userBean;

    private ScheduleModel eventModel;
    private DefaultScheduleEvent event = new DefaultScheduleEvent();

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();

        try {
            readAppointments();
        } catch (SQLException ex) {
            Logger.getLogger(StudentScheduleView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Load the advisement status
    private void updateStatusLabel() {
        RequestContext.getCurrentInstance().update("scheduleForm:eventStatus");
    }

    //Update student status in database
    private void updateStatus() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            //Update the students status to the appointment day and time
            PreparedStatement as = conn.prepareStatement(
                    "UPDATE usertable SET ADVISEMENT_STATUS = '"
                    + currentEventDetails() + "' WHERE USERNAME = '"
                    + userBean.getUsername() + "'"
            );

            as.executeUpdate();

            //Check for another appointment before setting the new one
            as = conn.prepareStatement(
                    "SELECT * FROM appointmenttable WHERE student_id = "
                    + userBean.getUserID());

            ResultSet results = as.executeQuery();

            if (results.next()) {
                as = conn.prepareStatement(
                "UPDATE appointmenttable SET STUDENT_ID = ?, BOOKED = 0 "
                + " WHERE STUDENT_ID = " + userBean.getUserID());
                as.setNull(1, Types.INTEGER);
                as.executeUpdate();
            }

            //Update the event to include the student id
            as = conn.prepareStatement(
                    "UPDATE appointmenttable SET STUDENT_ID = "
                    + userBean.getUserID()
                    + ", BOOKED = 1 WHERE ID =  " + event.getId()
            );

            as.executeUpdate();

            //Update userbean value to match the database
            userBean.setAdvisementStatus(currentEventDetails());
            updateStatusLabel();
        } finally {
            conn.close();
        }

        readAppointments();
    }

    private void readAppointments() throws SQLException {
        //Remove all appointments and reload
        eventModel.clear();

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM APPOINTMENTTABLE WHERE BOOKED = 0 AND APPOINTMENT_TIME >= NOW()"
            );

            // Get non-booked appointments from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                DefaultScheduleEvent e = new DefaultScheduleEvent();
                eventModel.addEvent(e);

                //Set values after adding to event model or id will be changed by the system
                e.setId(String.valueOf(result.getInt("ID")));
                e.setTitle("Appointment");
                e.setStartDate(result.getTimestamp("APPOINTMENT_TIME"));
                e.setEndDate(result.getTimestamp("APPOINTMENT_TIME"));
                e.setData(result.getInt("EVENT_ID"));
            }

        } finally {
            conn.close();
        }
    }

    public void removeEvent(ActionEvent actionEvent) {

    }

    public String currentEventDetails() {
        Date startDate = event.getStartDate();

        if (startDate != null) {
            return new SimpleDateFormat("EEEE, d MMMM hh:mm aaa ").format(event.getStartDate());
        }

        return "";
    }

    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar.getTime();
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(DefaultScheduleEvent event) {
        this.event = event;
    }

    public void addEvent(ActionEvent actionEvent) {
        try {
            updateStatus();
        } catch (SQLException ex) {
            Logger.getLogger(StudentScheduleView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (DefaultScheduleEvent) selectEvent.getObject();
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}

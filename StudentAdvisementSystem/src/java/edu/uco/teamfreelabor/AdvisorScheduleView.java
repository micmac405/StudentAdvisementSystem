package edu.uco.teamfreelabor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@ManagedBean
@ViewScoped
public class AdvisorScheduleView implements Serializable {

    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    @Inject
    private UserBean userBean;
    private String userId;

    private static final int SLOT_TIME_AMOUNT = 10;

    //To insert the date into SQL the date needs to be in this format
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Calendar selectedDate = Calendar.getInstance();
    ;

    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();

        try {
            getUserId();
            loadAdvisorEvents();
        } catch (SQLException ex) {
            Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadAdvisorEvents() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement("select * from EVENTTABLE where ADVISOR_ID = "
                    + userId);

            ResultSet rs = ps.executeQuery();

            ScheduleEvent readEvent;

            while (rs.next()) {
                readEvent = new DefaultScheduleEvent();
                String readId = rs.getString("ID");
                String title = rs.getString("TITLE");
                Date startDate = rs.getTimestamp("START_DATE");
                Date endDate = rs.getTimestamp("END_DATE");

                ((DefaultScheduleEvent) readEvent).setStartDate(startDate);
                ((DefaultScheduleEvent) readEvent).setEndDate(endDate);
                ((DefaultScheduleEvent) readEvent).setTitle(title);
                eventModel.addEvent(readEvent);

                //Set the id after adding it to the eventModel or it will be over written
                readEvent.setId(readId);
            }

        } finally {
            conn.close();
        }
    }

    private void getUserId() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement("select ID from USERTABLE where USERNAME = '"
                    + userBean.getUsername() + "'");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userId = rs.getString("ID");
            }
        } finally {
            conn.close();
        }
    }

    private void makeAppointment() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            //Use calendars to change the times to make time slots from one event (appointment)
            Calendar start = Calendar.getInstance();
            start.setTime(event.getStartDate());

            //With time pnly the time is correct but the day is wrong. Need to set the correct day
            start.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DATE), start.get(Calendar.HOUR), start.get(Calendar.MINUTE));

            Calendar end = (Calendar) start.clone();
            end.setTime(event.getEndDate());

            end.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DATE), end.get(Calendar.HOUR), end.get(Calendar.MINUTE));
            
            //Need to set the correct dates and times for the event
            ((DefaultScheduleEvent)event).setStartDate(start.getTime());
            ((DefaultScheduleEvent)event).setStartDate(end.getTime());

            //Insert the event (appointment)
            PreparedStatement ps = conn.prepareStatement("insert into EVENTTABLE (title, advisor_id, start_date, end_date)"
                    + " values ('" + event.getTitle() + "', " + userId + ", '" + sdf.format(start.getTime()) + "', '" + sdf.format(end.getTime()) + "')");
            ps.execute();

            //Get the last inserted id from this admin
            ResultSet rs = ps.executeQuery("select last_insert_id() as last_id from EVENTTABLE");

            //Must check next before doing anything
            if (rs.next()) {
                //Set the events id to match the database id
                event.setId(rs.getString("last_id"));
            }
        } finally {
            conn.close();
        }
    }

    private void makeTimeSlots() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            Calendar start = Calendar.getInstance();
            start.setTime(event.getStartDate());

            Calendar end = (Calendar) start.clone();
            end.setTime(event.getEndDate());

            PreparedStatement ps;

            //Make time slots from the date
            do {
                //Insert the appointment into the table
                //Must be in the loop or only the first value for start will be used
                ps = conn.prepareStatement(
                        "insert into APPOINTMENTTABLE (event_id, appointment_time, booked)"
                        + " values(" + event.getId() + ", '" + sdf.format(start.getTime()) + "', 0)"
                );
                ps.execute();
                //Make the start time go up by the slot time amount
                start.set(Calendar.MINUTE, start.get(Calendar.MINUTE) + SLOT_TIME_AMOUNT);
            } while (end.after(start));
        } finally {
            conn.close();
        }
    }

    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar.getTime();
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    //Not sure if it is needed
    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void addEvent(ActionEvent actionEvent) {
        if (event.getId() == null) {
            try {
                makeAppointment();
                makeTimeSlots();
                eventModel.addEvent(event);
            } catch (SQLException ex) {
                Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
//            eventModel.updateEvent(event);
            //Allow updating? Not shure on how to handle the database yet.
        }

        event = new DefaultScheduleEvent();
    }

    public void deleteEvent() {
        if (event.getId() != null) {
            eventModel.deleteEvent(event);

            try {
                removeAppointments();
            } catch (SQLException ex) {
                Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void removeAppointments() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            //Remove all apointments
            PreparedStatement ps = conn.prepareStatement("delete from APPOINTMENTTABLE where EVENT_ID = "
                    + event.getId());
            ps.execute();

            ps = conn.prepareStatement("delete from EVENTTABLE where ID = " + event.getId());
            ps.execute();

        } finally {

        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        selectedDate.setTime((Date) selectEvent.getObject());
    }

    public Calendar getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}

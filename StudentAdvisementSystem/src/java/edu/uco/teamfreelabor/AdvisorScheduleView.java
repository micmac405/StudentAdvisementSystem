package edu.uco.teamfreelabor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.mail.Session;
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

    //Needed to convert the event date and time to correct values
    //Using time only for a caledar only sets the time so the date must 
    //be put back in the dates. Java Date does not allow changing the date so 
    //calendar has to be used.
    // The date when the user clicks on a day in the schedule
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();

    //Used to set the limits for the end date time
    private int startHour = 0;
    private int startMinute = 0;

    private ScheduleModel eventModel;

    //The event that holds the selected date and time
    private ScheduleEvent event = new DefaultScheduleEvent();

    //If event is changed but not saved used to reload the event
    private ScheduleEvent eventBackup = new DefaultScheduleEvent();

    //testing for appointment stuffsss -----
    private ArrayList<MyAppointments> myAppointments = new ArrayList<>();

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();

        try {
            getUserId();
            myAppointments = retrieveApps();
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

    //**************************************************************
    //User bean has the id now remove later
    //**************************************************************
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

    //Update a changed appointment
    private void updateAppointment() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        System.out.println("updateAppointment startTime: " + sdf.format(startTime.getTime()));
        System.out.println("updateAppointment endTime: " + sdf.format(endTime.getTime()));

        try {
            PreparedStatement ps
                    = conn.prepareStatement("update EVENTTABLE set TITLE = '"
                            + event.getTitle() + "', START_DATE = '"
                            + sdf.format(startTime.getTime()) + "', END_DATE = '"
                            + sdf.format(endTime.getTime()) + "' WHERE ID = "
                            + event.getId());

            ps.execute();
        } finally {
            conn.close();
        }
    }

    //Put the event the advisor made into the table
    private void makeEvent() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            //Insert the event (appointment)
            PreparedStatement ps = conn.prepareStatement("insert into EVENTTABLE (title, advisor_id, start_date, end_date)"
                    + " values ('" + event.getTitle() + "', " + userId + ", '"
                    + sdf.format(startTime.getTime()) + "', '"
                    + sdf.format(endTime.getTime()) + "')");
            ps.execute();

            //Get the last inserted id from this admin
            ResultSet rs = ps.executeQuery("select last_insert_id() as last_id from EVENTTABLE");
            
            eventModel.addEvent(event);
            
            //Must check next before doing anything
            if (rs.next()) {
                //Set the events id to match the database id
                event.setId(rs.getString("last_id"));
            }
            
            System.out.println("Make event event id: " + event.getId());
        } finally {
            conn.close();

            startHour = 0;
            startMinute = 0;
        }
    }

    //After the event is stored make appointment times for the students to 
    //select
    private void makeAppointments() throws SQLException {
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
                        + " values(" + event.getId() + ", '"
                        + sdf.format(start.getTime()) + "', 0)"
                );
                ps.execute();
                //Make the start time go up by the slot time amount
                start.set(Calendar.MINUTE, start.get(Calendar.MINUTE) + SLOT_TIME_AMOUNT);
            } while (end.after(start));
        } finally {
            conn.close();
        }
    }

    //After the event is changed make new time slots for the difference in time
    private void makeAppointments(Calendar start, Calendar end) throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps;

            System.out.println("makeTimeSlots end  : " + end.getTime());

            //Make time slots from the date
            do {
                //Insert the appointment into the table
                //Must be in the loop or only the first value for start will be used
                ps = conn.prepareStatement(
                        "insert into APPOINTMENTTABLE (event_id, appointment_time, booked)"
                        + " values(" + event.getId() + ", '"
                        + sdf.format(start.getTime()) + "', 0)"
                );
                ps.execute();
                //Make the start time go up by the slot time amount
                System.out.println("makeTimeSlots start: " + start.getTime());

                start.set(Calendar.MINUTE, start.get(Calendar.MINUTE) + SLOT_TIME_AMOUNT);
            } while (end.after(start));
        } finally {
            conn.close();
        }
    }

    //Time only for the schedule put the incorrect date with the start and end 
    //times. Put the correct date with the correct times.
    private void correctDateTime() {
        //Use calendars to change the times to make time slots from one event (appointment)
        startTime.setTime(event.getStartDate());

        //With time pnly the time is correct but the day is wrong. Need to set the correct day
        startTime.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DATE), startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE));

        endTime.setTime(event.getEndDate());

        endTime.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DATE), endTime.get(Calendar.HOUR_OF_DAY),
                endTime.get(Calendar.MINUTE));

        //Need to set the correct dates and times for the event
        ((DefaultScheduleEvent) event).setStartDate(startTime.getTime());
        ((DefaultScheduleEvent) event).setEndDate(endTime.getTime());
    }

    public ScheduleModel getEventModel() {
        return eventModel;
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
                correctDateTime();
                makeEvent();
                makeAppointments();
                
            } catch (SQLException ex) {
                Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            correctDateTime();
            checkEventTimes();
            eventModel.updateEvent(event);
        }

        //Make a blank event for the next selection
        event = new DefaultScheduleEvent();
    }

    //If changes were made and saved check the times to see if the database
    //needs to be updated
    private void checkEventTimes() {
        //The times of the event before the change
        Calendar oldStart = Calendar.getInstance();
        Calendar oldEnd = Calendar.getInstance();

        oldStart.setTime(eventBackup.getStartDate());
        oldEnd.setTime(eventBackup.getEndDate());

        //The times of the event after the change
        Calendar newStart = Calendar.getInstance();
        Calendar newEnd = Calendar.getInstance();

        newStart.setTime(event.getStartDate());
        newEnd.setTime(event.getEndDate());

        //If either date or the title is changed update the appointment
        boolean changesMade = false;

        //Need to check if the start time is past the end time and handle it 
        //differently. SQL Exception happens right now
        //Check start time
        if (oldStart.after(newStart)) {
            changesMade = true;

            System.out.println("checkEventTimes START old after new *********************");
            try {
                makeAppointments(newStart, oldStart);
            } catch (SQLException ex) {
                Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (oldStart.before(newStart)) {
            changesMade = true;

            System.out.println("checkEventTimes START old before new **********************");
            try {
                removeAppointmentsBefore();
            } catch (SQLException ex) {
                Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Check end time
        if (oldEnd.after(newEnd)) {
            changesMade = true;

            System.out.println("checkEventTimes END old after new **********************");
            try {
                removeAppointmentsAfter();
            } catch (SQLException ex) {
                Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (oldEnd.before(newEnd)) {
            changesMade = true;

            System.out.println("checkEventTimes END old before new **********************");
            try {
                makeAppointments(oldEnd, newEnd);
            } catch (SQLException ex) {
                Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Check title
        if (event.getTitle().compareTo(eventBackup.getTitle()) != 0) {
            changesMade = true;
        }

        //Update the appointment in the table
        if (changesMade) {
            try {
                updateAppointment();
            } catch (SQLException ex) {
                Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void deleteEvent() {
        if (event.getId() != null) {
            eventModel.deleteEvent(event);

            try {
                removeAllAppointments();
            } catch (SQLException ex) {
                Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //Remove the appointments before the time change
    private void removeAppointmentsBefore() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            //Find if there are any booked appointments and send emails that they
            //need to reschedule
            PreparedStatement ps = conn.prepareStatement("SELECT u.id, u.email, u.advisement_status "
                    + "FROM USERTABLE u "
                    + "INNER JOIN APPOINTMENTTABLE a on u.id = a.student_id "
                    + "INNER JOIN EVENTTABLE e on e.ID = a.EVENT_ID "
                    + "WHERE a.BOOKED = 1 and e.ID = " + event.getId()
                    + " AND a.APPOINTMENT_TIME < '"
                    + sdf.format(endTime.getTime()) + "'");

            ResultSet results = ps.executeQuery();

            //Change the students status when the appointment is canceled 
            PreparedStatement updateStatus;

            //Send email out to the students
            while (results.next()) {
                sendCancellationEmail(
                        results.getString("EMAIL"),
                        results.getString("ADVISEMENT_STATUS"));

                //Set the status to the default
                updateStatus = conn.prepareStatement("UPDATE usertable SET advisement_status = 'Not Selected' "
                        + "WHERE usertable.id = " + results.getInt("ID"));

                updateStatus.executeUpdate();
            }

            ps = conn.prepareStatement("delete from APPOINTMENTTABLE where EVENT_ID = "
                    + event.getId() + " AND APPOINTMENT_TIME < '"
                    + sdf.format(startTime.getTime()) + "'");
            ps.execute();
        } finally {
            conn.close();
        }
    }

    //Remove the appointments after the time changes
    private void removeAppointmentsAfter() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            //Find if there are any booked appointments and send emails that they
            //need to reschedule
            PreparedStatement ps = conn.prepareStatement("SELECT u.id, u.email, u.advisement_status "
                    + "FROM USERTABLE u "
                    + "INNER JOIN APPOINTMENTTABLE a on u.id = a.student_id "
                    + "INNER JOIN EVENTTABLE e on e.ID = a.EVENT_ID "
                    + "WHERE a.BOOKED = 1 and e.ID = " + event.getId()
                    + " AND a.APPOINTMENT_TIME >= '"
                    + sdf.format(endTime.getTime()) + "'");

            ResultSet results = ps.executeQuery();

            //Change the students status when the appointment is canceled 
            PreparedStatement updateStatus;

            //Send email out to the students
            while (results.next()) {
                sendCancellationEmail(
                        results.getString("EMAIL"),
                        results.getString("ADVISEMENT_STATUS"));

                //Set the status to the default
                updateStatus = conn.prepareStatement("UPDATE usertable SET advisement_status = 'Not Selected' "
                        + "WHERE usertable.id = " + results.getInt("ID"));

                updateStatus.executeUpdate();
            }

            ps = conn.prepareStatement("delete from APPOINTMENTTABLE where EVENT_ID = "
                    + event.getId() + " AND APPOINTMENT_TIME >= '"
                    + sdf.format(endTime.getTime()) + "'");
            ps.execute();
        } finally {
            conn.close();
        }
    }

    //If an event is deleted by an advisor remove all of the appointments
    private void removeAllAppointments() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            //Find if there are any booked appointments and send emails that they
            //need to reschedule
            PreparedStatement ps = conn.prepareStatement("SELECT u.id, u.email, u.advisement_status "
                    + "FROM USERTABLE u "
                    + "INNER JOIN APPOINTMENTTABLE a on u.id = a.student_id "
                    + "INNER JOIN EVENTTABLE e on e.ID = a.EVENT_ID "
                    + "WHERE a.BOOKED = 1 and e.ID = " + event.getId());

            ResultSet results = ps.executeQuery();

            //Change the students status when the appointment is canceled 
            PreparedStatement updateStatus;

            //Send email out to the students
            while (results.next()) {
                sendCancellationEmail(
                        results.getString("EMAIL"),
                        results.getString("ADVISEMENT_STATUS"));

                //Set the status to the default
                updateStatus = conn.prepareStatement("UPDATE usertable SET advisement_status = 'Not Selected' "
                        + "WHERE usertable.id = " + results.getInt("ID"));

                updateStatus.executeUpdate();
            }

            //Remove apointment
            ps = conn.prepareStatement("delete from APPOINTMENTTABLE where EVENT_ID = "
                    + event.getId());
            ps.execute();

            ps = conn.prepareStatement("delete from EVENTTABLE where ID = " + event.getId());
            ps.execute();

        } finally {
            conn.close();
        }
    }

    private void sendCancellationEmail(String email, String date) {
        Email sendEmail = new Email();
        String message = "You appointment on " + date + " has been "
                + "cancled. Please resechedule as soon as possible.";
        Session session = null;
        try {
            sendEmail.sendEmail(session, email, message);
        } catch (Exception ex) {
            Logger.getLogger(AdvisorScheduleView.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Sending emails: " + email + " on date: " + date);
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        selectedDate.setTime(event.getStartDate());

        //Make a backup incase the user cancels 
        ((DefaultScheduleEvent) eventBackup).setTitle(event.getTitle());
        ((DefaultScheduleEvent) eventBackup).setStartDate(event.getStartDate());
        ((DefaultScheduleEvent) eventBackup).setEndDate(event.getEndDate());
    }

    public void cancelEvent() {
        //Put the values that were there back
        ((DefaultScheduleEvent) event).setTitle(eventBackup.getTitle());
        ((DefaultScheduleEvent) event).setStartDate(eventBackup.getStartDate());
        ((DefaultScheduleEvent) event).setEndDate(eventBackup.getEndDate());

        //Reset the min time selection for end time
        setMinTimeSelection();
    }

    //Make a temp event to store the values.
    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(),
                (Date) selectEvent.getObject());

        //Save the date that was selected
        selectedDate.setTime((Date) selectEvent.getObject());
    }

    //Limits the end time so that it can not be before the start time
    private void setMinTimeSelection() {
        //Use a calendar so hour and minute can be pulled
        Calendar time = Calendar.getInstance();
        time.setTime(event.getStartDate());

        startHour = time.get(Calendar.HOUR_OF_DAY);

        //******************BUG*********************
        //Need to only limit minutes when hour is the same
        startMinute = time.get(Calendar.MINUTE);
    }

    //When the start time is changed adjustment may need to be made to the end
    public void onTimeSelect() {
        setMinTimeSelection();

        //Time only sets the time so the selected date needs to be inserted. 
        correctDateTime();

        //Change end date if it is before start time
        //need too use calendar not date
        startTime.setTime(event.getStartDate());
        endTime.setTime(event.getEndDate());

        if (startTime.after(endTime)) {
            ((DefaultScheduleEvent) event).setEndDate(event.getStartDate());
        }
    }

    public ArrayList<MyAppointments> retrieveApps() throws SQLException {
        if (!myAppointments.isEmpty()) {
            myAppointments.clear();
        }

        ArrayList<MyAppointments> temp = new ArrayList<>();

        System.out.println("We at least got to the method!");
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement("select u.FIRST_NAME, "
                    + "u.LAST_NAME, u.ID, a.APPOINTMENT_TIME from appointmenttable a "
                    + "join eventtable e on a.`EVENT_ID` = e.`ID` "
                    + "join usertable u on a.`STUDENT_ID` = u.`ID` "
                    + "where e.`ADVISOR_ID` = ? and a.`BOOKED` = 1;");
            ps.setString(1, userId);
            ResultSet results = ps.executeQuery();
            System.out.println("The Advisor ID = " + userId);
            while (results.next()) {
                System.out.println("We at least started the query");
                MyAppointments newApp = new MyAppointments();
                newApp.setID(results.getInt("ID"));
                newApp.setFirstName(results.getString("FIRST_NAME"));
                newApp.setLastName(results.getString("LAST_NAME"));
                newApp.setTime(results.getTimestamp("APPOINTMENT_TIME"));
                temp.add(newApp);
                System.out.println(newApp);
            }

        } finally {
            conn.close();
        }
        return temp;
    }

    public Calendar getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public ArrayList<MyAppointments> getMyAppointments() {
        return myAppointments;
    }
}

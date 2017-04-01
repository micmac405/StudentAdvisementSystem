package edu.uco.teamfreelabor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@ManagedBean
@SessionScoped
public class StudentScheduleView implements Serializable {

    private ScheduleModel eventModel;
    private DefaultScheduleEvent event = new DefaultScheduleEvent();

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        
        eventModel.addEvent(new DefaultScheduleEvent("Advisement", new Date(), new Date()));
        eventModel.addEvent(new DefaultScheduleEvent("Advisement", new Date(), new Date()));
        eventModel.addEvent(new DefaultScheduleEvent("Advisement", new Date(), new Date()));
        eventModel.addEvent(new DefaultScheduleEvent("Advisement", new Date(), new Date()));
        eventModel.addEvent(new DefaultScheduleEvent("Advisement", new Date(), new Date()));
        eventModel.addEvent(new DefaultScheduleEvent("Advisement", new Date(), new Date()));
    }

    public void removeEvent(ActionEvent actionEvent) {
        System.out.println("****************** remove event ***************");
//        eventModel.deleteEvent(event);
//        event = new DefaultScheduleEvent();
    }
    
    public String currentEventDetails(){
        Date startDate = event.getStartDate();
        
        if(startDate != null){
            return  new SimpleDateFormat("EEEE, d MMMM hh:mm aaa ").format(event.getStartDate());
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
//        if (event.getId() == null) {
//            //Only one appointment for a student clear out old appointments
//            eventModel.clear();
//            event.setTitle("Appointment");
//            event.setStartDate(appointmentDate);
//            eventModel.addEvent(event);
//        } else {
//            eventModel.updateEvent(event);
//        }
//        event = new DefaultScheduleEvent();
        System.out.println("**************** selected an event and save **************");
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (DefaultScheduleEvent) selectEvent.getObject();
    }

//    public void onDateSelect(SelectEvent selectEvent) {
//        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
//    }
//
//    public void onEventMove(ScheduleEntryMoveEvent event) {
//        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
//
//        addMessage(message);
//    }
//
//    public void onEventResize(ScheduleEntryResizeEvent event) {
//        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
//
//        addMessage(message);
//    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}

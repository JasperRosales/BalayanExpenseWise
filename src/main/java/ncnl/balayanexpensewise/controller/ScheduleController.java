package ncnl.balayanexpensewise.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import ncnl.balayanexpensewise.beans.Calendar;
import ncnl.balayanexpensewise.beans.Schedule;
import ncnl.balayanexpensewise.service.ScheduleService;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ScheduleController implements Initializable {

    @FXML
    private TextField findEventField, eventNameField;

    @FXML
    private DatePicker findEventDatePicker, dateManageEvent;

    @FXML
    private RadioButton radioAdd, radioRemove, editEvent;

    @FXML
    private TextArea shortDescTxt;

    @FXML
    private ComboBox<String> orgBox;

    @FXML
    private JFXButton findBtn, submitControlBtn, alertBtn;

    @FXML
    private TableView schedTable;

    @FXML
    private TableColumn<Schedule, String> eventNameCol, dateCol, desccriptionCol;

    private ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();


    private Calendar calendar = new Calendar();
    private ScheduleService scheduleService = new ScheduleService();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        orgBox.getItems().addAll("SSC", "CICS", "CET");
        orgBox.setValue("SSC");

        eventNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEventName()));
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()).asString());
        desccriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        schedTable.setItems(scheduleList);

        Timeline updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> displayAllEvents()),
                new KeyFrame(Duration.seconds(5))
        );


    }


    public void findEvent(ActionEvent actionEvent) {
        String searchQuery = findEventField.getText();
        LocalDate selectedDate = findEventDatePicker.getValue();

        displayEvent(searchQuery ,selectedDate);
    }



    public void handleEvents(ActionEvent actionEvent) {
        String eventName = eventNameField.getText();
        String description = shortDescTxt.getText();
        LocalDate eventDate = dateManageEvent.getValue();

        if (radioAdd.isSelected()) {
            Schedule schedule = new Schedule(eventName, eventDate, description);
            scheduleService.addSchedule(schedule);
            System.out.println("Event added: " + eventName + " on " + eventDate);
        }
        else if (radioRemove.isSelected()) {
            Schedule schedule = scheduleService.getScheduleById(Integer.parseInt(eventName));
            if (schedule != null) {
                scheduleService.deleteSchedule(schedule.getEventName());
                System.out.println("Event removed: " + eventName + " on " + eventDate);
            }
        }
        else if (editEvent.isSelected()) {
            Schedule schedule = scheduleService.getScheduleById(Integer.parseInt(eventName));
            if (schedule != null) {
                schedule.setDescription(description);
                schedule.setDate(eventDate);
                scheduleService.updateSchedule(schedule.getEventName(), schedule);
                System.out.println("Event edited: " + eventName + " on " + eventDate);
            }
        }
    }

    private void displayEvent(String name, LocalDate date) {
            String eventName = name;
            LocalDate eventDate = date;

            List<Schedule> events = scheduleService.getEventsByNameOrDate(eventName, eventDate);

            if (events.isEmpty()) {
                AlarmUtils.showErrorAlert("No events found for the given search criteria.");
            } else {
                ObservableList<Schedule> eventList = FXCollections.observableArrayList(events);
                schedTable.setItems(eventList);
                AlarmUtils.showCustomSuccessAlert("Successfully added");
            }

    }

    private void displayAllEvents() {
        List<Schedule> schedules = scheduleService.getAllSchedules();

        scheduleList.clear();

        scheduleList.addAll(schedules);

        schedTable.setItems(scheduleList);
    }

}

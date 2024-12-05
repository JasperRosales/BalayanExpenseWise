package ncnl.balayanexpensewise.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import ncnl.balayanexpensewise.beans.Schedule;
import ncnl.balayanexpensewise.repository.ScheduleDAO;
import ncnl.balayanexpensewise.service.ScheduleService;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class ScheduleController implements Initializable {

    @FXML
    private TextField findEventField, eventNameField, eventNameChange;

    @FXML
    private DatePicker dateManageEvent;

    @FXML
    private RadioButton radioAdd, radioRemove, editEvent;

    @FXML
    private TextArea shortDescTxt;

    @FXML
    private Label nearestEvent, nearestEventDate;

    @FXML
    private JFXButton submitControlBtn;

    @FXML
    private TableView schedTable;

    @FXML
    private TableColumn<Schedule, String> eventNameCol, dateCol, desccriptionCol;

    @FXML
    private ToggleGroup eventModes;

    private ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();


    private ScheduleService scheduleService = new ScheduleService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateManageEvent.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        eventModes = new ToggleGroup();
        radioAdd.setToggleGroup(eventModes);
        radioRemove.setToggleGroup(eventModes);
        editEvent.setToggleGroup(eventModes);

        eventNameChange.setDisable(true);
        displayAllEvents();
        displayNearestEvent();

        Timeline updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> displayAllEvents()),
                new KeyFrame(Duration.seconds(0), event -> displayNearestEvent()),
                new KeyFrame(Duration.seconds(3))
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();

        eventNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEventName()));
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()).asString());
        desccriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        schedTable.setRowFactory(table -> new TableRow<Schedule>() {
            @Override
            protected void updateItem(Schedule schedule, boolean empty) {
                super.updateItem(schedule, empty);

                if (schedule == null || empty) {
                    setStyle("");
                } else {
                    LocalDate rowDate = schedule.getDate();
                    if (rowDate.isBefore(LocalDate.now())) {
                        setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        FilteredList<Schedule> filteredList = new FilteredList<>(scheduleList, b -> true);

        findEventField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(schedule -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();
                String dateAsString = schedule.getDate().toString();

                return schedule.getEventName().toLowerCase().contains(searchKeyword) ||
                        dateAsString.toLowerCase().contains(searchKeyword);
            });
        });

        schedTable.setItems(filteredList);

        schedTable.setOnMouseClicked(event -> {
            Schedule selectedSchedule = (Schedule) schedTable.getSelectionModel().getSelectedItem();
            if (selectedSchedule != null) {
                populateFields(selectedSchedule);
            }
        });

        eventModes.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == editEvent) {
                eventNameField.setDisable(true);
                eventNameChange.setDisable(false);
            } else {
                eventNameChange.clear();
                eventNameChange.setDisable(true);
                eventNameField.setDisable(false);
            }
        });
    }

    private void populateFields(Schedule schedule) {
        eventNameField.setText(schedule.getEventName());
        eventNameChange.setText(schedule.getEventName());
        dateManageEvent.setValue(schedule.getDate());
        shortDescTxt.setText(schedule.getDescription());
    }

    private void clearFields() {
        eventNameField.clear();
        shortDescTxt.clear();
        dateManageEvent.setValue(null);

    }

    public void handleEvents(ActionEvent actionEvent) {
        String eventName = eventNameField.getText();
        String description = shortDescTxt.getText();
        LocalDate eventDate = dateManageEvent.getValue();

        if (radioAdd.isSelected()) {
            eventNameChange.setDisable(true);
            Schedule schedule = new Schedule(eventName, eventDate, description);
            scheduleService.addSchedule(schedule);

        }
        else if (radioRemove.isSelected()) {
            Integer idDelete = scheduleService.getScheduleIdByDetails(eventName,eventDate);
            if (eventName != null) {
                scheduleService.deleteSchedule(idDelete);
            } else{
                AlarmUtils.showErrorAlert("Something went wrong");
            }
        }
        else if (editEvent.isSelected()) {
            eventNameField.setDisable(true);
            Schedule sched = (Schedule) schedTable.getSelectionModel().getSelectedItem();
            Integer idUpdate = scheduleService.getScheduleIdByDetails(eventName, sched.getEventDate());

            if (idUpdate != null) {
                Schedule schedule = new Schedule(eventNameChange.getText(), eventDate, description);
                scheduleService.updateSchedule(idUpdate, schedule);
            } else {
                AlarmUtils.showErrorAlert("Something went wrong");
            }
        }else{
            AlarmUtils.showErrorAlert("Click a row first!");
        }

        clearFields();
    }

    public void displayNearestEvent(){
        nearestEvent.setText(scheduleService.getNearestEvent().getEventName());
        nearestEventDate.setText(String.valueOf(scheduleService.getNearestEvent().getEventDate()));
    }

    private void displayAllEvents() {
        scheduleList.setAll(scheduleService.getAllSchedules());
    }

}

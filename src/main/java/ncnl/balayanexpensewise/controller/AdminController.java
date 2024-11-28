package ncnl.balayanexpensewise.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;
import ncnl.balayanexpensewise.beans.*;
import ncnl.balayanexpensewise.service.InboxService;
import ncnl.balayanexpensewise.service.TransactionService;
import ncnl.balayanexpensewise.service.UserLoggerService;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private TableView<Inbox> adminTable, userTable;

    @FXML
    private Label userR1, roleR1, eventStatusR1,
            userR2, roleR2, eventStatusR2,
            userR3, roleR3, eventStatusR3,
            userR4, roleR4, eventStatusR4,
            userR5, roleR5, eventStatusR5;


    @FXML
    private TableColumn<Inbox,String> idCol, descriptionCol, departmentCol, categoryCol, amountCol, priceCol, totalPriceCol, timestampCol, remarksCol, controlCol;


    private ObservableList<Inbox> inboxAdminData;
    private ObservableList<UserInbox> inboxUserData;


    private final InboxService inboxService = new InboxService();
    private final TransactionService transactionService = new TransactionService();
    private final UserLoggerService userLoggerService = new UserLoggerService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initiliazeData();
        initiliazeLabels();

        loadInboxData();
        loadTransactionLogs(initiliazeLabels());

        Timeline updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> loadInboxData()),
                new KeyFrame(Duration.seconds(0), event -> loadTransactionLogs(initiliazeLabels())),
                new KeyFrame(Duration.seconds(5))
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();

        adminTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        adminTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Row Selected: " + newValue.getId());
            } else {
                System.out.println("No row selected.");
            }
        });


    }

    public void initiliazeData(){
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        remarksCol.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        controlCol.setCellValueFactory(new PropertyValueFactory<>("control"));
        controlCol.setCellFactory(createButtonCellFactory());
    }

    public List<LabelRow> initiliazeLabels(){
        List<LabelRow> userLogs = Arrays.asList(
                new LabelRow(userR1, roleR1, eventStatusR1),
                new LabelRow(userR2, roleR2, eventStatusR2),
                new LabelRow(userR3, roleR3, eventStatusR3),
                new LabelRow(userR4, roleR4, eventStatusR4),
                new LabelRow(userR5, roleR5, eventStatusR5)
        );

        return userLogs;
    }

    private Callback<TableColumn<Inbox, String>, TableCell<Inbox, String>> createButtonCellFactory() {
        return param -> new javafx.scene.control.TableCell<Inbox, String>() {
            final Button approveButton = new Button("✅");
            final Button rejectButton = new Button("❌");


            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    approveButton.setOnAction(event -> onApproveButtonClick(getTableRow().getItem()));
                    rejectButton.setOnAction(event -> onRejectButtonClick(getTableRow().getItem()));

                    setGraphic(new javafx.scene.layout.HBox(10, approveButton, rejectButton));
                }
            }


        };
    }

    private void loadInboxData() {
        List<Inbox> inboxList = inboxService.getAllInboxRecords();
        inboxAdminData = FXCollections.observableArrayList(inboxList);
        adminTable.setItems(inboxAdminData);

    }

    private void onApproveButtonClick(Inbox inbox) {
        Inbox selectedInbox = adminTable.getSelectionModel().getSelectedItem();

        if (selectedInbox != null) {
            Integer id = selectedInbox.getId();
            String description = selectedInbox.getDescription();
            String department = selectedInbox.getDepartment();
            String category = selectedInbox.getCategory();
            Integer amount = selectedInbox.getAmount();
            Double price = selectedInbox.getPrice();


            Transaction transaction = new Transaction(description, price, amount, category);

            transactionService.addTransaction(department, transaction);
            deleteInboxById(id);


        } else {
            AlarmUtils.showErrorAlert("No Selected Items");
        }
    }

    @FXML
    public void loadTransactionLogs(List<LabelRow> logLabels) {
        List<UserLogger> userLogs = userLoggerService.getRecentLoggingEvents();

        for (int i = 0; i < userLogs.size(); i++) {
            UserLogger logs = userLogs.get(i);


            LabelRow labelRow = logLabels.get(i);

            logs.updateLabels(
                    labelRow.getNameLabel(),
                    labelRow.getRoleLabel(),
                    labelRow.getEventLabel()
            );
        }
    }

    @FXML
    private void onRejectButtonClick(Inbox item) {
        Inbox selectedInbox = adminTable.getSelectionModel().getSelectedItem();

        if (selectedInbox != null && AlarmUtils.showRejectConfirmationAlert()) {
            deleteInboxById(selectedInbox.getId());
        } else {
            AlarmUtils.showErrorAlert("Click a row first to use the buttons");
        }
    }

    private void deleteInboxById(Integer id) {
        inboxService.deleteInboxRecord(id);
    }



}

package ncnl.balayanexpensewise.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Duration;
import ncnl.balayanexpensewise.beans.*;
import ncnl.balayanexpensewise.service.*;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private TableView<Inbox> transTable;
    @FXML
    private TableView<UserInbox> userTable;

    @FXML
    private Label userR1, roleR1, eventStatusR1,
            userR2, roleR2, eventStatusR2,
            userR3, roleR3, eventStatusR3,
            userR4, roleR4, eventStatusR4,
            userR5, roleR5, eventStatusR5;


    @FXML
    private TableColumn<Inbox,String> idCol, descriptionCol, departmentTransCol, categoryCol, amountCol, priceCol, totalPriceCol, timestampCol, remarksCol, controlCol;

    @FXML TableColumn<UserInbox, String> idUserCol, nameCol, roleCol, purposeCol, departmentUserCol , userControl;

    private ObservableList<Inbox> inboxTransData;
    private ObservableList<UserInbox> inboxUserData;
    private final InboxService inboxService = new InboxService();
    private final TransactionService transactionService = new TransactionService();
    private final UserLoggerService userLoggerService = new UserLoggerService();
    private final UserInboxService userInboxService = new UserInboxService();
    private final AdminService adminService = new AdminService();
    private final UserService userService = new UserService();
    private final RequestService requestService = new RequestService();
    private final MainDashboardController controller = new MainDashboardController();

    private List<LabelRow> userLogs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTransData();
        initializeUserData();
        initializeLabels();

        loadInboxData();
        loadTransactionLogs();


        Timeline updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> loadInboxData()),
                new KeyFrame(Duration.seconds(0), event -> loadTransactionLogs()),
                new KeyFrame(Duration.seconds(5))
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();

        transTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        transTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Row Selected: " + newValue.getId());
            } else {
                System.out.println("No row selected.");
            }
        });

        userTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        userTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Row Selected: " + newValue.getId());
            } else {
                System.out.println("No row selected.");
            }
        });
    }

    public void initializeTransData(){
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        departmentTransCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        remarksCol.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        controlCol.setCellValueFactory(new PropertyValueFactory<>("control"));
        controlCol.setCellFactory(createButtonCellFactory());
    }

    public void initializeUserData(){
        idUserCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        purposeCol.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        departmentUserCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        userControl.setCellValueFactory(new PropertyValueFactory<>("control"));
        userControl.setCellFactory(createActionButtonFactory());
    }


    public void initializeLabels(){
        userLogs = Arrays.asList(
                new LabelRow(userR1, roleR1, eventStatusR1),
                new LabelRow(userR2, roleR2, eventStatusR2),
                new LabelRow(userR3, roleR3, eventStatusR3),
                new LabelRow(userR4, roleR4, eventStatusR4),
                new LabelRow(userR5, roleR5, eventStatusR5)
        );
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
                    approveButton.setOnAction(event -> onApproveTransButtonClick(getTableRow().getItem()));
                    rejectButton.setOnAction(event -> onRejectTransButtonClick(getTableRow().getItem()));

                    setGraphic(new javafx.scene.layout.HBox(10, approveButton, rejectButton));
                }
            }
        };
    }

    private Callback<TableColumn<UserInbox, String>, TableCell<UserInbox, String>> createActionButtonFactory() {
        return param -> new TableCell<UserInbox, String>() {
            final Button approveBtn = new Button("✅");
            final Button rejectBtn = new Button("❌");


            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);



                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    UserInbox currentUser = getTableRow().getItem();

                    approveBtn.setOnAction(event -> {
                        onApproveUserButtonClick(currentUser);
                    });

                    rejectBtn.setOnAction(event -> {
                        onRejectUserButtonClick(currentUser);
                    });

                    setGraphic(new HBox(10, approveBtn, rejectBtn));
                }


            }
        };
    }


    private void loadInboxData() {
        List<Inbox> inboxList = inboxService.getAllInboxRecords();
        inboxTransData = FXCollections.observableArrayList(inboxList);
        transTable.setItems(inboxTransData);

        List<UserInbox> userInboxes = userInboxService.getAllUserInboxRecords();
        inboxUserData = FXCollections.observableArrayList(userInboxes);
        userTable.setItems(inboxUserData);

    }

    private void onRejectUserButtonClick(UserInbox item) {
        UserInbox selectedInbox = userTable.getSelectionModel().getSelectedItem();

        if (selectedInbox != null && AlarmUtils.showRejectConfirmationAlert()){
            requestService.deleteRequestById(selectedInbox.getId());
            userInboxService.deleteUserInboxRecord(selectedInbox.getId());
        } else{
            AlarmUtils.showErrorAlert("No selected items");
        }
    }

    private void onApproveUserButtonClick(UserInbox item) {
        UserInbox selectedInbox = userTable.getSelectionModel().getSelectedItem();
        User user = requestService.getRequestById(item.getId());
        Admin admin = requestService.getRequestById(item.getId());

        String currentRole = controller.getRole();

        if (item == null) {
            AlarmUtils.showErrorAlert("No selected items");
            return;
        }

        String status = item.getPurpose();
        User userTemp = new User(user.getSrcode(), user.getFirstName(), user.getMiddleName(), user.getLastName(), user.getRole(), user.getGsuite(), user.getPassword());
        Admin adminTemp = new Admin(admin.getSrcode(), admin.getFirstName(), admin.getMiddleName(), admin.getLastName(), admin.getRole(), admin.getGsuite(), admin.getDepartment(), admin.getOtherRole(), admin.getPassword());

        Integer tableUserId = userService.getIdByRoleAndDepartment(item.getRole(), item.getDepartment());
        Integer tableAdminId = adminService.getTableIdByRoleAndDepartment(item.getRole(),item.getDepartment());

        try {
            switch (status) {
                case "Adding new member to the organization" -> {
                    if (userTemp != null) {
                        if ("ssc".equalsIgnoreCase(item.getDepartment())) {
                            adminService.updateAdmin(tableAdminId, adminTemp);
                            AlarmUtils.showCustomSuccessAlert("Admin added successfully.");
                            requestService.deleteRequestById(selectedInbox.getId());
                            userInboxService.deleteUserInboxRecord(selectedInbox.getId());
                        } else {
                            userService.updateUserById(tableUserId,userTemp);
                            AlarmUtils.showCustomSuccessAlert("User added successfully.");
                            requestService.deleteRequestById(selectedInbox.getId());
                            userInboxService.deleteUserInboxRecord(selectedInbox.getId());
                        }
                    } else {
                        AlarmUtils.showErrorAlert("Failed to retrieve user for addition.");
                    }
                }
                case "Updating member details in the organization" -> {
                    if (userTemp != null) {
                        if ("ssc".equalsIgnoreCase(item.getDepartment())) {
                            adminService.updateAdmin(tableAdminId, adminTemp);
                            AlarmUtils.showCustomSuccessAlert("Admin updated successfully.");
                            requestService.deleteRequestById(selectedInbox.getId());
                            userInboxService.deleteUserInboxRecord(selectedInbox.getId());
                        } else {
                            userService.updateUserById(tableUserId, userTemp);
                            AlarmUtils.showCustomSuccessAlert("User updated successfully.");
                            requestService.deleteRequestById(selectedInbox.getId());
                            userInboxService.deleteUserInboxRecord(selectedInbox.getId());
                        }
                    } else {
                        AlarmUtils.showErrorAlert("Failed to retrieve user for update.");
                    }
                }
                case "Removing member from the organization" -> {
                    if ("ssc".equalsIgnoreCase(item.getDepartment())) {
                        adminService.deleteAdmin(tableAdminId);
                        AlarmUtils.showCustomSuccessAlert("Admin removed successfully.");
                        requestService.deleteRequestById(selectedInbox.getId());
                        userInboxService.deleteUserInboxRecord(selectedInbox.getId());
                    } else {
                        userService.deleteUser(tableUserId);
                        AlarmUtils.showCustomSuccessAlert("User removed successfully.");
                        requestService.deleteRequestById(selectedInbox.getId());
                        userInboxService.deleteUserInboxRecord(selectedInbox.getId());
                    }
                }
                case "Resignation request from the organization" -> {
                    if ("ssc".equalsIgnoreCase(item.getDepartment())) {
                        adminService.deleteAdmin(tableAdminId);
                        AlarmUtils.showCustomSuccessAlert("Resignation processed successfully.");
                        requestService.deleteRequestById(selectedInbox.getId());
                        userInboxService.deleteUserInboxRecord(selectedInbox.getId());
                    } else {
                        userService.deleteUser(tableUserId);
                        AlarmUtils.showCustomSuccessAlert("Resignation processed successfully.");
                        requestService.deleteRequestById(selectedInbox.getId());
                        userInboxService.deleteUserInboxRecord(selectedInbox.getId());
                    }
                }
                default -> AlarmUtils.showErrorAlert("Invalid request purpose.");
            }
        } catch (Exception e) {
            AlarmUtils.showErrorAlert("An error occurred while processing the request: " + e.getMessage());
        }
    }

    private void onApproveTransButtonClick(Inbox inbox) {
        Inbox selectedInbox = transTable.getSelectionModel().getSelectedItem();

        if (selectedInbox != null) {
            Integer id = selectedInbox.getId();
            String description = selectedInbox.getDescription();
            String department = selectedInbox.getDepartment();
            String category = selectedInbox.getCategory();
            Integer amount = selectedInbox.getAmount();
            Double price = selectedInbox.getPrice();

            Transaction transaction = new Transaction(description, price, amount, category);

            transactionService.addTransaction(department, transaction);
            inboxService.deleteInboxRecord(id);
        } else {
            AlarmUtils.showErrorAlert("No Selected Items");
        }
    }

    @FXML
    public void loadTransactionLogs() {
        List<UserLogger> userLogsList = userLoggerService.getRecentLoggingEvents();

        for (int i = 0; i < userLogsList.size(); i++) {
            if (i >= userLogs.size()) break;

            UserLogger logs = userLogsList.get(i);
            LabelRow labelRow = userLogs.get(i);

            logs.updateLabels(
                    labelRow.getNameLabel(),
                    labelRow.getRoleLabel(),
                    labelRow.getEventLabel()
            );
        }
    }

    private void onRejectTransButtonClick(Inbox item) {
        Inbox selectedInbox = transTable.getSelectionModel().getSelectedItem();

        if (selectedInbox != null && AlarmUtils.showRejectConfirmationAlert()) {
            inboxService.deleteInboxRecord(selectedInbox.getId());
        } else {
            AlarmUtils.showErrorAlert("Click a row first to use the buttons");
        }
    }


}

package ncnl.balayanexpensewise.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.util.Duration;
import ncnl.balayanexpensewise.beans.*;
import ncnl.balayanexpensewise.service.LoggerService;
import ncnl.balayanexpensewise.service.ScheduleService;
import ncnl.balayanexpensewise.service.TransactionService;
import ncnl.balayanexpensewise.service.UserInboxService;
import ncnl.balayanexpensewise.utils.DisplayUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SSCController extends GenericController implements Initializable {


    @FXML
    private JFXButton submitBtn, resetBtn;

    @FXML
    private TextField nameFieldR1, quantityR1, priceR1,
            nameFieldR2, quantityR2, priceR2,
            nameFieldR3, quantityR3, priceR3,
            nameFieldR4, quantityR4, priceR4,
            nameFieldR5, quantityR5, priceR5;

    @FXML
    private ComboBox<String> categoryR1, categoryR2, categoryR3, categoryR4, categoryR5,
            departmentR1, departmentR2, departmentR3, departmentR4, departmentR5,
            monthCategory, periodCategory;

    @FXML
    private ProgressBar progressbarCompletion;

    @FXML
    private BarChart<String,Number> monthlyChart;

    @FXML
    private AreaChart<String,Number> yearlyChart;

    @FXML
    private Label currentDate, currentTime,
            budgetSSC, budgetCICS, budgetCET, nextEventDate,
            transferInboxCount, userInboxCount;

    private List<ExpenseRow> rows;

    private final Calendar calendar = new Calendar();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private final TransactionService transactionService = new TransactionService();
    private final ScheduleService scheduleService = new ScheduleService();
    private final LoggerService loggerService = new LoggerService();
    private final UserInboxService userInboxService = new UserInboxService();
    private final Schedule nearDate = scheduleService.getNearestEvent();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        List<ComboBox<String>> categoryBoxes = List.of(categoryR1, categoryR2, categoryR3, categoryR4, categoryR5);
        List<ComboBox<String>> departmentBoxes = List.of(departmentR1, departmentR2, departmentR3, departmentR4, departmentR5);

        DropBox.populateComboBoxes(categoryBoxes, List.of("Expense", "Service"));
        DropBox.populateComboBoxes(departmentBoxes, List.of("SSC", "CICS", "CET"));

        monthCategory.getItems().addAll(calendar.getAllMonths());
        monthCategory.setValue("January");

        periodCategory.getItems().addAll(calendar.getAllPeriods());
        periodCategory.setValue("Academic Year");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> {
                    LocalDateTime now = LocalDateTime.now();
                    currentDate.setText(now.format(dateFormatter));
                    currentTime.setText(now.format(timeFormatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Timeline updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> updateData()),
                new KeyFrame(Duration.seconds(0), event -> addDataToPeriodChart()),
                new KeyFrame(Duration.seconds(5)) // Trigger every 5 seconds
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();

        rows = new ArrayList<>();
        rows.add(new ExpenseRow(nameFieldR1, quantityR1, priceR1, categoryR1, departmentR1));
        rows.add(new ExpenseRow(nameFieldR2, quantityR2, priceR2, categoryR2, departmentR2));
        rows.add(new ExpenseRow(nameFieldR3, quantityR3, priceR3, categoryR3, departmentR3));
        rows.add(new ExpenseRow(nameFieldR4, quantityR4, priceR4, categoryR4, departmentR4));
        rows.add(new ExpenseRow(nameFieldR5, quantityR5, priceR5, categoryR5, departmentR5));

        rows.forEach(row -> {
            row.getNameField().textProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
            row.getQuantityField().textProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
            row.getPriceField().textProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
            row.getCategoryComboBox().valueProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
            row.getDepartmentComboBox().valueProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
        });

        submitBtn.setDisable(true);

        progressbarCompletion.setProgress(0);

    }

    /**
     * Validates the fields of all rows for completeness and partial completeness. Then allows the user to submit
     * the products or service even if there are one or two rows filled.
     *
     */
    private void validateProgressAndSubmit() {
        boolean hasCompleteRow = false;
        boolean hasPartialRow = false;

        for (ExpenseRow row : rows) {
            boolean isRowComplete = isFieldFilled(row.getNameField())
                    && isFieldFilled(row.getQuantityField())
                    && isFieldFilled(row.getPriceField())
                    && isFieldFilled(row.getCategoryComboBox())
                    && isFieldFilled(row.getDepartmentComboBox());

            boolean isRowPartial = isFieldFilled(row.getNameField())
                    || isFieldFilled(row.getQuantityField())
                    || isFieldFilled(row.getPriceField())
                    || isFieldFilled(row.getCategoryComboBox())
                    || isFieldFilled(row.getDepartmentComboBox());

            boolean isPriceZero = isValidPrice(row.getPriceField().getText());
            boolean isQuantityZero = isValidQuantity(row.getQuantityField().getText());

            if (isPriceZero || isQuantityZero) {
                submitBtn.setDisable(true);
                return;
            }

            if (isRowComplete) {
                hasCompleteRow = true;
            } else if (isRowPartial) {
                hasPartialRow = true;
            }
        }

        if (hasPartialRow) {
            submitBtn.setDisable(true);
        } else {
            submitBtn.setDisable(!hasCompleteRow);
        }

        double totalFields = rows.size() * 6;
        double filledFields = 0;

        for (ExpenseRow row : rows) {
            boolean isRowComplete = isFieldFilled(row.getNameField())
                    && isFieldFilled(row.getQuantityField())
                    && isFieldFilled(row.getPriceField())
                    && isFieldFilled(row.getCategoryComboBox())
                    && isFieldFilled(row.getDepartmentComboBox());

            if (isRowComplete) {
                filledFields += 6;
            } else {
                filledFields += (isFieldFilled(row.getNameField()) ? 1 : 0)
                        + (isFieldFilled(row.getQuantityField()) ? 1 : 0)
                        + (isFieldFilled(row.getPriceField()) ? 1 : 0)
                        + (isFieldFilled(row.getCategoryComboBox()) ? 1 : 0)
                        + (isFieldFilled(row.getDepartmentComboBox()) ? 1 : 0);
            }
        }

        double progress = filledFields / totalFields;
        progressbarCompletion.setProgress(progress);
    }

    private boolean isValidPrice(String priceText) {
        try {
            double price = Double.parseDouble(priceText);
            return price == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private boolean isValidQuantity(String quantityText) {
        try {
            double quantity = Double.parseDouble(quantityText);
            return quantity == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private boolean isFieldFilled(TextField field) {
        return field.getText() != null && !field.getText().trim().isEmpty();
    }

    private boolean isFieldFilled(ComboBox<?> comboBox) {
        return comboBox.getValue() != null && !comboBox.getValue().toString().isEmpty();
    }


    @FXML
    private void resetFields() {
        rows.forEach(row -> {
            row.getNameField().clear();
            row.getQuantityField().clear();
            row.getPriceField().clear();
            row.getCategoryComboBox().setValue(null);
            row.getDepartmentComboBox().setValue(null);
        });
        submitBtn.setDisable(true);
    }

    private void updateData() {
        monthlyChart.getData().clear();
        addDataToChart();

        budgetSSC.setText("₱ " + DisplayUtil.getBudget(Table.getTableName("SSC")));
        budgetCICS.setText("₱ " + DisplayUtil.getBudget(Table.getTableName("CICS")));
        budgetCET.setText("₱ " + DisplayUtil.getBudget(Table.getTableName("CET")));

        transferInboxCount.setText(String.valueOf(loggerService.countTransactionInboxRecords()));
        userInboxCount.setText(String.valueOf(userInboxService.countUserInboxRecords()));

        nextEventDate.setText(nearDate.getEventName());
    }


    private void addDataToChart() {
        String selectedMonth = monthCategory.getValue();

        BarChart.Series<String, Number> sscSeries = new BarChart.Series<>();
        sscSeries.setName("SSC");

        BarChart.Series<String, Number> cicsSeries = new BarChart.Series<>();
        cicsSeries.setName("CICS");

        BarChart.Series<String, Number> cetSeries = new BarChart.Series<>();
        cetSeries.setName("CET");

        double sscExpense = Double.parseDouble(DisplayUtil.getTotalExpenseByMonth(Table.getTableName("SSC"), selectedMonth));
        double sscService = Double.parseDouble(DisplayUtil.getTotalServiceByMonth(Table.getTableName("SSC"), selectedMonth));

        double cicsExpense = Double.parseDouble(DisplayUtil.getTotalExpenseByMonth(Table.getTableName("CICS"), selectedMonth));
        double cicsService = Double.parseDouble(DisplayUtil.getTotalServiceByMonth(Table.getTableName("CICS"), selectedMonth));

        double cetExpense = Double.parseDouble(DisplayUtil.getTotalExpenseByMonth(Table.getTableName("CET"), selectedMonth));
        double cetService = Double.parseDouble(DisplayUtil.getTotalServiceByMonth(Table.getTableName("CET"), selectedMonth));

        sscSeries.getData().add(new BarChart.Data<>("Expense", sscExpense));
        sscSeries.getData().add(new BarChart.Data<>("Service", sscService));

        cicsSeries.getData().add(new BarChart.Data<>("Expense", cicsExpense));
        cicsSeries.getData().add(new BarChart.Data<>("Service", cicsService));

        cetSeries.getData().add(new BarChart.Data<>("Expense", cetExpense));
        cetSeries.getData().add(new BarChart.Data<>("Service", cetService));

        monthlyChart.getData().clear();
        monthlyChart.getData().addAll(sscSeries, cicsSeries, cetSeries);
    }


    private void addDataToPeriodChart() {
        AreaChart.Series<String, Number> sscExpenses = new AreaChart.Series<>();
        sscExpenses.setName("SSC Expenses");

        AreaChart.Series<String, Number> cicsExpenses = new AreaChart.Series<>();
        cicsExpenses.setName("CICS Expenses");

        AreaChart.Series<String, Number> cetExpenses = new AreaChart.Series<>();
        cetExpenses.setName("CET Expenses");

        List<Integer> sscData = DisplayUtil.getYearlyExpensesFromDatabase(Table.getTableName("SSC"));
        List<Integer> cicsData = DisplayUtil.getYearlyExpensesFromDatabase(Table.getTableName("CICS"));
        List<Integer> cetData = DisplayUtil.getYearlyExpensesFromDatabase(Table.getTableName("CET"));

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        String periodValue = periodCategory.getValue();
        int[] monthIndexes;

        if ("Academic Year".equalsIgnoreCase(periodValue)) {
            monthIndexes = new int[]{0, 11}; // Jan - Dec
        } else if ("1st Semester".equalsIgnoreCase(periodValue)) {
            monthIndexes = new int[]{7, 11}; // Aug - Dec
        } else if ("2nd Semester".equalsIgnoreCase(periodValue)) {
            monthIndexes = new int[]{0, 5}; // Jan - Jun
        } else {
            monthIndexes = new int[]{0, 11}; // Default to full year
        }

        for (int i = monthIndexes[0]; i <= monthIndexes[1]; i++) {
            int sscValue = i < sscData.size() ? sscData.get(i) : 0;
            int cicsValue = i < cicsData.size() ? cicsData.get(i) : 0;
            int cetValue = i < cetData.size() ? cetData.get(i) : 0;

            sscExpenses.getData().add(new AreaChart.Data<>(months[i], sscValue));
            cicsExpenses.getData().add(new AreaChart.Data<>(months[i], cicsValue));
            cetExpenses.getData().add(new AreaChart.Data<>(months[i], cetValue));
        }

        yearlyChart.getData().clear();
        yearlyChart.getData().addAll(sscExpenses, cicsExpenses, cetExpenses);

    }


    private boolean isRowValid(ExpenseRow row) {
        return isFieldFilled(row.getNameField())
                && isFieldFilled(row.getQuantityField())
                && isFieldFilled(row.getPriceField())
                && isFieldFilled(row.getCategoryComboBox());
    }

    @FXML
    public void handleSubmit(javafx.event.ActionEvent actionEvent) {
        rows.forEach(row -> {
            if (isRowValid(row)) {
                String name = row.getNameField().getText();
                int amount = Integer.parseInt(row.getQuantityField().getText());
                double price = Double.parseDouble(row.getPriceField().getText());
                String category = row.getCategoryComboBox().getValue();
                String department = row.getDepartmentComboBox().getValue();

                Transaction transaction = new Transaction(name, price, amount, category);
                transaction.setTransactionDate(LocalDateTime.now());

                transactionService.addTransaction(department, transaction);
            }
        });
        resetFields();
    }

    public void clearFields(ActionEvent actionEvent) {
        resetFields();
    }
}
package ncnl.balayanexpensewise.beans;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to handle various calendar-related operations for academic years and semesters.
 * It allows retrieval of period ranges (academic year, first semester, second semester)
 * and provides flexibility to get periods for both the current and previous academic years.
 */
public class Calendar {

    public enum Period {
        ACADEMIC_YEAR, FIRST_SEMESTER, SECOND_SEMESTER;
    }


    /**
     * Retrieves all month names in a formatted list, with the first letter capitalized and the rest lowercase.
     *
     * @return a list of strings representing the names of all 12 months.
     */
    public List<String> getAllMonths() {
        List<String> monthNames = new ArrayList<>();
        for (Month m : Month.values()) {
            String monthName = m.name().charAt(0) + m.name().substring(1).toLowerCase();
            monthNames.add(monthName);
        }
        return monthNames;
    }


    /**
     * Returns the number of days in the given month and year.
     * @param year  the year to calculate days for
     * @param month the month to calculate days for (1-12)
     * @return the number of days in the month
     */
    public int daysInMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.lengthOfMonth();
    }

    public int getCurrentMonthDays() {
        LocalDate now = LocalDate.now();
        return daysInMonth(now.getYear(), now.getMonthValue());
    }



    /**
     * Retrieves a list of the three main academic periods: Academic Year, 1st Semester, and 2nd Semester.
     *
     * @return a list of strings representing the different academic periods.
     */
    public List<String> getAllPeriods() {
        List<String> periodNames = new ArrayList<>();
        periodNames.add("Academic Year");
        periodNames.add("1st Semester");
        periodNames.add("2nd Semester");
        return periodNames;
    }

    /**
     * Retrieves the current academic year in the format "Academic Year 2024-2025".
     * It uses the current year and adds 1 to represent the following year in the academic year format.
     *
     * @return the formatted string representing the current academic year (e.g., "Academic Year 2024-2025").
     */
    public String getAcademicYear() {
        int currentYear = LocalDate.now().getYear();
        return "Academic Year " + currentYear + "-" + (currentYear + 1);
    }

    /**
     * Retrieves the timestamp range for the entire academic year (from August to June) for a given year.
     *
     * @param year the starting year of the academic year (e.g., 2023 for the 2023-2024 academic year).
     * @return a list of two LocalDate objects representing the start and end of the academic year.
     */
    public List<LocalDate> getAcademicYearPeriod(int year) {
        List<LocalDate> period = new ArrayList<>();
        period.add(LocalDate.of(year, 8, 1)); // Start of Academic Year (Aug)
        period.add(LocalDate.of(year + 1, 6, 30)); // End of Academic Year (Jun)
        return period;
    }


    /**
     * Retrieves the timestamp range for the 1st Semester (from August to December) for a given year.
     *
     * @param year the year for the 1st semester.
     * @return a list of two LocalDate objects representing the start and end of the 1st Semester.
     */
    public List<LocalDate> getFirstSemesterPeriod(int year) {
        List<LocalDate> period = new ArrayList<>();
        period.add(LocalDate.of(year, 8, 1)); // Start of 1st Semester (Aug)
        period.add(LocalDate.of(year, 12, 31)); // End of 1st Semester (Dec)
        return period;
    }

    /**
     * Retrieves the timestamp range for the 2nd Semester (from January to June) for a given year.
     *
     * @param year the year for the 2nd semester.
     * @return a list of two LocalDate objects representing the start and end of the 2nd Semester.
     */
    public List<LocalDate> getSecondSemesterPeriod(int year) {
        List<LocalDate> period = new ArrayList<>();
        period.add(LocalDate.of(year, 1, 1)); // Start of 2nd Semester (Jan)
        period.add(LocalDate.of(year, 6, 30)); // End of 2nd Semester (Jun)
        return period;
    }

    /**
     * Retrieves the timestamp ranges for all periods (Academic Year, 1st Semester, and 2nd Semester) for the current year.
     *
     * @return a list of lists, where each inner list contains two LocalDate objects representing the start and end of a period.
     */
    public List<List<LocalDate>> getCurrentYearPeriodRanges() {
        int currentYear = LocalDate.now().getYear();
        List<List<LocalDate>> periods = new ArrayList<>();
        periods.add(getAcademicYearPeriod(currentYear)); // Academic Year
        periods.add(getFirstSemesterPeriod(currentYear)); // 1st Semester
        periods.add(getSecondSemesterPeriod(currentYear)); // 2nd Semester
        return periods;
    }

    /**
     * Retrieves the timestamp ranges for all periods (Academic Year, 1st Semester, and 2nd Semester) for the previous academic year.
     *
     * @return a list of lists, where each inner list contains two LocalDate objects representing the start and end of a period.
     */
    public List<List<LocalDate>> getPreviousYearPeriodRanges() {
        int currentYear = LocalDate.now().getYear();
        int previousYear = currentYear - 1;
        List<List<LocalDate>> periods = new ArrayList<>();
        periods.add(getAcademicYearPeriod(previousYear)); // Academic Year (previous year)
        periods.add(getFirstSemesterPeriod(previousYear)); // 1st Semester (previous year)
        periods.add(getSecondSemesterPeriod(previousYear)); // 2nd Semester (previous year)
        return periods;
    }

    /**
     * Retrieves the timestamp range for a specific period (e.g., "1st Semester", "2nd Semester", or "Academic Year")
     * for a given year. The period is specified as a string.
     *
     * @param year the year for which the period range is needed.
     * @param semester the name of the period ("1st Semester", "2nd Semester", or "Academic Year").
     * @return a list of two LocalDate objects representing the start and end of the specified period.
     * @throws IllegalArgumentException if the semester name is not valid.
     */
    public List<LocalDate> getPeriodRange(int year, Period period) {
        switch (period) {
            case ACADEMIC_YEAR:
                return getAcademicYearPeriod(year);
            case FIRST_SEMESTER:
                return getFirstSemesterPeriod(year);
            case SECOND_SEMESTER:
                return getSecondSemesterPeriod(year);
            default:
                throw new IllegalArgumentException("Invalid period");
        }
    }

    /**
     * Retrieves the academic year in the format "Academic Year YYYY-YYYY" for a given offset from the current year.
     * For example, if offset is -1, it returns the academic year for the previous year.
     *
     * @param offset the number of years to offset from the current academic year (e.g., -1 for previous year).
     * @return the academic year as a string in the format "Academic Year YYYY-YYYY".
     */
    public String getAcademicYearByOffset(int offset) {
        int currentYear = LocalDate.now().getYear();
        int startYear = currentYear + offset;
        int endYear = startYear + 1;
        return "Academic Year " + startYear + "-" + endYear;
    }
}

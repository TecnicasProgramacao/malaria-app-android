package com.peacecorps.malaria.app_date;


public class AppDate {

    private int day;
    private int month;
    private int year;

    private final int NUMBER_OF_MONTHS = 12;
    private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    private static final int MAX_VALID_YEAR = 2100;
    private final int MIN_VALID_YEAR = 1900;

    AppDate(int day, int month, int year) throws IllegalMonthException, IllegalDayException, IllegalArgumentException {
        setMonth(month);
        setDay(month, day);
        setYear(year);
    }

    private void setDay(final int month, final int day) throws IllegalDayException {
        if(isValidDay(month, day)) {
            this.day = day;
        }
        else {
            throw new IllegalDayException("Invalid day");
        }

    }

    private void setMonth(final int month) throws IllegalMonthException {
        if(isValidMonth(month)) {
            this.month = month;
        }
        else {
            throw new IllegalArgumentException("Invalid month");
        }

    }

    private void setYear(final int year) throws IllegalArgumentException {
        if(isValidYear(year)) {
            this.year = year;
        }
        else {
            throw new IllegalArgumentException("Invalid year");
        }
    }

    private boolean isValidYear(final int year) {
        boolean ok_year = false;
        if(year >= MIN_VALID_YEAR && year <= MAX_VALID_YEAR) {
            ok_year = true;
        }
        return ok_year;
    }


    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    private boolean isValidDay(final int month, final int day) {
        assert isValidMonth(month) : "month is invalid"; // month must be valid in this step (it was checked before)

        boolean ok_day = false;
        if(daysOfMonth[month] == day) {
                ok_day = true;
        }
        return ok_day;
    }

    private boolean isValidMonth(final int month) {
        return (month >= 0) && (month < NUMBER_OF_MONTHS);
    }
}

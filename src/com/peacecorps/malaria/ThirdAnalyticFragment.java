package com.peacecorps.malaria;

/**
 * Created by Ankita on 6/12/2015.
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.peacecorps.malaria.app_date.AppDate;
import com.peacecorps.malaria.app_date.IllegalMonthException;
import com.peacecorps.malaria.app_date.IllegalYearException;


/* To do: Log messages in separate functions. They are consuming too much visual space.*/

public class ThirdAnalyticFragment extends Activity implements OnClickListener {
    private static final String tag = "ThirdAnalyticFragment";
    public final static String DATE_TAG = "com.peacecorps.malaria.thirdanalyticfragment.SELECTED_DATE";
    private TextView currentMonth = null;
    //private Button selectedDayMonthYearButton;
    private ImageView prevMonth = null;
    private ImageView nextMonth = null;
    private GridView calendarView = null;
    private GridCellAdapter adapter = null;
    private Calendar _calendar = null;
    @SuppressLint("NewApi")
    private int month = 0, year = 0;
    @SuppressWarnings("unused")
    @SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi" })
    private final DateFormat dateFormatter = new DateFormat();
    private static final String dateTemplate = "MMMM yyyy";
    DatabaseSQLiteHelper dbSQLH = new DatabaseSQLiteHelper(this);

    private final int JANUARY = 1;
    private final int DECEMBER = 12;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_third_analytic_screen);

        /** Added by Ankita for getting specific month **/
        Intent intent = getIntent();
        String mon = intent.getStringExtra(SecondAnalyticFragment.MONTH_REQ);
        Calendar cal = Calendar.getInstance();;
        int intmon = 0;
        Date dat = null;
        try{
            dat = new SimpleDateFormat("MMMM").parse(mon);
            cal.setTime(dat);
            intmon= cal.get(Calendar.MONTH);
        }
        catch(ParseException e)
        {
            Log.d(tag,"Parse Month Error!");
        }
        _calendar = Calendar.getInstance();
        year = _calendar.get(Calendar.YEAR);
        _calendar.set(Calendar.MONTH,intmon+1);
        month = _calendar.get(Calendar.MONTH);

        Calendar cal_head = Calendar.getInstance();
        cal_head.set(Calendar.MONTH,intmon);


       /** In above snippet, Calendar is set for Specific Month, not just the current month **/

        Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " + "Year: "
                + year);


        prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) this.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate,
                cal_head.getTime()));

        nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (GridView) this.findViewById(R.id.calendar);

        // Initialised
        adapter = new GridCellAdapter(getApplicationContext(),
                R.id.calendar_day_gridcell, month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    /**
     *
     * @param month
     * @param year
     */

    /**Setting Adapter to each Grid Cell of Calendar**/
    private void setGridCellAdapterToDate(int month, int year) {
        assert (month >= JANUARY && month <= DECEMBER) : ("Month is not between January (1) and December (12)");
        assert year <= Calendar.YEAR;


        adapter = new GridCellAdapter(getApplicationContext(),
                R.id.calendar_day_gridcell, month, year);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    /**
     * Defines next or preivous month, according to parameter view
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        try {
            // Current month should be changed to it's previous month
            if(view == prevMonth) {
                setPreviousMonth();
            }
            // Current month should be changed to it's next month
            if(view == nextMonth) {
                setNextMonth();
            }
        }
        catch(IllegalMonthException illegalMonthException) {
            // To do.
        }
        catch(IllegalYearException illegalYearException) {
            // To do.
        }

        // Postcondition
        assertMonthIsValid(month);
    }

        @Override
        public void onDestroy() {
            Log.d(tag, "Destroying View ...");
            super.onDestroy();
        }

        @Override
        public void onResume(){
            super.onResume();
            adapter.notifyDataSetChanged();
            calendarView.setAdapter(adapter);
        }

        // Inner Class
        public class GridCellAdapter extends BaseAdapter implements OnClickListener {
            private static final String tag = "GridCellAdapter";
            private final Context _context;

            private final List<String> list;
            private static final int DAY_OFFSET = 1;
            private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
                    "Wed", "Thu", "Fri", "Sat" };
            private final String[] months = { "January", "February", "March",
                    "April", "May", "June", "July", "August", "September",
                    "October", "November", "December" };
            private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
                    31, 30, 31 };
            private int daysInMonth = 0;
            private int currentDayOfMonth = 0;
            private int currentWeekDay = 0;
            private Button gridcell = null;
            private TextView num_events_per_day = null;
            private final HashMap<String, Integer> eventsPerMonthMap;
            private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
                    "dd-MMM-yyyy");

            // Days in Current Month
            public GridCellAdapter(Context context, int textViewResourceId,
                                   int month, int year) {
                super();
                this._context = context;
                this.list = new ArrayList<String>();
                Log.d(tag, "==> Passed in Date FOR Month: " + month + " "
                        + "Year: " + year);

                Calendar calendar = Calendar.getInstance();
                setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
                setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

                Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
                Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
                Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

                // Print Month
                printMonth(month, year);

                // Find Number of Events
                eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
            }
            Intent intent = new Intent(getApplication(),DayFragmentActivity.class);

            private String getMonthAsString(int i) {
                return months[i];
            }

            private String getWeekDayAsString(int i) {
                return weekdays[i];
            }

            private int getNumberOfDaysOfMonth(int i) {
                return daysOfMonth[i];
            }

            public String getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }

            /**
             * Prints Month
             *
             * @param mm
             * @param yy
             */
            private void printMonth(int mm, int yy) {
                Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
                int trailingSpaces = 0;
                int daysInPrevMonth = 0;
                int prevMonth = 0;
                int prevYear = 0;
                int nextMonth = 0;
                int nextYear = 0;

                int currentMonth = mm - 1;
                String currentMonthName = getMonthAsString(currentMonth);
                daysInMonth = getNumberOfDaysOfMonth(currentMonth);

                Log.d(tag, "Current Month: " + " " + currentMonthName + " having "
                        + daysInMonth + " days.");

                GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
                Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

                /* Updating to next or previous month and year, according to the case.
                 * Handling cases where the next mont is on the next year, or previous month is on the previous year.
                 *
                 * Example: next month of DECEMBER 2016 is JANUARY 2017
                 *          previous month of JANUARY 2014 is DECEMBER 2013
                 */
                if (currentMonth == 11) {
                    prevMonth = currentMonth - 1;
                    daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);

                    nextMonth = getNextMonth(currentMonth);
                    assertMonthIsValid(nextMonth);

                    prevYear = yy;
                    nextYear = yy + 1;
                    Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
                            + prevMonth + " NextMonth: " + nextMonth
                            + " NextYear: " + nextYear);
                } else if (currentMonth == 0) {
                    prevMonth = 11;
                    prevYear = yy - 1;
                    nextYear = yy;
                    daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);

                    nextMonth = getNextMonth(currentMonth);
                    assertMonthIsValid(nextMonth);

                    Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:"
                            + prevMonth + " NextMonth: " + nextMonth
                            + " NextYear: " + nextYear);
                } else {
                    prevMonth = currentMonth - 1;

                    nextMonth = getNextMonth(currentMonth);
                    assertMonthIsValid(nextMonth);
                    
                    nextYear = yy;
                    prevYear = yy;
                    daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                    Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:"
                            + prevMonth + " NextMonth: " + nextMonth
                            + " NextYear: " + nextYear);
                }

                int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
                trailingSpaces = currentWeekDay;

                Log.d(tag, "Week Day:" + currentWeekDay + " is "
                        + getWeekDayAsString(currentWeekDay));
                Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
                Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

                if (cal.isLeapYear(cal.get(Calendar.YEAR)))
                    if (mm == 2)
                        ++daysInMonth;
                    else if (mm == 3)
                        ++daysInPrevMonth;

                // Trailing Month days
                for (int i = 0; i < trailingSpaces; i++) {
                    Log.d(tag,
                            "PREV MONTH:= "
                                    + prevMonth
                                    + " => "
                                    + getMonthAsString(prevMonth)
                                    + " "
                                    + String.valueOf((daysInPrevMonth
                                    - trailingSpaces + DAY_OFFSET)
                                    + i));
                    list.add(String
                            .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                                    + i)
                            + "-GREY"
                            + "-"
                            + getMonthAsString(prevMonth)
                            + "-"
                            + prevYear);
                }

                // Current Month Days
                for (int i = 1; i <= daysInMonth; i++) {
                    Log.d(currentMonthName, String.valueOf(i) + " "
                            + getMonthAsString(currentMonth) + " " + yy);
                    if (i == getCurrentDayOfMonth()) {
                        list.add(String.valueOf(i) + "-BLUE" + "-"
                                + getMonthAsString(currentMonth) + "-" + yy);
                    } else {
                        list.add(String.valueOf(i) + "-WHITE" + "-"
                                + getMonthAsString(currentMonth) + "-" + yy);
                    }
                }

                // Leading Month days
                for (int i = 0; i < list.size() % 7; i++) {
                    Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
                    list.add(String.valueOf(i + 1) + "-GREY" + "-"
                            + getMonthAsString(nextMonth) + "-" + nextYear);
                }
            }

            /**
             *  Given the YEAR, MONTH, retrieve
             * ALL entries from a SQLite database for that month. Iterate over the
             * List of All entries, and get the dateCreated, which is converted into
             * day.
             *
             * @param year
             * @param month
             * @return
             */
            private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,
                                                                        int month) {
                HashMap<String, Integer> map = new HashMap<String, Integer>();

                return map;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            /**Setting Up the View
             * View is Set up accoeding to the SQLite Database Entries
             *
             * @param position
             * @param convertView
             * @param parent
             * @return
             */
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;
                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) _context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    row = inflater.inflate(R.layout.screen_gridcell, parent, false);
                }

                // Get a reference to the Day gridcell
                gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
                gridcell.setOnClickListener(this);

                // ACCOUNT FOR SPACING

                //Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
                String[] day_color = list.get(position).split("-");
                String theday = day_color[0];
                String themonth = day_color[2];
                String theyear = day_color[3];
                if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
                    if (eventsPerMonthMap.containsKey(theday)) {
                        num_events_per_day = (TextView) row
                                .findViewById(R.id.num_events_per_day);
                        Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
                        num_events_per_day.setText(numEvents.toString());
                    }
                }

                // Set the Day GridCell
                gridcell.setText(theday);
                gridcell.setTag(theday + "-" + themonth + "-" + theyear);
                gridcell.setBackgroundResource(R.drawable.calendar_button_selector);
                /**Customization, of each grid cell on basis of query, added by Ankita**/
                int status = 0;
                status = dbSQLH.isEntered(Integer.parseInt(theday),
                        getMonthNumber(themonth),
                        Integer.parseInt(theyear));
                Drawable dr;
                //Setting the Drawables according to Taken or Not Taken
                switch (status) {
                    case 0:
                        dr = getResources().getDrawable(R.drawable.accept_medi_checked_tiny);
                        gridcell.setCompoundDrawablesWithIntrinsicBounds(null, dr, null, null);
                        gridcell.setTextColor(getResources().getColor(R.color.green));
                        break;
                    case 1:
                        dr = getResources().getDrawable(R.drawable.reject_medi_checked_tiny);
                        gridcell.setCompoundDrawablesWithIntrinsicBounds(null, dr, null, null);
                        gridcell.setTextColor(getResources().getColor(R.color.dark_red));
                        break;
                    case 2:
                        gridcell.setBackgroundResource(R.drawable.calendar_button_selector);
                        gridcell.setTextColor(getResources().getColor(R.color.golden_brown));
                        break;
                    default:
                        // Do nothing.
                }

                Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"
                        + theyear);

               /* if (day_color[1].equals("GREY")) {
                    gridcell.setTextColor(getResources()
                            .getColor(R.color.golden_brown));
                }
                if (day_color[1].equals("WHITE")) {
                    gridcell.setTextColor(getResources().getColor(
                            R.color.lightgray02));
                }
                if (day_color[1].equals("BLUE")) {
                    gridcell.setTextColor(getResources().getColor(R.color.blue));
                }*/
                return row;
            }

            @Override
            public void onClick(View view) {
                String date_month_year = (String) view.getTag();
                //selectedDayMonthYearButton.setText(" " + date_month_year);
                Log.e("Selected dates", date_month_year);
                Date parsedDate = Calendar.getInstance().getTime();
                try {
                    parsedDate = dateFormatter.parse(date_month_year);
                    Log.d(tag, "Parsed Date: " + parsedDate.toString());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String selectedDate= parsedDate.toString();
                intent.putExtra(DATE_TAG, selectedDate);
                startActivity(intent);

            }

            public int getCurrentDayOfMonth() {
                return currentDayOfMonth;
            }

            private void setCurrentDayOfMonth(int currentDayOfMonth) {
                this.currentDayOfMonth = currentDayOfMonth;
            }

            public void setCurrentWeekDay(int currentWeekDay) {
                this.currentWeekDay = currentWeekDay;
            }

            public int getCurrentWeekDay() {
                return currentWeekDay;
            }

            /*
             Getting the number of Month from the Progress Bars in Second Analaytic Fragment
             */
            public int getMonthNumber(String month)
            {
                SimpleDateFormat sdf=  new SimpleDateFormat("MMMM");
                Date date = Calendar.getInstance().getTime();
            try {
                date = sdf.parse(month);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int monthInteger = cal.get(Calendar.MONTH);
            Log.d("ThirdAnalyticFragment","Month Integer is:"+monthInteger);
            return monthInteger;
        }

    }

    /**
     * Calculates the preivous month, according to the current month.
     */
    private void setPreviousMonth() throws IllegalMonthException, IllegalYearException {
        AppDate appDate = new AppDate(month, year);
        appDate.setToPreviousMonth();
        month = appDate.getMonth();
        year = appDate.getYear();

        Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: "
                + month + " Year: " + year);
        setGridCellAdapterToDate(month, year);
    }

    /**
     * Calculates the next month, according to the current month.
     */
    private void setNextMonth() throws IllegalMonthException, IllegalYearException {
        AppDate appDate = new AppDate(month, year);
        appDate.setToNextMonth();
        month = appDate.getMonth();
        year = appDate.getYear();

        Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: "
                + month + " Year: " + year);
        setGridCellAdapterToDate(month, year);
    }

    /**
     * Assert that month is between JANUARY and DECEMBER.
     * @param month
     */
    private void assertMonthIsValid(final int month) {
        assert (month >= JANUARY && month <= DECEMBER) : ("Month is not between January (1) and December (12)");
    }

    /**
     * Returns the next month of the year according to the current month.
     * @param currentMonth
     * @return next month of the year
     */
    private int getNextMonth(final int currentMonth) {
        assert (currentMonth >= JANUARY && currentMonth <= DECEMBER) : ("Month is not between January (1) and December (12)");

        // Precondition.
        assertMonthIsValid(currentMonth);

        int nextMonth = -1; // Next month of the year.

        // Next month lies on the current year.
        if(currentMonth != DECEMBER) {
            nextMonth = currentMonth + 1;
        }
        else { // Next month lies on the next year.
            nextMonth = JANUARY;
        }

        //Post condition.
        assertMonthIsValid(nextMonth);

        return nextMonth;
    }
}

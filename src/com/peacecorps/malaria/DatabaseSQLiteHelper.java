package com.peacecorps.malaria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chimdi on 7/18/14.
 * Edited by Ankita
 **/
public class DatabaseSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MalariaDatabase";
    private static final String USER_MEDICATION_CHOICE_TABLE = "userSettings";
    private static final String APP_SETTING_TABLE = "appSettings";
    private static final String LOCATION_TABLE = "locationSettings";
    private static final String PACKING_TABLE = "packingSettings";
    private static final String TAG_DATABASE_HELPER = "DatabaseSQLiteHelper";
    public static final String LOCATION = "Location";
    public static final String KEY_ROW_ID = "_id";

    private static final String EMPTY_STRING = "";
    private static final int INT_ZERO = 0;

    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};
    private final int[] daysOfMonthLeap = {31, 29, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};

    public DatabaseSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(final SQLiteDatabase database) {
        //Creating Tables
        database.execSQL("CREATE TABLE " + USER_MEDICATION_CHOICE_TABLE
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, Drug INTEGER,Choice VARCHAR, "
                + "Month VARCHAR, Year VARCHAR,Status VARCHAR,Date INTEGER, "
                + "Percentage DOUBLE, Timestamp VARCHAR);");
        database.execSQL("CREATE TABLE " + APP_SETTING_TABLE
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, Drug VARCHAR, Choice VARCHAR,"
                + "WeeklyDay INTEGER, FirstTime LONG, FreshInstall VARCHAR);");
        database.execSQL("CREATE TABLE " + LOCATION_TABLE
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, Location VARCHAR, Times INTEGER);");
        database.execSQL("CREATE TABLE " + PACKING_TABLE
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, PackingItem VARCHAR,"
                + "Quantity INTEGER, Status VARCHAR);");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {

    }

    public static ArrayList<Double> percentages;
    public static ArrayList<Integer> dates;

    /**Method to Update the Progress Bars**/
    public int getData(final int month, final int year, final String choice) {

        percentages= new ArrayList<Double>();
        dates= new ArrayList<Integer>();
        int count = INT_ZERO;

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String column[] = {"_id", "Date", "Percentage"};
        String args[] = {EMPTY_STRING + month, EMPTY_STRING + year, "yes", choice};
        Cursor cursor = sqLiteDatabase.query(USER_MEDICATION_CHOICE_TABLE, column,
                "Month =? AND Year =? AND Status =? AND Choice =?", args, null, null, "Date ASC");

        boolean isDataFound = false;

        while (cursor.moveToNext()) {
            isDataFound = true;
            count += 1;

            dates.add(cursor.getInt(1));
            percentages.add(cursor.getDouble(2));

            Log.d(TAG_DATABASE_HELPER, "INSIDE GET DATA DATE: " + cursor.getInt(1));
            Log.d(TAG_DATABASE_HELPER, "INSIDE GET DATA PERCENTAGE:" + cursor.getDouble(2));

        }

        if (isDataFound) {
            if (!(dates.get(dates.size() - 1) == Calendar.getInstance().get(Calendar.DATE))) {
                percentages.add(0.0);
                dates.add(Calendar.getInstance().get(Calendar.DATE));
            }
        }

        sqLiteDatabase.close();
        Log.d(TAG_DATABASE_HELPER, EMPTY_STRING + count);

        return count;
    }

    /**Method to Update the User Selection of Medicine and it's Status of whether Medicine was taken or not.
     * Used in Alert Dialog to Directly update the current Status
     * Used in Home Screen Fragment for updating the current status through tick marks**/
    public void getUserMedicationSelection(final Context context, final String choice, final Date date,
                                           final String status, final Double percentage) {
        ContentValues values = new ContentValues(2);

        Calendar calendarAux;
        calendarAux = Calendar.getInstance();
        calendarAux.setTime(date);

        int dayOfMonth = calendarAux.get(Calendar.DATE);
        String timeStamp = EMPTY_STRING;

        if (dayIsSingleDigit(dayOfMonth)) {
            timeStamp = EMPTY_STRING + calendarAux.get(Calendar.YEAR) + "-"
                    + calendarAux.get(Calendar.MONTH) + "-" + calendarAux.get(Calendar.DATE);
        } else {
            timeStamp = EMPTY_STRING + calendarAux.get(Calendar.YEAR) + "-"
                    + calendarAux.get(Calendar.MONTH) + "-0" + calendarAux.get(Calendar.DATE);
        }

        Log.d(TAG_DATABASE_HELPER, timeStamp);

        values.put("Drug", SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.drug",
                INT_ZERO));
        values.put("Choice", choice);
        values.put("Month", EMPTY_STRING + calendarAux.get(Calendar.MONTH));
        values.put("Year", EMPTY_STRING + calendarAux.get(Calendar.YEAR));
        values.put("Status", status);
        values.put("Date", calendarAux.get(Calendar.DATE));
        values.put("Percentage", percentage);
        values.put("Timestamp", timeStamp);

        this.getWritableDatabase().insert(USER_MEDICATION_CHOICE_TABLE, "medication", values);
        this.getWritableDatabase().close();
    }

    /**Getting Medication Data of Each Day in Day Fragment Activity**/
    public String getMedicationData(final int date, final int month, final int year) {
        //1~12 valid months intervals for one year
        assert month >= 1 && month <= 12;
        //1~31 valid day intervals for one month
        assert date >= 1 && date <= 31;
        //2000~current year valid years intervals
        assert year >= 2000 && year <= Calendar.YEAR;

        SQLiteDatabase systemQueryDatabase = getReadableDatabase();

        String choice = "daily";
        String[] columns = {"_id", "Date", "Percentage", "Status"};
        String[] selArgs = {EMPTY_STRING + month, EMPTY_STRING + year, choice};

        StringBuffer buffer = new StringBuffer();
        Cursor cursor = systemQueryDatabase.query(USER_MEDICATION_CHOICE_TABLE, columns,
                "Month =? AND Year " + "=? AND Choice =?", selArgs, null, null, null, null);

        int columnOfDate, columnStatus;

        /**Queried for a Month in an Year, stopping when the dates required is found**/
        while (cursor.moveToNext()) {

            columnOfDate = cursor.getColumnIndex("Date");
            columnStatus = cursor.getColumnIndex("Status");

            int dateOfTableDatabbase = cursor.getInt(columnOfDate);
            String statusQueried = cursor.getString(columnStatus);

            Log.d(TAG_DATABASE_HELPER, "Passed Date:" + date + "Found dates:" +
                    dateOfTableDatabbase);
            Log.d(TAG_DATABASE_HELPER, EMPTY_STRING + year);

            if (dateOfTableDatabbase == date) {
                buffer.append(statusQueried);
            }
        }
        systemQueryDatabase.close();
        return buffer.toString();
    }

    /**Method to Modify the entry of Each Day**/
    public void updateMedicationEntry(final int date, final int month, final int year,
                                      final String entry, final double percentage){

        //1~12 valid months intervals for one year
        assert month >= 1 && month <= 12;
        //1~31 valid day intervals for one month
        assert date >= 1 && date <= 31;
        //2000~current year valid years intervals
        assert year >= 2000 && year <= Calendar.YEAR;

        SQLiteDatabase systemQueryDatabase = getReadableDatabase();
        ContentValues values = new ContentValues(2);

        values.put("Status", entry);
        values.put("Percentage", percentage);

        String[] args = new String[]{String.valueOf(date), String.valueOf(month),
                String.valueOf(year)};
        String[] column = {"Percentage"};

        /**Update is used instead of Insert, because the entry already exist**/
        systemQueryDatabase.update(USER_MEDICATION_CHOICE_TABLE, values, "Date=? AND Month=? " +
                "AND YEAR=?", args);
        Cursor cursor = systemQueryDatabase.query(USER_MEDICATION_CHOICE_TABLE, column, null, null,
                null, null, null);

        while (cursor.moveToNext()) {
         Log.d(TAG_DATABASE_HELPER, "Percentage:" + cursor.getDouble(0));
        }
        systemQueryDatabase.close();
    }

    /*If No Entry will be found it will enter in the database, so that it can be later updated.
     * Usage is in Day Fragment Activity **/
    public void insertOrUpdateMissedMedicationEntry(final int date, final int month,
                                                    final int year, final double percentage) {

        //1~12 valid months intervals for one year
        assert month >= 1 && month <= 12;
        //1~31 valid day intervals for one month
        assert date >= 1 && date <= 31;
        //2000~current year valid years intervals
        assert year >= 2000 && year <= Calendar.YEAR;

        SQLiteDatabase systemQueryDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(2);
        String choice = EMPTY_STRING, dateFormation = EMPTY_STRING;

        //Get the type of choice in SharedPreferences
        if (SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly",
                false)) {
            choice = "weekly";
        }
        else {
            choice = "daily";
        }

        //Scans and formats the date
        if (dayIsSingleDigit(date)) {
            dateFormation = EMPTY_STRING + year + "-" + month + "-" + date;
        }
        else {
            dateFormation = EMPTY_STRING + year + "-" + month + "-0" + date;
        }

        String []columns = {"Status"};
        String []selArgs = {EMPTY_STRING + date, EMPTY_STRING + month, EMPTY_STRING + year};

        Cursor cursor = systemQueryDatabase.query(USER_MEDICATION_CHOICE_TABLE, columns,
                "Date=? AND Month =? AND Year =?", selArgs, null, null, null, null);

        int statusQueried;
        boolean hasStatus = false;
        String lastStatus = EMPTY_STRING;

        //Runs through the database and saves the last status saved
        while (cursor.moveToNext()) {
            statusQueried = cursor.getColumnIndex("Status");
            lastStatus = cursor.getString(statusQueried);
            hasStatus = true;
        }

        Log.d(TAG_DATABASE_HELPER, EMPTY_STRING + year);

        if (!hasStatus && lastStatus.equalsIgnoreCase(EMPTY_STRING)) {
            contentValues.put("Drug", SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps." +
                    "malaria.drug", INT_ZERO));
            contentValues.put("Choice", choice);
            contentValues.put("Month", EMPTY_STRING + month);
            contentValues.put("Year", EMPTY_STRING + year);
            contentValues.put("Date", date);
            contentValues.put("Percentage", percentage);
            contentValues.put("Timestamp", dateFormation);

            systemQueryDatabase.insert(USER_MEDICATION_CHOICE_TABLE, "medicaton", contentValues);

            String []col = {"Date"};
            String []arg = {EMPTY_STRING + month, EMPTY_STRING + year};
            Cursor cursorNoHaveStatus = systemQueryDatabase.query(USER_MEDICATION_CHOICE_TABLE,
                    col, "Month =? AND Year =?", arg, null, null, "Date ASC");

            //Runs through the database and store the medication information that will be taken in the database
            while (cursor.moveToNext()) {
                int lim = cursorNoHaveStatus.getInt(0);

                for (int i = 1; i < lim; i++) {
                   if (dayIsSingleDigit(date + i)) {
                       dateFormation = EMPTY_STRING + year + "-" + month + "-" + (date + i);
                   }
                   else {
                       dateFormation = EMPTY_STRING + year + "-" + month + "-0" + (date + i);
                   }
                   contentValues.put("Drug", SharedPreferenceStore.mPrefsStore.getInt("com." +
                           "peacecorps.malaria.drug", INT_ZERO));
                   contentValues.put("Choice", choice);
                   contentValues.put("Month", EMPTY_STRING + month);
                   contentValues.put("Year", EMPTY_STRING + year);
                   contentValues.put("Date", date + i);
                   contentValues.put("Percentage", percentage);
                   contentValues.put("Timestamp", dateFormation);
                   systemQueryDatabase.insert(USER_MEDICATION_CHOICE_TABLE, "medicaton",
                           contentValues);
               }
            }
        }
        systemQueryDatabase.close();
    }

    /*Is Entered is Used for Getting the Style of Each Calendar Grid Cell According to the Medication Status Taken or Not Taken*/
    public int isEntered(final int date, final int month, final int year) {
        //1~12 valid months intervals for one year
        assert month >= 1 && month <= 12;
        //1~31 valid day intervals for one month
        assert date >= 1 && date <= 31;
        //2000~current year valid years intervals
        assert year >= 2000 && year <= Calendar.YEAR;

        SQLiteDatabase sqDB = getWritableDatabase();
        String column[] = {"Status"};
        String args[] = {EMPTY_STRING + date, EMPTY_STRING + month, EMPTY_STRING + year};
        Cursor cursor = sqDB.query(USER_MEDICATION_CHOICE_TABLE, column,
                "Date=? AND Month =? AND Year =?", args, null, null, null, null);
        int idx = INT_ZERO;
        String status = EMPTY_STRING;
        //Runs through the database and checks whether the user has already taken the medicine
        while (cursor.moveToNext()) {
            idx = cursor.getColumnIndex("Status");
            status = cursor.getString(idx);
            if (status != null) {
                if (status.equalsIgnoreCase("yes"))
                    return 0;
                else if (status.equalsIgnoreCase("no"))
                    return 1;
            }
        }
        sqDB.close();
        return 2;
    }

    /**Getting the oldest registered entry of Pill**/
    public long getFirstTime() {
        SQLiteDatabase sqDB = getWritableDatabase();
        String column[] = {"Timestamp"};
        Cursor cursor = sqDB.query(USER_MEDICATION_CHOICE_TABLE, column, null, null,
                null, null, "Timestamp ASC LIMIT 1");

        int idx = INT_ZERO;
        String selectedDate = EMPTY_STRING;
        long firstRunTime = INT_ZERO;
        //Runs through the database and checks the last time you used the medication
        while (cursor.moveToNext()) {
            idx = cursor.getColumnIndex("Timestamp");
            selectedDate = cursor.getString(idx);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date compareDate = Calendar.getInstance().getTime();
            try {
                compareDate   = sdf.parse(selectedDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG_DATABASE_HELPER, "First Time: " + selectedDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(compareDate);
            firstRunTime = cal.getTimeInMillis();
        }
        sqDB.close();
        return firstRunTime;
    }

    /**Getting the Status of Each Day, like whether the Medicine was taken or not.
     * Usages in Alert Dialog Fragment for geting the status of pill for setting up Reminder
     * Usages in Day Fragment Activity for getting the previous status of day before updating it as not taken. **/
    public String getStatus(final int date, final int month, final int year){
        //1~12 valid months intervals for one year
        assert month >= 1 && month <= 12;
        //1~31 valid day intervals for one month
        assert date >= 1 && date <= 31;
        //2000~current year valid years intervals
        assert year >= 2000 && year <= Calendar.YEAR;

        SQLiteDatabase sqDB = getWritableDatabase();
        String []column = {"Status"};
        String []selArgs = {EMPTY_STRING + date, EMPTY_STRING + month, EMPTY_STRING + year};

        Cursor cursor= sqDB.query(USER_MEDICATION_CHOICE_TABLE, column,
                "Date =? AND Month =? AND Year =?", selArgs, null, null, null, null);

        while (cursor.moveToNext()) {
            return cursor.getString(0);
        }

        return "miss";
    }


    /**From the Last Time Pill was Taken it Calculates the maximum days in a row medication was taken
     * Need at Home Screen, First Analytic Scrren, Second Analytic Scrren, Day Fragment Screen
     * Main Activity for updating the dosesInArow as it changes according to the status we enter.**/
    public int getDosesInaRowDaily() {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column = {"Status", "Timestamp", "Date", "Month", "Year", "Choice"};

        Cursor cursor= sqDB.query(USER_MEDICATION_CHOICE_TABLE, column, null,
                null, null, null, "Timestamp DESC");

        int dosesInaRow = INT_ZERO, prevDate = INT_ZERO, currDate = INT_ZERO,
                currDateMonth = INT_ZERO, prevDateMonth = INT_ZERO, prevDateYear = INT_ZERO,
                currDateYear = INT_ZERO;
        String ts = EMPTY_STRING
                ;
        /**One Iteration is done before entering the while loop for updating the previous and current dates**/
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor != null) {
                try {
                    ts = cursor.getString(cursor.getColumnIndex("Timestamp"));
                    currDate = cursor.getInt(2);
                    Log.d(TAG_DATABASE_HELPER, "curr dates 1->" + ts);
                } catch (Exception e) {
                    return 0;
                }
                if (cursor.getString(0).compareTo("yes") == 0) {
                    prevDate = cursor.getInt(2);
                    prevDateMonth = cursor.getInt(3);
                    if (Math.abs(currDate - prevDate) <= 1) {
                        dosesInaRow++;
                    } else {
                        //Nothing to do
                    }
                } else {
                    //Nothing to do
                }

                /**Since Previous and Current Date our Updated,
                 * Now backwards scan is done till we receive consecutive previous and current dates **/
                while (cursor != null && cursor.moveToNext()) {
                    currDate = cursor.getInt(2);
                    currDateMonth = cursor.getInt(3);
                    currDateYear = cursor.getInt(4);

                    ts = cursor.getString(cursor.getColumnIndex("Timestamp"));
                    Log.d(TAG_DATABASE_HELPER, "curr dates ->" + ts);

                    int parameter = Math.abs(currDate - prevDate);
                    if ((cursor.getString(0)) != null) {
                        if (currDateMonth == prevDateMonth) {
                            if (cursor.getString(0).compareTo("yes") == 0 && parameter == 1) {
                                dosesInaRow++;
                            }
                            else {
                                break;
                            }
                        } else {
                            parameter = Math.abs(currDate - prevDate) %
                                    (getNumberDaysInMonth(currDateMonth, currDateYear) - 1);
                            if (cursor.getString(0).compareTo("yes") == 0 && parameter <= 1) {
                                dosesInaRow++;
                            }
                            else {
                                break;
                            }
                        }
                    } else {
                        //Nothing to do
                    }
                    Log.d(TAG_DATABASE_HELPER, "Doses in Row->" + dosesInaRow);

                    prevDate = currDate;
                    prevDateMonth = currDateMonth;
                }
            } else {
                //Nothing to do
            }
        } else {
            //Nothing to do
        }

        Log.d(TAG_DATABASE_HELPER, "Doses in Row->" + dosesInaRow);
        sqDB.close();
        return dosesInaRow;
    }

    /**Method to give no. of days in month. */
    private int getNumberDaysInMonth(final int month, final int year) {
        //1~12 valid months intervals for one year
        assert month >= 1 && month <= 12;
        //2000~current year valid years intervals
        assert year >= 2000 && year <= Calendar.YEAR;

        if (isLeapYear(year)) {
            return daysOfMonthLeap[month];
        } else {
            return daysOfMonth[month];
        }
    }

    /**Check whether it is a leap layer**/
    private static boolean isLeapYear(final int year) {
        //2000~current year valid years intervals
        assert year >= 2000 && year <= Calendar.YEAR;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);

        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }

    /**From the Last Time Pill was Taken it Calculates the maximum weeks in a row medication was taken
     * Need at Home Screen, First Analytic Scrren, Second Analytic Scrren, Day Fragment Screen
     * Main Activity for updating the dosesInArow as it changes according to the status we enter.**/
    public int getDosesInaRowWeekly() {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column = {"Status", "Timestamp", "Date", "Month", "Year"};

        Cursor cursor= sqDB.query(USER_MEDICATION_CHOICE_TABLE, column, null,
                null, null, null, "Timestamp DESC");

        int dosesInaRow = 1, aMonth = INT_ZERO, pMonth = INT_ZERO;
        Date ado, pdo;
        int pPara = INT_ZERO;
        long aPara = INT_ZERO;
        int numDays = INT_ZERO;
        String ats = EMPTY_STRING, pts = EMPTY_STRING;
        if(cursor!=null) {
            cursor.moveToNext();
            if(cursor!=null) {

                try {
                    ats = cursor.getString(1);
                }
                catch (CursorIndexOutOfBoundsException e)
                {
                    return 0;
                }

                aMonth = cursor.getInt(3) + 1;
                ats = getHumanDateFormat(ats, aMonth);
                ado = getDateObject(ats);
                while (cursor.moveToNext()) {
                    pts = cursor.getString(1);
                    pMonth = cursor.getInt(3) + 1;
                    pts = getHumanDateFormat(pts, pMonth);
                    pdo = getDateObject(pts);
                    numDays = getDayofWeek(pdo);
                    pPara = 7 - numDays + 7;
                    aPara = getNumberOfDays(pdo, ado);
                    if (aPara <= pPara)
                        dosesInaRow++;
                    else
                        break;
                    ats = pts;
                    ado = pdo;
                }
            }
        }
        return dosesInaRow;


    }

    /*Getting the Date Object from the String**/
    private Date getDateObject(String s)
    {
        Date dobj=null;

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        try {
            dobj= sdf.parse(s);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dobj;
    }

    /*Getting the Day of Week from the String**/
    private int getDayofWeek(Date d)
    {
        Calendar cal=Calendar.getInstance();
        cal.setTime(d);
        int day=cal.get(Calendar.DAY_OF_WEEK);
        return day;
    }

    /*Getting no. of Days between two interval**/
    private long getNumberOfDays(Date d1,Date d2) {
        long interval = 0;
        Calendar c= Calendar.getInstance();
        c.setTime(d1);
        long ld1 = c.getTimeInMillis();
        c.setTime(d2);
        long ld2=c.getTimeInMillis();
        long oneDay = 1000 * 60 * 60 * 24;
        interval = (ld2-ld1) / oneDay;
        return interval;
    }

    /*Setting the Date Object to Human Readable Format**/
    private String getHumanDateFormat(String ats,int aMonth)
    {
        String aYear=ats.substring(0, 4);
        String aDate=ats.substring(Math.max(ats.length() - 2, 0));
        ats=aYear+"-"+aMonth+"-"+aDate;
        return ats;
    }

    public String getMediLastTakenTime()
    {
        SQLiteDatabase sqDB=getWritableDatabase();
        String [] column={"Date","Month","Year"};
        String recentDate="";
        Cursor cursor = sqDB.query(USER_MEDICATION_CHOICE_TABLE, column, null, null, null, null, "Timestamp DESC LIMIT 1");
        if(cursor!=null)
        { cursor.moveToNext();
            try
            {
                recentDate=cursor.getString(0)+"/"+cursor.getString(1);
            }
            catch (Exception e)
            {
                return "";
            }
        }      sqDB.close();
        return recentDate;
    }

    /*Deleting the Database*/
    public void resetDatabase()
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        sqDB.delete(USER_MEDICATION_CHOICE_TABLE,null,null);
        sqDB.delete(APP_SETTING_TABLE,null,null);
        sqDB.delete(LOCATION_TABLE,null,null);
        sqDB.delete(PACKING_TABLE,null,null);
        sqDB.close();
    }

    /**Inseting the location for maintaining Location History**/
    public void insertLocation(String location)
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        ContentValues cv =new ContentValues(2);
        int a=0,flag=0;
        cv.put("Location", location);

        String [] columns = {"Location","Times"};
        String [] selArgs = {""+location};

        Cursor cursor = sqDB.query(LOCATION_TABLE,columns,"Location = ?",selArgs,null,null,null);

        while (cursor.moveToNext())
        {
            a= cursor.getInt(1);
            a++;
            flag=1;
        }
        cv.put("Times", a);

        if(flag==1)
            sqDB.update(LOCATION_TABLE, cv, "Location= ?", selArgs);
        else
            sqDB.insert(LOCATION_TABLE, "location", cv);


    }

    /**Fetching the Location**/
    public Cursor getLocation()
    {

        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"_id","Location"};

        return sqDB.query(LOCATION_TABLE, column,
                null, null, null, null,
                KEY_ROW_ID + " asc ");
    }

    /**Inserting the Packing Item in DataBase when using Add Item Edit Text**/
    public void insertPackingItem(String pItem,int quantity, String status)
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        ContentValues cv =new ContentValues(2);
        int flag=0,q=0;
        cv.put("PackingItem",pItem);
        cv.put("Status",status);

        String [] columns = {"PackingItem","Quantity","Status"};
        String [] selArgs = {""+pItem};

        Cursor cursor = sqDB.query(PACKING_TABLE,columns,"PackingItem = ?",selArgs,null,null,null);

        while (cursor.moveToNext())
        {
           q= cursor.getInt(1);
            flag++;
            Log.d(TAG_DATABASE_HELPER,"Flag: "+flag);
        }


        if(flag==1) {

            cv.put("Quantity", quantity);
            sqDB.update(PACKING_TABLE, cv, "PackingItem= ?", selArgs);
        }
        else {
            cv.put("Quantity", quantity);
            sqDB.insert(PACKING_TABLE, "item", cv);
        }


    }

    /**Fetching the Packing Item to be taken**/
    public Cursor getPackingItemChecked()
    {

        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"_id","PackingItem","Quantity"};
        String []selArgs={"yes"};


        Cursor cursor = sqDB.query(PACKING_TABLE,column,"Status= ?",selArgs,null,null,KEY_ROW_ID+" asc ");

        return cursor;

    }

    /**Fetching the list of Packing Item from which one can be chosen**/
    public Cursor getPackingItem()
    {

        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"_id","PackingItem","Quantity"};
        return sqDB.query(PACKING_TABLE, column,
                null, null, null, null,
                KEY_ROW_ID + " asc ");

    }


    /**Refreshing the status of each packing item to its original state**/
    public void refreshPackingItemStatus()
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"_id","PackingItem"};
        String pItem="";
        String []selArgs={pItem};
        ContentValues cv = new ContentValues(2);
        cv.put("Status","no");
        Cursor cursor= sqDB.query(PACKING_TABLE, column,
                null, null, null, null,
                KEY_ROW_ID + " asc ");

        while (cursor.moveToNext())
        {
            try {
                selArgs[0]=cursor.getString(1);
                sqDB.update(PACKING_TABLE,cv,"PackingItem=?",selArgs);
            }
            catch (Exception e)
            {
                break;
            }



        }


    }


    /**Finding the No. of Drugs**/
    public int getCountTaken()
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"Status","Timestamp","Date","Month","Year","Choice"};
        Cursor cursor= sqDB.query(USER_MEDICATION_CHOICE_TABLE,column,null,null,null,null,"Timestamp ASC");
        int count=0;
        if(cursor!=null)
        {
            while (cursor.moveToNext())
            {
                try {
                    if (cursor.getString(0).equalsIgnoreCase("yes") == true) {
                        count++;
                        Log.d(TAG_DATABASE_HELPER,"Counter :"+count);
                    }
                }
                catch(NullPointerException npe)
                {
                    return 0;

                }
            }
        }
        sqDB.close();
        return count;



    }


    /**Finding the No. of weekly days between two dates for calculating Adherence**/
    public int getIntervalWeekly(Date s, Date e, int weekday)
    {
        Calendar startCal;
        Calendar endCal;
        startCal = Calendar.getInstance();
        startCal.setTime(s);
        endCal = Calendar.getInstance();
        endCal.setTime(e);
        int medDays = 0,flag=0;
        //If working dates are same,then checking what is the day on that dates.
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            if (startCal.get(Calendar.DAY_OF_WEEK) == weekday)
            {
                ++medDays;
                return medDays;
            }
        }
        /*If start dates is coming after end dates, Then shuffling Dates and storing dates
        by incrementing upto end dates in do-while part.*/
        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(e);
            endCal.setTime(s);
        }

        do {

            if (startCal.get(Calendar.DAY_OF_WEEK)==weekday) {
                ++medDays;
            }
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        } while (startCal.getTimeInMillis() <= endCal.getTimeInMillis());

        if(startCal.get(Calendar.DAY_OF_WEEK)==endCal.get(Calendar.DAY_OF_WEEK) && (startCal.get(Calendar.DAY_OF_WEEK)==weekday))
            ++medDays;

        return medDays;
    }

    /**Finding the No. of days between two dates for calculating adherence of daily drugs**/
    public int getIntervalDaily(Date s,Date e)
    {
        long sLong=s.getTime();
        long eLong=e.getTime();

        long oneDay=24*60*60*1000;

        long interval=(eLong-sLong)/oneDay;

        int interv=(int)interval+1;

        return interv;

    }

    /**Finding the Drugs between two dates for updaing Adherence in Day Fragment Activity of any selected dates**/
    public int getCountTakenBetween(Date s,Date e)
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"Status","Timestamp","Date","Month","Year","Choice"};
        Cursor cursor= sqDB.query(USER_MEDICATION_CHOICE_TABLE,column,null,null,null,null,"Timestamp ASC");
        int count=0;

        if(cursor!=null)
        {
            while (cursor.moveToNext())
            {
                try {

                    String d= cursor.getString(1);
                    Log.d(TAG_DATABASE_HELPER,"Curr Time:"+d);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date curr= Calendar.getInstance().getTime();
                    try{

                        curr=sdf.parse(d);
                    }
                    catch (Exception ex)
                    {
                    }
                    Log.d(TAG_DATABASE_HELPER,e.toString());
                    Log.d(TAG_DATABASE_HELPER,s.toString());
                    long currt=curr.getTime();
                    long endt=e.getTime();

                    Calendar cal =Calendar.getInstance();
                    cal.setTime(s);
                    cal.add(Calendar.MONTH, 1);
                    s=cal.getTime();
                    long strt=s.getTime();

                    Log.d(TAG_DATABASE_HELPER,"Current Long:"+currt);
                    Log.d(TAG_DATABASE_HELPER,"End Long:"+endt);
                    Log.d(TAG_DATABASE_HELPER,"Start Long:"+strt);

                    if (cursor.getString(0).equalsIgnoreCase("yes") == true) {

                        if(currt>=strt && currt<=endt)
                            count++;
                        else if(strt==endt)
                        {
                            count++;
                        }
                    }
                }
                catch(NullPointerException npe)
                {
                    return 0;

                }
            }
        }
        sqDB.close();
        return count;
    }

    private static final int MAX_NUMBER_WITH_ONE_DIGIT = 9;

    private boolean dayIsSingleDigit(final int dayOfMonth) {

        boolean dayIsSingleDigit = false;

        if (dayOfMonth <= MAX_NUMBER_WITH_ONE_DIGIT) {
            dayIsSingleDigit = true;
        } else {
            dayIsSingleDigit = false;
        }
        return dayIsSingleDigit;
    }
}

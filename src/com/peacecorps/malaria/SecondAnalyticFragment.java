
package com.peacecorps.malaria;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.util.Calendar;

/**Second Analytic Fragment
 * It shows the Progress Bars and Graph
 * **/
public class SecondAnalyticFragment extends Fragment {

    private TextView firstMonthProgressLabel, secondMonthProgressLabel, thirdMonthProgressLabel, fourthMonthProgressLabel;
    private TextView firstMonthProgressPercent, secondMonthProgressPercent, thirdMonthProgressPercent, fourthMonthProgressPercent;

    // These bars represent the progress of drug taking over months, in percent [0, 100]
    private ProgressBar firstMonthProgressBar, secondMonthProgressBar, thirdMonthProgressBar, fourthMonthProgressBar;

    private Button mSettingsButton;
    private View rootView;
    public final static String MONTH_REQ = "com.peacecorps.malaria.secondanalyticfragment.MONTHREQ";

    static SharedPreferenceStore mSharedPreferenceStore;


    private static final String DATABASE_NAME = "MalariaDatabase";
    private static final String userMedicationChoiceTable = "userSettings";
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};
    private String TAGSAF = "SecondAnalyticFragment";

    GraphViewSeries drugGraphSeries;
    private GraphViewData[] graphViewData;
    private int date;
    private String choice;
    private Dialog dialog = null;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Declaring the Views
        rootView = inflater.inflate(R.layout.fragment_second_analytic_screen,
                null);

        mSettingsButton = (Button) rootView.findViewById(R.id.fragment_second_screen_settings_button);

        firstMonthProgressLabel = (TextView) rootView.findViewById(R.id.firstMonthProgressLabel);
        secondMonthProgressLabel = (TextView) rootView.findViewById(R.id.secondMonthProgressLabel);
        thirdMonthProgressLabel = (TextView) rootView.findViewById(R.id.thirdMonthProgressLabel);
        fourthMonthProgressLabel = (TextView) rootView.findViewById(R.id.fourthMonthProgressLabel);

        firstMonthProgressPercent = (TextView) rootView.findViewById(R.id.firstMonthProgressPercent);
        secondMonthProgressPercent = (TextView) rootView.findViewById(R.id.secondMonthProgressPercent);
        thirdMonthProgressPercent = (TextView) rootView.findViewById(R.id.thirdMonthProgressPercent);
        fourthMonthProgressPercent = (TextView) rootView.findViewById(R.id.fourthMonthProgressPercent);

        firstMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.firstMonthProgressBar);
        secondMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.secondMonthProgressBar);
        thirdMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.thirdMonthProgressBar);
        fourthMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.fourthMonthProgressBar);

        //Calendar necessary to take a current month
        Calendar cal = Calendar.getInstance();

        date = Calendar.getInstance().get(Calendar.MONTH);
        //checking choic of pill whether weekly or daily
        if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                "com.peacecorps.malaria.isWeekly", false)) {
            choice = "weekly";
        } else {
            choice = "daily";
        }

        updateUI(choice, date);


        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI(choice, date);

    }

    //Auxiliar day using in below methods
    int mdate;
    //Auxiliar year using in below methods
    int myear;

    //finding month from its integer
    public String getMonth(int date) {
        assert date >= 0 && date <= 31;

        String month[] = getResources().getStringArray(R.array.month);

        /* After decrementing month date, it may become negative, so the month will be on the previous year.
         * Now month and year must be updated accordingly.
         */

        /* TO DO: change dates to positive numbers. */
        if (date == -1) {
            date = 11;
            myear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else if (date == -2) {
            date = 10;
            myear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else if (date == -3) {
            date = 9;
            myear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else {
            myear = Calendar.getInstance().get(Calendar.YEAR);
            mdate = date;
        }
        return month[date];
    }
    /*Opening Dialog on Clicking Gear Icon*/
    public void addButtonListeners() {
        mSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addDialog();

            }
        });
        /*On Clicking the Progress Bars Opens Calendar of that Specific Month*/
        firstMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = firstMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                startActivity(intent);
                Toast.makeText(getActivity(), "First progress", Toast.LENGTH_SHORT).show();
            }
        });

        secondMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = secondMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                startActivity(intent);
                Toast.makeText(getActivity(), "Second progress", Toast.LENGTH_SHORT).show();
            }
        });

        thirdMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = thirdMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                startActivity(intent);
                Toast.makeText(getActivity(), "Third progress", Toast.LENGTH_SHORT).show();
            }
        });

        fourthMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = fourthMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                startActivity(intent);
                Toast.makeText(getActivity(), "Fourth progress", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*Fetching the Details and Settings from Shared Preferences*/
    public void getSharedPreferences() {

        mSharedPreferenceStore.mPrefsStore = getActivity()
                .getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
                        Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }

    /*Finding No. of Days in Month*/
    public int getNumberOfDaysInMonth(int month) {
        final int JANUARY = 0;
        final int DECEMBER = 12;

        assert (month >= JANUARY && month <= DECEMBER) : ("Month is not between January (1) and December (12)");
        return daysOfMonth[month];
    }

    /*Updating the Progress Bars
    * On the basis of drugs taken or not
    * Also on the basis of status of each day modified later in the calendar
    * */
    public void updateProgressBar(String choice, int date) {
        assert date >= 0 && date <= 31;

        DatabaseSQLiteHelper sqLH = new DatabaseSQLiteHelper(getActivity());
        Typeface cf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/garreg.ttf");
        firstMonthProgressLabel.setText(getMonth(date - 3));
        firstMonthProgressLabel.setTypeface(cf);
        int progress = sqLH.getData(mdate, myear, choice);
        float progressp = 0;
        /* If the user chooses to take notifications daily progress will
            be measured according to the days*/
        if (choice.equalsIgnoreCase("daily")) {
            progressp=(float) progress / getNumberOfDaysInMonth(mdate) * 100;
        } else {
            progressp=progress * 25;
        }

        if(progressp>=50)
        {
            firstMonthProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.saf_progress_bar_green));
            firstMonthProgressBar.setBackground(getResources().getDrawable(R.drawable.progress_bg_green));

        }
        firstMonthProgressBar.setProgress((int) progressp);
        firstMonthProgressPercent.setText("" + (int) progressp + "%");
        firstMonthProgressPercent.setTypeface(cf);

        secondMonthProgressLabel.setText(getMonth(date - 2));
        secondMonthProgressLabel.setTypeface(cf);
        progress = sqLH.getData(mdate, myear, choice);

        /* If the user chooses to take notifications daily progress will
            be measured according to the days*/
        if (choice.equalsIgnoreCase("daily")) {

            final float progressFloat = (float) progress;
            final float numberOfDaysInMonthFloat = (float) getNumberOfDaysInMonth(mdate);
            final float PERCENTAGE = 100.0F;

            // Float division is required.
            progressp = (progressFloat / numberOfDaysInMonthFloat) * PERCENTAGE;
        }
        else {
            progressp = (float) progress * 25.0F;
        }

        if(progressp >= 50.0)
        {
            secondMonthProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.saf_progress_bar_green));
            secondMonthProgressBar.setBackground(getResources().getDrawable(R.drawable.progress_bg_green));

        }
        secondMonthProgressBar.setProgress((int) progressp);
        secondMonthProgressPercent.setText("" + (int) progressp + "%");
        secondMonthProgressPercent.setTypeface(cf);

        thirdMonthProgressLabel.setText(getMonth(date - 1));
        thirdMonthProgressLabel.setTypeface(cf);
        progress = sqLH.getData(mdate, myear, choice);

        /* If the user chooses to take notifications daily progress will
            be measured according to the days*/
        if (choice.equalsIgnoreCase("daily"))
            progressp = (float) progress / getNumberOfDaysInMonth(mdate) * 100;
        else
            progressp = progress * 25;

        if(progressp>=50) {
            thirdMonthProgressBar.setBackground(getResources().getDrawable(R.drawable.progress_bg_green));
            thirdMonthProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.saf_progress_bar_green));
        }
            thirdMonthProgressBar.setProgress((int) progressp);
            thirdMonthProgressPercent.setText("" + (int) progressp + "%");
        thirdMonthProgressPercent.setTypeface(cf);

        fourthMonthProgressLabel.setText(getMonth(date));
        fourthMonthProgressLabel.setTypeface(cf);
        progress = sqLH.getData(mdate, myear, choice);
        Log.d(TAGSAF, "Query Return: " + progress);

        /* If the user chooses to take notifications daily progress will
            be measured according to the days*/
        if (choice.equalsIgnoreCase("daily"))
            progressp = (float) progress / getNumberOfDaysInMonth(mdate) * 100;
        else
            progressp = progress * 25;
        Log.d(TAGSAF, "" + getNumberOfDaysInMonth(mdate));
        Log.d(TAGSAF, "" + progress);
        Log.d(TAGSAF, "" + progressp);

        if(progressp>=50) {
            fourthMonthProgressBar.setBackground(getResources().getDrawable(R.drawable.progress_bg_green));
            fourthMonthProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.saf_progress_bar_green));
        }
        fourthMonthProgressBar.setProgress((int) progressp);
        fourthMonthProgressPercent.setText("" + (int) progressp + "%");
        fourthMonthProgressPercent.setTypeface(cf);
    }

    /**Update UI is called on resume to Update the Graph and Progress Bars**/
    public void updateUI(String choice, int date) {
        assert date >= 0 && date <= 31;

        updateProgressBar(choice, date);
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
        if (sqLite.getDosesInaRowDaily()!=0)
        {
            SetupAndShowGraph();
        }
        getSharedPreferences();
        addButtonListeners();

    }
    /**Setting Up Graph**/
    public void SetupAndShowGraph() {

        //Chart to be shown
        GraphViewData graphViewData[] = new GraphViewData[DatabaseSQLiteHelper.dates.size()];
        //Acceptable label vector in chart
        String verLabels[] = {"100%", "50%", "25%", "0%"};
        //adding data
        for (int index=0; index < DatabaseSQLiteHelper.percentages.size(); index++) {

            graphViewData[index] = new GraphViewData(DatabaseSQLiteHelper.dates.get(index), Double.parseDouble("" + DatabaseSQLiteHelper.percentages.get(index)));
        }
        drugGraphSeries = new GraphViewSeries(graphViewData);

        GraphView lineGraphView = new LineGraphView(getActivity(), "");
         //styling graph
        lineGraphView.getGraphViewStyle().setGridColor(getResources().getColor(R.color.lightest_brown));
        lineGraphView.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.BOTH);
        lineGraphView.getGraphViewStyle().setHorizontalLabelsColor(getResources().getColor(R.color.golden_brown));
        lineGraphView.getGraphViewStyle().setVerticalLabelsColor(getResources().getColor(R.color.golden_brown));
        lineGraphView.setBackground(getResources().getDrawable(R.drawable.graph_bg));
        lineGraphView.getGraphViewStyle().setTextSize(8.0F);
        lineGraphView.setVerticalLabels(verLabels);

        lineGraphView.setTitle("Adherence Rate vs Day");


        lineGraphView.setScrollable(true);
        lineGraphView.setScalable(true);

        lineGraphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isXAxis) {


                return null;
            }
        });

        ((LineGraphView) lineGraphView).setDrawBackground(true);
        ((LineGraphView) lineGraphView).setDrawDataPoints(true);
        ((LineGraphView) lineGraphView).setBackgroundColor(getResources().getColor(R.color.light_blue));
         float r=(float)0.20;
         ((LineGraphView) lineGraphView).setDataPointsRadius(r);
        //plotting data
        lineGraphView.addSeries(drugGraphSeries);

        //showing graph
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.graphView);
        linearLayout.addView(lineGraphView);


    }
    /**Reset Dailog**/
    public void addDialog()
    {    //opening the reset Dialog
        dialog = new Dialog(this.getActivity(),android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setContentView(R.layout.resetdata_dialog);
        dialog.setTitle("Reset Data");

        final RadioGroup btnRadGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupReset);
        Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOKReset);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedId = btnRadGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton btnRadButton = (RadioButton) dialog.findViewById(selectedId);

                String ch = btnRadButton.getText().toString();

                if (ch.equalsIgnoreCase("yes")) {
                    DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
                    sqLite.resetDatabase();
                    mSharedPreferenceStore.mEditor.clear().commit();
                    startActivity(new Intent(getActivity(),
                            UserMedicineSettingsFragmentActivity.class));
                    getActivity().finish();
                } else {
                    dialog.dismiss();
                }

            }
        });

        Button btnCancel = (Button) dialog.findViewById(R.id.dialogButtonCancelReset);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

}

package com.peacecorps.malaria;

/**
 * Created by Ankita on 7/4/2015.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Exchanger;

public class DropDownListAdapter extends BaseAdapter {


    private ArrayList<String> mListItems;
    private LayoutInflater mInflater;
    private TextView mSelectedItems;
    private static int selectedCount = 0;
    private static String firstSelected = "";
    private ViewHolder holder;
    private static String selected = "";	//shortened selected values representation


    private String TAG_DDLA = "DropDownListAdapter";

    public static String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        DropDownListAdapter.selected = selected;
    }

    public DropDownListAdapter(Context context, ArrayList<String> items,
                               TextView tv) {
        mListItems = new ArrayList<String>();
        mListItems.addAll(items);
        mInflater = LayoutInflater.from(context);
        mSelectedItems = tv;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.trip_item_dropdown_popup, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.itemSelectOption);
            holder.chkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(mListItems.get(position));

        final int position1 = position;

        //whenever the checkbox is clicked the selected values textview is updated with new selected values
        holder.chkbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setText(position1);
            }
        });

        try {
            final boolean isPositionSelected = TripIndicatorFragmentActivity.getCheckSelectedAt(position);

            if (isPositionSelected)
                holder.chkbox.setChecked(true);
            else {
                holder.chkbox.setChecked(false);
            }
        } catch(IllegalArgumentException illegalArgumentException) {
            holder.chkbox.setChecked(false);
        }
        return convertView;
    }


    /*
     * Function which updates the selected values display and information(checkSelected[])
     * */
    private void setText(int position1){
        assert position1 >= 0;

        final boolean isCheckSelectedAtPosition = TripIndicatorFragmentActivity.getCheckSelectedAt(position1);
        boolean nextCheckedValue = false;
        try {
            if (!isCheckSelectedAtPosition) {
                nextCheckedValue = true;
                selectedCount++;
            } else {
                nextCheckedValue = false;
                selectedCount--;
            }
            TripIndicatorFragmentActivity.setCheckSelectedAt(position1, nextCheckedValue);
        } catch(IllegalArgumentException illegalArgumentException) {
        }

        final int checkSelectedLength = TripIndicatorFragmentActivity.getCheckSelectedLength();
        if (selectedCount == 0) {
            mSelectedItems.setText(R.string.trip_select_string);
        } else if (selectedCount == 1) {
            try {
                for (int i = 0; i < checkSelectedLength; i++) {
                    final boolean selected = TripIndicatorFragmentActivity.getCheckSelectedAt(i);
                    if (selected == true) {
                        firstSelected = mListItems.get(i);
                        break;
                    }
                }
                mSelectedItems.setText(firstSelected);
                setSelected(firstSelected);
            } catch(IllegalArgumentException illegalArgumentException) {
                Log.d(TAG_DDLA, "Invalid checkSelected length");
            }
        } else if (selectedCount > 1) {
            try {
                for (int i = 0; i < checkSelectedLength; i++) {
                    final boolean selected = TripIndicatorFragmentActivity.getCheckSelectedAt(i);
                    if (selected == true) {
                        firstSelected = mListItems.get(i);
                        break;
                    }
                }

                final String selectedItemsMessage = firstSelected + " & " + (selectedCount - 1) + " more";

                mSelectedItems.setText(selectedItemsMessage);
                setSelected(selectedItemsMessage);
            }
            catch(IllegalArgumentException illegalArgumentException) {
                Log.d(TAG_DDLA, "Invalid checkSelected length");
            }
        }
    }

    private class ViewHolder {
        TextView tv;
        CheckBox chkbox;
    }





}

package unimelb.steven.fitnessapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import unimelb.steven.fitnessapp.R;
import unimelb.steven.fitnessapp.models.Calories2;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> expandableListTitle;
    private ArrayList<String> expandableListCalories;
    private HashMap<String, ArrayList<Calories2>> expandableListDetail;

    public CustomExpandableListAdapter(Context context, ArrayList<String> expandableListTitle,
                                       HashMap<String, ArrayList<Calories2>> expandableListDetail,
                                       ArrayList<String> expandableListCalories) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.expandableListCalories = expandableListCalories;

        //notifyDataSetChanged();
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Log.i("Calories","getchildview");


        //final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) this.context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.list_calories2_row,parent,false);
            convertView = LayoutInflater.from(context).inflate(R.layout.list_calories2_row, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.activity = (TextView) convertView.findViewById(R.id.tv_activity);
            holder.date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.calories = (TextView) convertView.findViewById(R.id.tv_calories);
            holder.distance = (TextView) convertView.findViewById(R.id.tv_distance);
            holder.steps = (TextView) convertView.findViewById(R.id.tv_steps);

            convertView.setTag(holder);
        }


        ViewHolder holder = (ViewHolder) convertView.getTag();

        Calories2 item = (Calories2) getChild(listPosition, expandedListPosition);

        holder.date.setText(item.getDate());
        holder.activity.setText(item.getActivity());
        holder.calories.setText(item.getCalories() + " kcal");

        if (item.getActivity().equals("Cycling")){
            holder.distance.setText(item.getDistance() + " m");
            holder.steps.setText(item.getSteps() + " pedals");
        }else if (item.getActivity().equals("Upstairs") || item.getActivity().equals("Downstairs")){
            holder.distance.setText(String.valueOf(Math.round(Double.parseDouble(item.getDistance())/3)) + " floors");
            holder.steps.setText(item.getSteps() + " steps");
        }else{
            holder.distance.setText(item.getDistance() + " m");
            holder.steps.setText(item.getSteps() + " steps");
        }






//        TextView expandedListTextView = (TextView) convertView
//                .findViewById(R.id.tv_calories);
//        expandedListTextView.setText(expandedListText);
//


        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        Log.i("Calories","getChildrenCount");
//        Log.i("Calories",String.valueOf(listPosition));
//        Log.i("Calories",String.valueOf(this.expandableListTitle.get(listPosition)));

        String a = this.expandableListTitle.get(listPosition);

        Log.i("Calories",a);

        //Log.i(TAG,expandableListDetail.get(titles.get(i)).get(0).getActivity());


//        Log.i("Calories",String.valueOf(this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
//                .size()));

        Log.i("Calories", String.valueOf(expandableListDetail.size()));

        ArrayList<Calories2> b = expandableListDetail.get(a);

        Log.i("Calories", String.valueOf(b.size()));

        return this.expandableListDetail.get(a)
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {


        Log.i("Calories","getGroupView");

        String listTitle = (String) getGroup(listPosition);
        String listCalories = expandableListCalories.get(listPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        TextView listCaloriesTextView = (TextView) convertView
                .findViewById(R.id.listTotalCalories);
        listCaloriesTextView.setText(listCalories + " kcal");

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }



    static class ViewHolder {
        TextView activity;
        TextView date;
        TextView calories;
        TextView distance;
        TextView steps;
    }


}

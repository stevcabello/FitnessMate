package unimelb.steven2.fitnessapp.adapters;


import android.content.Context;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;
import unimelb.steven2.fitnessapp.R;
import unimelb.steven2.fitnessapp.models.Calories;
import unimelb.steven2.fitnessapp.models.Calories2;

public class Calories2Adapter extends ArrayAdapter<Calories> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Calories2> data = new ArrayList();

    public Calories2Adapter(Context context, int resource, ArrayList data) {
        super(context, resource,data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((FragmentActivity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.activity = (TextView) row.findViewById(R.id.tv_activity);
            holder.date = (TextView) row.findViewById(R.id.tv_date);
            holder.calories = (TextView) row.findViewById(R.id.tv_calories);
            holder.distance = (TextView) row.findViewById(R.id.tv_distance);
            holder.steps = (TextView) row.findViewById(R.id.tv_steps);


            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Calories2 item = data.get(position);

        Log.i("CaloriesAdapter",item.getActivity());

        holder.activity.setText(item.getActivity());
        holder.date.setText(item.getDate());
        holder.distance.setText(item.getDistance() + " m");
        holder.steps.setText(item.getSteps() + " steps");
        holder.calories.setText(item.getCalories() + " kcal");



        return row;
    }

    static class ViewHolder {
        TextView activity;
        TextView date;
        TextView calories;
        TextView distance;
        TextView steps;
    }

}

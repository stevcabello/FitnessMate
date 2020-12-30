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

public class CaloriesAdapter extends ArrayAdapter<Calories> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Calories> data = new ArrayList();

    public CaloriesAdapter(Context context, int resource, ArrayList data) {
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
            holder.duration = (TextView) row.findViewById(R.id.tv_duration);
            holder.distance = (TextView) row.findViewById(R.id.tv_distance);
            holder.speed = (TextView) row.findViewById(R.id.tv_speed);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Calories item = data.get(position);

        Log.i("CaloriesAdapter",item.getActivity());

        holder.activity.setText(item.getActivity());
        holder.date.setText(item.getDate());
        holder.duration.setText(item.getDuration() + " min");
        holder.distance.setText(item.getDistance() + " m");
        holder.speed.setText(item.getSpeed() + " m/min");
        holder.calories.setText(item.getCalories() + "Kcal");



        return row;
    }

    static class ViewHolder {
        TextView activity;
        TextView date;
        TextView calories;
        TextView duration;
        TextView distance;
        TextView speed;
    }

}

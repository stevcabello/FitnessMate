package unimelb.steven2.fitnessapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import unimelb.steven2.fitnessapp.R;
import unimelb.steven2.fitnessapp.models.Calories2;

public class ExpandableListAdapter extends BaseExpandableListAdapter {


	
	/*
	 * data
	 */
	private Context context = null;
	ArrayList<Group> groups = new ArrayList<Group>();
	
	public ExpandableListAdapter(Context context){
		this.context = context;
	}
	

	
	/*
	 * (non-Javadoc)
	 * @see android.widget.BaseExpandableListAdapter#getChildTypeCount()
	 */
	@Override
	public int getChildTypeCount() {
		//Past and Future Travel Plans
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).travelItems.get(childPosition);
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {
		
		//get the type of the group this child belongs
		View view = convertView;


			if(view == null){
				view = LayoutInflater.from(context).inflate(R.layout.list_calories2_row, parent, false);

				FutureTravelViewHolder holder = new FutureTravelViewHolder();
				holder.title = (TextView) view.findViewById(R.id.tv_activity);
				holder.departure = (TextView) view.findViewById(R.id.tv_distance);
				holder.destination = (TextView) view.findViewById(R.id.tv_calories);
				holder.date = (TextView) view.findViewById(R.id.tv_date);
				holder.time = (TextView) view.findViewById(R.id.tv_steps);

				view.setTag(holder);
			}

			FutureTravelViewHolder holder = (FutureTravelViewHolder) view.getTag();

			Calories2 currentItem = (Calories2) getChild(groupPosition, childPosition);

			holder.title.setText(currentItem.getActivity());
			holder.departure.setText(currentItem.getDistance()+ " m");
			holder.time.setText(currentItem.getSteps()+ " steps");
			holder.date.setText(currentItem.getDate());
			holder.destination.setText(currentItem.getCalories() + " kcal");

		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).travelItems.size();
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		return groups.size();
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {

		View view = convertView;
		TextView text = null;
		ImageView image = null;

		if(view == null){
			view = LayoutInflater.from(context).inflate(R.layout.expandable_list_group_view, parent, false);
		}

		text = (TextView) view.findViewById(R.id.groupHeader);
		image = (ImageView) view.findViewById(R.id.expandableIcon);

		StringBuilder title = new StringBuilder();
		if(groupPosition == 0){
			title.append(context.getString(R.string.future_travel_list_header));
		} else {
			title.append(context.getString(R.string.past_travel_list_header));
		}


		text.setText(title.toString());



		int imageResourceId = isExpanded ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float;
		image.setImageResource(imageResourceId);



		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/*
	 * setup travel plans and past trips into groups
	 */
	public void setupTrips(ArrayList<Calories2> futurePlans){
		groups.clear();

		Group g1 = new Group();
		g1.travelItems.clear();
		g1.travelItems = new ArrayList<Calories2>(futurePlans);

		groups.add(g1);
		
		notifyDataSetChanged();
	}

	
	/*
	 * Holder for the Future view type
	 */
	class FutureTravelViewHolder {
		TextView title;
		TextView departure;
		TextView destination;
		TextView date;
		TextView time;
	}
	
	/*
	 * Wrapper for each group that contains the
	 * list elements and the type of travel.
	 */
	public static class Group {
		public enum Type {
			FUTURE;
		};
		
		public Type type;
		ArrayList<Calories2> travelItems = new ArrayList<Calories2>();
	}
}
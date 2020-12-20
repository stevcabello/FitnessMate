package unimelb.steven.fitnessapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import unimelb.steven.fitnessapp.MainActivity;
import unimelb.steven.fitnessapp.R;
import unimelb.steven.fitnessapp.adapters.Calories2Adapter;
import unimelb.steven.fitnessapp.adapters.CustomExpandableListAdapter;
import unimelb.steven.fitnessapp.adapters.ExpandableListAdapter;
import unimelb.steven.fitnessapp.database.DatabaseHandler;
import unimelb.steven.fitnessapp.models.Calories2;

import static org.apache.commons.math3.util.Precision.round;


public class CaloriesFragment extends Fragment {

    private ExpandableListView expList;
    private ExpandableListAdapter expListAdapter;

    ExpandableListView expandableListView;
    CustomExpandableListAdapter expandableListAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View rootView;
    private Calories2Adapter adapter;
    private ListView listView;
    private String TAG = "CaloriesFragment";
    private ArrayList<Calories2> caloriesArr;
    private ArrayList<String> titles;
    HashMap<String, ArrayList<Calories2>> expandableListDetail;
    ArrayList<String> expandableListCalories;

    DatabaseHandler db;

    // TODO: Rename and change types of parameters
    public static CaloriesFragment newInstance(String param1, String param2) {
        CaloriesFragment fragment = new CaloriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CaloriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        if (rootView != null) {
//            return rootView;
//        }

        //rootView = inflater.inflate(R.layout.fragment_calories, container, false);
        rootView = inflater.inflate(R.layout.expandable_list_layout, container, false);

        db = new DatabaseHandler(getActivity().getApplicationContext());



        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        expandableListDetail = new HashMap<String, ArrayList<Calories2>>();
        expandableListCalories = new ArrayList<>();

        Log.i(TAG, "onActivityCreated");

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss.SSS");
//
//        Date originalDate = new Date();
//        String strDate1 = sdf.format(originalDate);

        //caloriesArr = db.getCalories2(strDate1);

        titles = db.getCalories2Titles();

        double totalCalories=0;

        if (titles !=null) {

//            Log.i(TAG,"calories array size: " + String.valueOf(caloriesArr.size()));
//            Log.i(TAG,"calories :" + caloriesArr.get(0).getCalories());

            for (int i=0; i<titles.size();i++){
                totalCalories = 0;

                caloriesArr = db.getCalories2(titles.get(i));
                Log.i(TAG,String.valueOf(caloriesArr.size()));
                expandableListDetail.put(titles.get(i),caloriesArr);


                Log.i(TAG,expandableListDetail.get(titles.get(i)).get(0).getActivity());

                for (int j=0; j<caloriesArr.size();j++){
                    totalCalories = totalCalories + Double.parseDouble(caloriesArr.get(j).getCalories());
                }

                expandableListCalories.add(String.valueOf(round(totalCalories,2)));

                //caloriesArr.clear();
            }

            expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
            expandableListAdapter = new CustomExpandableListAdapter(getActivity().getApplicationContext(), titles,
                    expandableListDetail, expandableListCalories);
            //expandableListView.setGroupIndicator(null);
            expandableListView.setAdapter(expandableListAdapter);


            expandableListView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

//            expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//                    Log.i(TAG,"onscrollstatechanged");
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                    Log.i(TAG,"onscroll");
//                }
//            });
//
//
//            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//                @Override
//                public void onGroupExpand(int groupPosition) {
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            titles.get(groupPosition) + " List Expanded.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//                @Override
//                public void onGroupCollapse(int groupPosition) {
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            titles.get(groupPosition) + " List Collapsed.",
//                            Toast.LENGTH_SHORT).show();
//
//                }
//            });
//
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {


//                    final String activity = expandableListDetail.get(titles.get(groupPosition)).get(childPosition).getActivity();
//
//                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//                    final EditText edittext = new EditText(getActivity());
//                    alert.setMessage("axis,wsize");
//                    alert.setView(edittext);
//
//                    switch (activity){
//                        case "Walking":
//                            edittext.setText(MainActivity.walkingStepParams);
//                            break;
//                        case "Jogging":
//                            edittext.setText(MainActivity.joggingStepParams);
//                            break;
//                        case "Cycling":
//                            edittext.setText(MainActivity.cyclingStepParams);
//                            break;
//                        case "Upstairs":
//                            edittext.setText(MainActivity.upstairsStepParams);
//                            break;
//                        case "Downstairs":
//                            edittext.setText(MainActivity.downstairsStepParams);
//                            break;
//                    }
//
//                    alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            String params = edittext.getText().toString();
//
//                            switch (activity){
//                                case "Walking":
//                                    MainActivity.walkingStepParams = params;
//                                    break;
//                                case "Jogging":
//                                    MainActivity.joggingStepParams = params;
//                                    break;
//                                case "Cycling":
//                                    MainActivity.cyclingStepParams = params;
//                                    break;
//                                case "Upstairs":
//                                    MainActivity.upstairsStepParams = params;
//                                    break;
//                                case "Downstairs":
//                                    MainActivity.downstairsStepParams = params;
//                                    break;
//                            }
//                        }
//                    });
//
//                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            //ad.cancel();
//                        }
//                    });
//
//                    AlertDialog ad = alert.create();
//                    ad.show();
//
                    return false;
                }

            });

//            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//                @Override
//                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                    Toast.makeText(getActivity().getApplicationContext(),"show something",Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            });

            expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id) {

                    long packedPosition = expandableListView.getExpandableListPosition(position);
                            //m_expandableListView.getExpandableListPosition(position);

                    int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                    int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                    int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);


        /*  if group item clicked */
                    if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                        //  ...
                        onGroupLongClick(groupPosition);
                    }

        /*  if child item clicked */
                    else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                        //  ...
                        //onChildLongClick(groupPosition, childPosition);
                    }


                    return false;
                }
            });



//            expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(getActivity().getApplicationContext(),"delete",Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            });


//            expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(getActivity().getApplicationContext(),"show something",Toast.LENGTH_SHORT).show();
//                }
//            });

//            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//                @Override
//                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                    Log.i(TAG,"on group click");
//                    parent.expandGroup(groupPosition);
//                    return false;
//                }
//            });

        }


//            expList = (ExpandableListView) rootView.findViewById(R.id.expandableList);
//
//            expListAdapter = new ExpandableListAdapter(getActivity().getApplicationContext());
//
//            //removes the standard group state indicator
//            expList.setGroupIndicator(null);
//
//
//            //send data to adapter
//            expListAdapter.setupTrips(caloriesArr);
//            //tie the adapter to our expandable list view
//            expList.setAdapter(expListAdapter);

        //}

    }

    private void onGroupLongClick(final int groupPosition) {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity());
        builder.setMessage("Delete records from this day?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                String day = titles.get(groupPosition);
                                db.deleteCalories(day);
                                MainActivity.pagerAdapter.notifyDataSetChanged();

                                Toast.makeText(getActivity().getApplicationContext(),day + " deleted",Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}

package unimelb.steven2.fitnessapp.fragments;

//import android.support.v4.app.Fragment;

//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;


import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {//{implements OnMapReadyCallback {
//    private GoogleMap map;
//
//   private OnFragmentInteractionListener mListener;
//
//    private View rootView = null;
//    SupportMapFragment fragment;
//    private float ultimoZoom=14;
//    int contadorZoom=0;
//
//    DatabaseHandler db;
//    ArrayList<LatLng> LatLngs = new ArrayList<>();
//
//    String TAG = MapFragment.class.getSimpleName();
//    PolylineOptions lineas;
//
//
//
//    public static MapFragment newInstance(String accesstoken, String clientid) {
//        MapFragment fragment = new MapFragment();
//        return fragment;
//    }
//
//    public MapFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
//
//
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        if (rootView != null) {
//            return rootView;
//        }
//
//        setHasOptionsMenu(true); //to enable the search button view
//
//        // Inflate the layout for this fragment
//        rootView = inflater.inflate(R.layout.fragment_map,container,false);
//
//
//
//        Log.i(TAG,"onCreateView");
//
////        FrameLayout frameLayout = new FrameLayout(getActivity());
////        frameLayout.setBackgroundColor(
////                getResources().getColor(android.R.color.transparent));
////        ((ViewGroup) rootView).addView(frameLayout,
////                new ViewGroup.LayoutParams(
////                        LinearLayout.LayoutParams.MATCH_PARENT,
////                        LinearLayout.LayoutParams.MATCH_PARENT
////                )
////        );
//
//
//
//        db = new DatabaseHandler(getActivity().getApplicationContext());
//
//
//        return rootView;
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.i(TAG, "onPause");
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        Log.i(TAG,"onActivityCreated");
//
////        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
////                .findFragmentById(R.id.map);
////
////        if (mapFragment != null)
////            mapFragment.getMapAsync(this);
//
//        FragmentManager fm = getChildFragmentManager();
//        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
//        if (fragment == null) {
//            fragment = SupportMapFragment.newInstance();
//            fm.beginTransaction().replace(R.id.map, fragment).commit();
//        }
//
//    }
//
//    public void onResume() {
//
//        super.onResume();
//
//        Log.i(TAG, "onResume");
//
//        showLines();
//        //markerPoints = new ArrayList<LatLng>();
//
////        if (map ==null){
////            {
////                map = fragment.getMap();
////
////                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
////                        new LatLng(-2,-3),2
////                ));
////
////                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
////
////                    @Override
////                    public void onCameraChange(CameraPosition pos) {
////                        if (pos.zoom != ultimoZoom){
////                            contadorZoom+=1;
////                            if (contadorZoom ==1)
////                                ultimoZoom = 14;
////                            else
////                                ultimoZoom = pos.zoom;
////
////                            //Log.i("Zoom Actualizado", String.valueOf(ultimoZoom));
////                            // do you action here
////                            showLines();
////                        }
////                    }
////                });
////            }
////
////
////        }
//
//    }
//
//
//    private void showLines()
//    {
//
//        Log.i(TAG,"inside showLines");
//
//        MarkerOptions options = new MarkerOptions();
//        Marker marker;
//
//        PolylineOptions polyLineOptions = new PolylineOptions();
//        polyLineOptions.color(Color.RED);
//
//        //TODO: disabled by now because it speeds down the app, change it later
//        //LatLngs = db.getCoordinates();
//
//
//        if (map !=null) {
//            map.getUiSettings().setZoomControlsEnabled(true);
//            map.getUiSettings().setScrollGesturesEnabled(true);
//            map.getUiSettings().setTiltGesturesEnabled(true);
//            map.getUiSettings().setAllGesturesEnabled(true);
//            map.getUiSettings().setRotateGesturesEnabled(true);
//        }
//
//        if (LatLngs != null && LatLngs.size()>0) {
//
//            if (map ==null)
//            {
//                Log.i(TAG,"map null");
//                map = fragment.getMap();
//
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                        LatLngs.get(0), 14
//                ));
//
//
//            }
//
//            Log.i(TAG,String.valueOf(LatLngs.size()));
//
//            for (int i=0;i<LatLngs.size();i++){
//
//                options.position(LatLngs.get(i));
//                polyLineOptions.add(LatLngs.get(i));
//
//                if (i==0) {
//                    marker = map.addMarker(options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)) );
//                }else if (i==LatLngs.size()-1){
//                    marker = map.addMarker(options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)) );
//                }else {
//                    marker = map.addMarker(options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)) );
//                }
//
//                marker.setVisible(true);
////                map.addMarker(new MarkerOptions()
////                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))//.fromResource(R.drawable.gps_marker))
////                        .position(LatLngs.get(i))
////                        .flat(true));
////
////                lineas = new PolylineOptions()
////                        .add(LatLngs.get(i));
//            }
//
//            Polyline polyline = map.addPolyline(polyLineOptions);
//            polyline.setVisible(true);
//
////            lineas.width(7);
////            lineas.color(Color.BLUE);
////
////            map.addPolyline(lineas);
//
//        } else {
//            Log.i(TAG, "LatLngs is null");
//            if (map != null) map.clear();
//        }
//
//    }
//
//
//
//
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        Log.i(TAG,"onAttach");
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//
//        Log.i(TAG, "onDetach");
//
//
//        mListener = null;
//    }
//
//
//    /**
//     * Charge the information of the most popular
//     * Instagram users.
//     */
//    public interface OnFragmentInteractionListener {
//        public void onFragmentInteraction(Uri uri);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.i(TAG,"onDestroy");
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        LatLng sydney = new LatLng(-34, 151);
//        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
}

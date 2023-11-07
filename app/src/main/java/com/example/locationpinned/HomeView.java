package com.example.locationpinned;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationpinned.databinding.FragmentHomeViewBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class HomeView extends Fragment {

    private FragmentHomeViewBinding binding;
    private List<LocationObject> locationObjects = new ArrayList<>();
    private GeocoderHelper geocoderHelper;

    private DatabaseHelper db;
    private SearchView searchView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize geocoder and database
        geocoderHelper = new GeocoderHelper(getContext());
        db = new DatabaseHelper(getActivity());

        //Used for Testing
        db.deleteAllData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize Binding
        binding = FragmentHomeViewBinding.inflate(inflater, container, false);

        // Add New Address
        binding.addAddress.setOnClickListener(v ->
            NavHostFragment.findNavController(HomeView.this).navigate(R.id.action_address_view_to_newAddress));

        // Load File from Raw Resource
        binding.loadFile.setOnClickListener(v -> {
            try {
                loadFileDB();
                loadLocations("");
                loadLinearLayout();
                Toast.makeText(getContext(), "Successfully added file locations to Database", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("LocationData", "Error loading location file", e);
            }
        });

        //Search Bar on text change filter notes
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadLocations("");
                    loadLinearLayout();
                }else{
                    loadLocations(newText);
                    loadLinearLayout();
                }
                return true;
            }
        });

        loadLocations("");
        loadLinearLayout();
        return binding.getRoot();
    }

    // Load saved addresses into Linear layout
    private void loadLinearLayout() {

        LinearLayout linearLayout = binding.locationsLayout;
        int marginInPixels = (int) (8 * getResources().getDisplayMetrics().density);

        // Clear views when called again
        linearLayout.removeAllViews();

        for (LocationObject location : locationObjects) {
            // Inflate the item layout
            View view = LayoutInflater.from(getContext()).inflate(R.layout.saved_address, null, false);

            // Set Views for saved addresses
            TextView Street = view.findViewById(R.id.address);
            TextView Latitude = view.findViewById(R.id.Latitude);
            TextView Longitude = view.findViewById(R.id.Longitude);

            // Set Data for saved addresses
            Street.setText(location.getAddress());
            Latitude.setText(String.valueOf(location.getLatitude()));
            Longitude.setText(String.valueOf(location.getLongitude()));

            // Delete address from db and location array
            ImageButton deleteButton = view.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v -> {
                db.deleteAddress(location.getId());
                locationObjects.remove(location);  // Remove location object from the list
                loadLinearLayout();
            });

            // Edit addresses and update db and location array
            view.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("location", location);
                NavHostFragment.findNavController(HomeView.this).navigate(R.id.action_address_view_to_editAddress, bundle);
            });
            // Add margins between cards
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, marginInPixels, 0, marginInPixels);
            view.setLayoutParams(layoutParams);

            linearLayout.addView(view);
        }
    }

    // Load file to database
    public void loadFileDB() throws IOException {

        InputStream raw = getResources().openRawResource(R.raw.location);
        BufferedReader in = new BufferedReader(new InputStreamReader(raw));
        String line = in.readLine();

        // reset location objects array
        locationObjects.clear();

        // Read file
        while(line != null){
            // split string get variables
            String[] split = line.split(",");
            double latitude = Double.parseDouble(split[0].trim());
            double longitude = Double.parseDouble(split[1].trim());
            String address = geocoderHelper.getAddress(latitude, longitude);

            // populate database
            db.loadFile(address, longitude, latitude);
            line = in.readLine();
        }

        in.close();
    }

    public void loadLocations(String searchBarText) {
        locationObjects.clear();
        Cursor cursor = db.readAllData(searchBarText);

        if(cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "No addresses in Table", Toast.LENGTH_SHORT).show();
        } else {
            while(cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String address = cursor.getString(1);
                double latitude = cursor.getDouble(2);
                double longitude = cursor.getDouble(3);

                LocationObject location = new LocationObject(id, latitude, longitude, address);
                locationObjects.add(location);
            }
        }
        cursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
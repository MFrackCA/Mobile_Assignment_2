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

import com.example.locationpinned.databinding.FragmentAddressViewBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class AddressView extends Fragment {

    private FragmentAddressViewBinding binding;
    private List<LocationObject> locationObjects = new ArrayList<>();
    private FindAddressHelper findAddressHelper;

    private DatabaseHelper db;
    private SearchView searchView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize geocoder
        findAddressHelper = new FindAddressHelper(getContext());
        db = new DatabaseHelper(getActivity());
        //db.deleteAllData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize Binding
        binding = FragmentAddressViewBinding.inflate(inflater, container, false);

        // Add New Address
        binding.addAddress.setOnClickListener(v ->
            NavHostFragment.findNavController(AddressView.this).navigate(R.id.action_address_view_to_newAddress));

        // Load File from Raw Resource
        binding.loadFile.setOnClickListener(v -> {
            try {
                loadFileDB();
                loadLocations("");
                populateLinearLayout();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("LocationData", "Error loading location file", e);
            }
        });

        //Search Bar on text change filter notes
        searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadLocations("");
                    populateLinearLayout();
                }else{
                    loadLocations(newText);
                    populateLinearLayout();
                }
                return true;
            }
        });

        loadLocations("");
        populateLinearLayout();
        return binding.getRoot();
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
            String address = findAddressHelper.getAddress(latitude, longitude);

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
    private void populateLinearLayout() {

        LinearLayout linearLayout = binding.locationsLayout;

        // Clear views when called again
        linearLayout.removeAllViews();

        for (LocationObject location : locationObjects) {
            // Inflate the item layout
            View view = LayoutInflater.from(getContext()).inflate(R.layout.saved_address, null, false);

            // Set Views/Data for saved addresses
            TextView Street = view.findViewById(R.id.address);
            TextView Latitude = view.findViewById(R.id.Latitude);
            TextView Longitude = view.findViewById(R.id.Longitude);

            Street.setText(location.getAddress());
            Latitude.setText(String.valueOf(location.getLatitude()));
            Longitude.setText(String.valueOf(location.getLongitude()));


            // Delete notes from db and location objects array
            ImageButton deleteButton = view.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v -> {
                db.deleteAddress(location.getId());
                linearLayout.removeView(view);
                loadLocations("");
            });

            linearLayout.addView(view);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
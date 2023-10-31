package com.example.locationpinned;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private FindAddress findAddress;

    private DatabaseHelper db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize geocoder
        findAddress = new FindAddress(getContext());
        db = new DatabaseHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize Binding
        binding = FragmentAddressViewBinding.inflate(inflater, container, false);

        // Add New Address
        binding.addAddress.setOnClickListener(v ->
            NavHostFragment.findNavController(AddressView.this).navigate(R.id.action_adress_view_to_newAddress));

        // Load File from Raw Resource
        binding.loadFile.setOnClickListener(v -> {
            try {
                loadFileDB();
                populateLinearLayout();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("LocationData", "Error loading location file", e);
            }
        });
        loadLocations("");
        return binding.getRoot();
    }

    public void loadFileDB() throws IOException {

        InputStream raw = getResources().openRawResource(R.raw.location);
        BufferedReader in = new BufferedReader(new InputStreamReader(raw));
        String line = in.readLine();

        // reset location objects array
        locationObjects.clear();

        // Read file
        while(line != null){
            // split string get varibales
            String[] split = line.split(",");
            double latitude = Double.parseDouble(split[0].trim());
            double longitude = Double.parseDouble(split[1].trim());
            String address = findAddress.getAddressFromLatLng(latitude, longitude);

            // populate database
            db.loadFile(address, longitude, latitude);
            line = in.readLine();
//            locationObjects.add(new LocationObject(latitude, longitude, address));
        }
        in.close();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateLinearLayout() {
        LinearLayout linearLayout = binding.locationsLayout;

        for (LocationObject location : locationObjects) {
            // Inflate the item layout
            View view = LayoutInflater.from(getContext()).inflate(R.layout.saved_address, null, false);

            // Populate the data
            TextView tvStreet = view.findViewById(R.id.address);
            TextView tvLatitude = view.findViewById(R.id.Latitude);
            TextView tvLongitude = view.findViewById(R.id.Longitude);

            tvStreet.setText(location.getAddress());
            tvLatitude.setText(String.valueOf(location.getLatitude()));
            tvLongitude.setText(String.valueOf(location.getLongitude()));

            // Add the populated view to the LinearLayout
            linearLayout.addView(view);
        }
    }
    public void loadLocations(String searchBarText) {
        locationObjects.clear();

        Cursor cursor = db.readAllData(searchBarText);

        if(cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "No Locations in Table", Toast.LENGTH_SHORT).show();
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

        populateLinearLayout();
        cursor.close();
    }
}
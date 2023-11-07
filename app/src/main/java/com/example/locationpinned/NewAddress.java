package com.example.locationpinned;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.locationpinned.databinding.FragmentNewAddressBinding;


public class NewAddress extends Fragment {

    private FragmentNewAddressBinding binding;
    private DatabaseHelper db;
    private GeocoderHelper geocoderHelper;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate db and geocoder
        db = new DatabaseHelper(getActivity());
        geocoderHelper = new GeocoderHelper(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNewAddressBinding.inflate(inflater, container, false);


        // cancel new note
        binding.cancel.setOnClickListener(v -> {
            // Navigate back to home screen
            NavHostFragment.findNavController(NewAddress.this)
                    .navigate(R.id.action_newAddress_to_address_view);
        });

        // Find address with geocode
        binding.geocode.setOnClickListener(v -> {

            // set latitude and longitude
            double latitude = Double.parseDouble(binding.newLatitude.getText().toString().trim());
            double longitude = Double.parseDouble(binding.newLongitude.getText().toString().trim());

            // Check that latitude or longitude are within range
            if(latitude < -90 || latitude > 90){
                Toast.makeText(getActivity(), "Latitude must be between -90 and 90", Toast.LENGTH_LONG).show();
                return;
            }

            if(longitude <-180 || longitude > 180) {
                Toast.makeText(getActivity(), "Latitude must be between -90 and 90", Toast.LENGTH_LONG).show();
                return;
            }
                // Call get address method from geocoder
                String address = geocoderHelper.getAddress(latitude, longitude);

                // if address is found set textview to display address
                // else if geocode finds nothing set text view to address not found
                if (address != null) {
                    binding.textView.setText(address);
                } else {
                    binding.textView.setText("Address not found.");
                    Toast.makeText(getActivity(), "Please enter a valid address.", Toast.LENGTH_SHORT).show();
                }
        });

        // Save address to database
        binding.saveAddress.setOnClickListener(v -> {
            // set latitude and longitude vars
            double latitude = Double.parseDouble(binding.newLatitude.getText().toString().trim());
            double longitude = Double.parseDouble(binding.newLongitude.getText().toString().trim());

            // get address
            String address = binding.textView.getText().toString();

            // if address is empty or message not found do not allow saving to database
            if (address.isEmpty() || "Address not found.".equals(address)) {
                Toast.makeText(getActivity(), "Please enter a valid address.", Toast.LENGTH_SHORT).show();
                return;
            }

            // save address to database
            db.addAddress(address, latitude, longitude);

            // navigate back home
            NavHostFragment.findNavController(NewAddress.this)
                    .navigate(R.id.action_newAddress_to_address_view);
        });

        return binding.getRoot();
    }
}
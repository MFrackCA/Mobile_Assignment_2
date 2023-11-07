package com.example.locationpinned;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.locationpinned.databinding.FragmentEditAddressBinding;


public class EditAddress extends Fragment {

    private FragmentEditAddressBinding binding;
    private DatabaseHelper db;
    private GeocoderHelper geocoderHelper;
    private Bundle bundle;
    private LocationObject locationObject;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate db and geocoder
        db = new DatabaseHelper(getActivity());
        geocoderHelper = new GeocoderHelper(getActivity());

        // unpack bundle for saved location
        bundle = this.getArguments();
        if (bundle != null){
            locationObject = (LocationObject) bundle.getSerializable("location");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEditAddressBinding.inflate(inflater, container, false);

        // Set initial views
        binding.newLatitude.setText(String.valueOf(locationObject.getLatitude()));
        binding.newLongitude.setText(String.valueOf(locationObject.getLongitude()));
        binding.addressOutput.setText(locationObject.getAddress());


        // cancel new note
        binding.cancel.setOnClickListener(v -> {
            // Navigate back to home screen
            NavHostFragment.findNavController(EditAddress.this)
                    .navigate(R.id.action_editAddress_to_address_view);
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

            if (address != null) {
                binding.addressOutput.setText(address);
            } else {
                binding.addressOutput.setText("Address not found.");
                Toast.makeText(getActivity(), "Please enter a valid address.", Toast.LENGTH_SHORT).show();
            }
        });

        // Save address to database
        binding.updateAddress.setOnClickListener(v -> {
            String address = binding.addressOutput.getText().toString();

            // if address is found set textview to display address
            // else if geocode finds nothing set text view to address not found
            if (address.isEmpty() || "Address not found.".equals(address)) {
                Toast.makeText(getActivity(), "Please enter a valid address.", Toast.LENGTH_SHORT).show();
                return;
            }
            // set latitude and longitude
            double latitude = Double.parseDouble(binding.newLatitude.getText().toString().trim());
            double longitude = Double.parseDouble(binding.newLongitude.getText().toString().trim());

            // update address in DB
            db.updateAddress(locationObject.getId(), address, latitude, longitude);

            // Navigate back to home screen
            NavHostFragment.findNavController(EditAddress.this)
                    .navigate(R.id.action_editAddress_to_address_view);
        });

        return binding.getRoot();
    }
}
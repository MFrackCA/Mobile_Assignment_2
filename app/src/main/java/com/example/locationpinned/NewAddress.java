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
    private GeocoderHelper findAddressHelper;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());
        findAddressHelper = new GeocoderHelper(getActivity());
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

                double latitude = Double.parseDouble(binding.newLatitude.getText().toString().trim());
                double longitude = Double.parseDouble(binding.newLongitude.getText().toString().trim());
                // Call get address method
                String address = findAddressHelper.getAddress(latitude, longitude);

                if (address != null) {
                    binding.textView.setText(address);
                } else {
                    binding.textView.setText("Address not found.");
                }
        });

        // Save address to database
        binding.saveAddress.setOnClickListener(v -> {
            String address = binding.textView.getText().toString();
            if (address.isEmpty() || "Address not found.".equals(address)) {
                Toast.makeText(getActivity(), "Please enter a valid address.", Toast.LENGTH_SHORT).show();
                return;
            }

            double latitude = Double.parseDouble(binding.newLatitude.getText().toString().trim());
            double longitude = Double.parseDouble(binding.newLongitude.getText().toString().trim());
            db.addAddress(address, latitude, longitude);
            NavHostFragment.findNavController(NewAddress.this)
                    .navigate(R.id.action_newAddress_to_address_view);
        });

        return binding.getRoot();
    }
}
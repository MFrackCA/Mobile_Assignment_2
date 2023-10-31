package com.example.locationpinned;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.locationpinned.databinding.FragmentAdressViewBinding;


public class AddressView extends Fragment {

    private FragmentAdressViewBinding binding;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize Binding
        binding = FragmentAdressViewBinding.inflate(inflater, container, false);

        // Add New Note
        binding.addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AddressView.this)
                        .navigate(R.id.action_adress_view_to_newAddress);
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
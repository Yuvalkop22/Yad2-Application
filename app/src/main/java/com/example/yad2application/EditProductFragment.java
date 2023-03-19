package com.example.yad2application;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.yad2application.Model.Product;
import com.example.yad2application.databinding.FragmentEditProductBinding;
import com.example.yad2application.databinding.FragmentProductPageBinding;
import com.squareup.picasso.Picasso;

public class EditProductFragment extends Fragment {
    private FragmentEditProductBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        setHasOptionsMenu(true);
        binding = FragmentEditProductBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.CATEGORIES, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.spinnerEditPage.setAdapter(adapter);

        getParentFragmentManager().setFragmentResultListener("EditproductDetail", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d("TAG", "In product page -> name: " + result.getString("name"));

                Product pr = (Product) result.getSerializable("product");
                binding.productNameEditPage.setText(pr.getName());
                binding.priceEditPage.setText(pr.getPrice());
                binding.descriptionEditPage.setText(pr.getDescription());
                Picasso.get().load(pr.getAvatarUrl()).into(binding.avatarImgEditPage);
            }});
        return view;
    };
}
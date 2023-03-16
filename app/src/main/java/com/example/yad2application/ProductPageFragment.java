package com.example.yad2application;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.yad2application.ProductModel.Product;
import com.example.yad2application.ProductModel.ProductModel;
import com.example.yad2application.databinding.FragmentProductPageBinding;
import com.squareup.picasso.Picasso;


public class ProductPageFragment extends Fragment {
    private FragmentProductPageBinding binding;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        setHasOptionsMenu(true);
        binding = FragmentProductPageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        getParentFragmentManager().setFragmentResultListener("productDetail", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d("TAG", "In product page -> name: " + result.getString("name"));

                binding.textProductNamePreview.setText(result.getString("name"));
                binding.textCategoryPreview.setText(result.getString("category"));
                binding.textPricePreview.setText(result.getString("price"));
                binding.textDescriptionPreview.setText(result.getString("description"));
                Picasso.get().load(result.getString("imgURL")).into(binding.productImg);

            }
        });



        binding.btnCancelProdPage.setOnClickListener((view1)->{
            Navigation.findNavController(view1).popBackStack();
        });

        return view;

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
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

import com.example.yad2application.databinding.FragmentProductPageBinding;
import com.squareup.picasso.Picasso;


public class ProductPageFragment extends Fragment {
    private FragmentProductPageBinding binding;
    ProductsListFragmentViewModel viewModel;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        viewModel = new ViewModelProvider(this).get(ProductsListFragmentViewModel.class);

        setHasOptionsMenu(true);
        binding = FragmentProductPageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        getParentFragmentManager().setFragmentResultListener("posClicked", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int pos = result.getInt("pos");
                Log.d("TAG", pos + "");
                binding.textProductNamePreview.setText(viewModel.getData().getValue().get(pos).getName());
                binding.textCategoryPreview.setText(viewModel.getData().getValue().get(pos).getCategory());
                binding.textPricePreview.setText(viewModel.getData().getValue().get(pos).price);
                binding.textDescriptionPreview.setText(viewModel.getData().getValue().get(pos).getDescription());
                Picasso.get().load(viewModel.getData().getValue().get(pos).getAvatarUrl()).into(binding.productImg);
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
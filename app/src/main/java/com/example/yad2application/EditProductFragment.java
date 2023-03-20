package com.example.yad2application;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.yad2application.Model.Model;
import com.example.yad2application.Model.Product;
import com.example.yad2application.databinding.FragmentEditProductBinding;
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

        // Retrieve the arguments passed from the previous fragment
        getParentFragmentManager().setFragmentResultListener("EditproductDetail1",this,new FragmentResultListener(){

            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Product product = (Product) result.getSerializable("product");
                String name = product.getName();
                String price = product.getPrice();
                String description = product.getDescription();
                binding.productNameEditPage.setText(name);
                binding.priceEditPage.setText(product.getPrice());
                binding.descriptionEditPage.setText(description);

                binding.Save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = binding.productNameEditPage.getText().toString();
                        String newPrice = binding.priceEditPage.getText().toString();
                        String newDescription = binding.descriptionEditPage.getText().toString();
                        if (newName.equals(name) && newPrice.equals(price) && newDescription.equals(description)){
                            Toast.makeText(getContext(),"Nothing changed",Toast.LENGTH_LONG).show();
                        }else{
                            Model.instance().updateProduct(product.getProductId(), newName, newPrice, newDescription, (unused) -> {
                                requireActivity().runOnUiThread(() -> {
                                    Navigation.findNavController(view).navigate(R.id.productsListFragment);
                                });
                            });
                        }
                    }
                });
                binding.cancellBtnEditPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigation.findNavController(view).navigate(R.id.productsListFragment);
                    }
                });
            }
        });

        return view;
    };
}
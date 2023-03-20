package com.example.yad2application;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.yad2application.Model.CurrencyConversion;
import com.example.yad2application.Model.CurrencyConversionModel;
import com.example.yad2application.Model.Joke;
import com.example.yad2application.Model.JokeModel;
import com.example.yad2application.Model.Model;
import com.example.yad2application.Model.Product;
import com.example.yad2application.Model.Product;
import com.example.yad2application.Model.User;
import com.example.yad2application.databinding.FragmentProductPageBinding;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;


public class ProductPageFragment extends Fragment {
    private FragmentProductPageBinding binding;
    ProductsListFragmentViewModel viewModel;
    private String selectedCurrency;
    LiveData<CurrencyConversion> data;
    String currentCurrency;
    double currentPrice;
    String priceString;
    int currentCurrenctPosition;
    boolean isProductOwner = false;

    private Product product;

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
        viewModel = new ViewModelProvider(this).get(ProductsListFragmentViewModel.class);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.CURRENCY, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.currencySpinner.setAdapter(adapter);

        getParentFragmentManager().setFragmentResultListener("EditproductDetail", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d("TAG", "In product page -> name: " + result.getString("name"));

                Product pr = (Product) result.getSerializable("product");
                binding.textProductNamePreview.setText(pr.getName());
                binding.textCategoryPreview.setText(pr.getCategory());
                binding.textPricePreview.setText(pr.getPrice());
                priceString = pr.getPrice();
                binding.textDescriptionPreview.setText(pr.getDescription());
                Picasso.get().load(pr.getAvatarUrl()).into(binding.productImg);

                if (pr.ownerEmail.equals(Model.instance().getCurrentUser().getEmail())){
                    binding.btnBuyProdPage.setText("Edit");
                    binding.btnCancelProdPage.setText("Delete");
                    isProductOwner = true;
                }
                //If the user is not the owner
                if(!isProductOwner){
                    binding.btnBuyProdPage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String email = Model.instance().getCurrentUser().getEmail();
                            Model.instance().order(pr,email,(unused)->{
                                Toast.makeText(getActivity(),"Thanks",Toast.LENGTH_LONG).show();
                                Navigation.findNavController(view).navigate(R.id.productsListFragment);

                            });
                        }
                    });
                    binding.btnCancelProdPage.setOnClickListener((view1) -> {
                        Navigation.findNavController(view1).popBackStack();
                    });
                }
                //If the user is the owner
                if(isProductOwner){
                    //Edit button - Navigate to edit page
                    binding.btnBuyProdPage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("product", pr);
                            getParentFragmentManager().setFragmentResult("EditproductDetail1",bundle);
                            EditProductFragment editProductFragment = new EditProductFragment();
                            editProductFragment.setArguments(bundle);
                            Navigation.findNavController(view).navigate(R.id.editProductFragment);

                        }
                    });
                    //Delete button - Yuval needs to configure.
                    binding.btnCancelProdPage.setOnClickListener((view1) -> {
                        Navigation.findNavController(view1).popBackStack();
                    });
                }
            }

        });



            currentCurrenctPosition = 0;
            binding.currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ///Currency Selector
                currentCurrency = "USD";
                try {
                    if (priceString != null)
                        currentPrice = Double.parseDouble(priceString.trim());
                }catch (NumberFormatException e){
                    Log.d("TAG", "Failed parse to double: " + e.getMessage());
                }
                selectedCurrency =adapter.getItem(position).toString();
                if(position != currentCurrenctPosition) {
                    currentCurrenctPosition = position;
                    Log.d("TAG", "Currency clicked: " + selectedCurrency);
                    Log.d("TAG", "Currency current: " + currentCurrency);
                    Log.d("TAG", "Currency price: " + currentPrice);
                    data = CurrencyConversionModel.instance.convertCurrency(currentCurrency,selectedCurrency,currentPrice);
                    data.observe(getViewLifecycleOwner(), result->{
                        String newAmount = result.getNew_amount().toString();
                        Log.d("TAG", "Price returned: " + newAmount);
                        binding.textPricePreview.setText(newAmount);
                        currentCurrency = result.getNew_currency();
                        try {
                            currentPrice = Double.parseDouble(result.getNew_amount());
                        }catch (NumberFormatException e) {
                            Log.d("TAG", "onCreateView: " + e.getMessage());
                        }});

                }
                }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
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
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
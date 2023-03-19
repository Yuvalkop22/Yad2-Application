package com.example.yad2application;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yad2application.Model.Model;
import com.example.yad2application.Model.Product;
import com.example.yad2application.databinding.FragmentProductsListBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsListFragment extends Fragment {
    FragmentProductsListBinding binding;
    ProductRecyclerAdapter adapter;
    ProductsListFragmentViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductsListFragmentViewModel.class);
    }

    Bundle bundle = new Bundle();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductsListBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        binding.productsrecyclerList.setHasFixedSize(true);
        binding.productsrecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductRecyclerAdapter(getLayoutInflater(),viewModel.getData().getValue());
        binding.productsrecyclerList.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Log.d("TAG", "Row was clicked " + pos);
                Product pr = viewModel.getData().getValue().get(pos);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", pr);

                getParentFragmentManager().setFragmentResult("productDetail",bundle);
                ProductPageFragment productPageFragment = new ProductPageFragment();
                productPageFragment.setArguments(bundle);

                Navigation.findNavController(view).navigate(R.id.productPageFragment);
            }
        });
        binding.progressBar.setVisibility(View.GONE);

        viewModel.getData().observe(getViewLifecycleOwner(),list->{
            adapter.setData(list);
        });

        Model.instance().EventProductsListLoadingState.observe(getViewLifecycleOwner(),status->{
            binding.swipeRefresh.setRefreshing(status == Model.LoadingState.LOADING);
        });

        binding.swipeRefresh.setOnRefreshListener(()->{
            reloadData();
        });

        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ProductsListFragmentViewModel.class);
    }

    void reloadData(){
        //binding.progressBar.setVisibility(View.VISIBLE);
        Model.instance().refreshAllProducts();
    }
    static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView nameTv;
        TextView priceTv;
        List<Product> data;
        ImageView avatarImage;
        public ProductViewHolder(@NonNull View itemView, ProductRecyclerAdapter.OnItemClickListener listener, List<Product> data) {
            super(itemView);
            this.data = data;
            nameTv = itemView.findViewById(R.id.productlistrow_name_tv);
            priceTv = itemView.findViewById(R.id.productlistrow_price_tv);
            avatarImage = itemView.findViewById(R.id.productlistrow_avatar_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(pos);
                }
            });
        }

        public void bind(Product st, int pos) {
            nameTv.setText(st.name);
            priceTv.setText("$" + st.price);
            if (st.getAvatarUrl()  != null && st.getAvatarUrl().length() > 5) {
                Picasso.get().load(st.getAvatarUrl()).placeholder(R.drawable.ic_baseline_photo_camera_24).into(avatarImage);
            }else{
                avatarImage.setImageResource(R.drawable.avatar);
            }
        }
    }

    public static class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductViewHolder>{
        OnItemClickListener listener;
        public interface OnItemClickListener{
            void onItemClick(int pos);
        }

        LayoutInflater inflater;
        List<Product> data;
        public void setData(List<Product> data){
            this.data = data;
            notifyDataSetChanged();
        }
        public ProductRecyclerAdapter(LayoutInflater inflater, List<Product> data){
            this.inflater = inflater;
            this.data = data;
        }

        void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.products_list_row,parent,false);
            return new ProductViewHolder(view,listener, data);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = data.get(position);
            Log.d("TAG","ID = " + product.getProductId());
            holder.bind(product,position);
        }

        @Override
        public int getItemCount() {
            if (data == null) return 0;
            return data.size();
        }

    }

}
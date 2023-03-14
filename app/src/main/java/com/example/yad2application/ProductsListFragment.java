package com.example.yad2application;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yad2application.ProductModel.Product;
import com.example.yad2application.ProductModel.ProductModel;
import com.example.yad2application.databinding.FragmentProductsListBinding;

import java.util.LinkedList;
import java.util.List;

public class ProductsListFragment extends Fragment {

    List<Product> data = new LinkedList<>();
    ProductRecyclerAdapter adapter;
    FragmentProductsListBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView nameTv;
        TextView idTv;
        CheckBox cb;
        public ProductViewHolder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.studentlistrow_name_tv);
            idTv = itemView.findViewById(R.id.studentlistrow_id_tv);
            cb = itemView.findViewById(R.id.studentlistrow_cb);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int)cb.getTag();
                    Product pro = data.get(pos);
                    pro.cb = cb.isChecked();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Toast.makeText(getContext(),pos,Toast.LENGTH_LONG).show();
                    listener.onItemClick(pos);
                }
            });
        }

        public void bind(Product prod,int pos) {
            nameTv.setText(prod.name);
            idTv.setText(prod.category);
            cb.setChecked(prod.cb);
            cb.setTag(pos);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int pos);
    }
    public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductViewHolder>{
        OnItemClickListener listener;
        void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
        LayoutInflater inflater;
        List<Product> data;
        public void setData(List<Product> data){
            this.data = data;
            notifyDataSetChanged();
        }

        public ProductRecyclerAdapter(LayoutInflater inflater, List<Product> data) {
            this.inflater = inflater;
            this.data = data;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.products_list_row,parent,false);
            return new ProductViewHolder(view,listener);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product prod = data.get(position);
            holder.bind(prod,position);

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductsListBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        binding.productsrecyclerList.setHasFixedSize(true);
        binding.productsrecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        ProductModel.instance().getAllProducts((list)->{
            data = list;
            adapter.setData(data);
        });

        adapter = new ProductRecyclerAdapter(inflater,data);
        binding.productsrecyclerList.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Log.d("TAG","Row was clicked "  + pos);
            }
        });
        return view;
    }
}
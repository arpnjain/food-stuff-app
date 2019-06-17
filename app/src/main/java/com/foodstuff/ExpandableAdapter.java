package com.foodstuff;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.PrimitiveIterator;

public class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.MyViewHolder> {
    private Context context;
    private List<FoodDataGet> list;
    private BreakfastItemClickListener breakfastItemClickListener;

    public ExpandableAdapter(Context context, List<FoodDataGet> list) {
        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name, kcal, carbs, fat, protein;
        public Button delete;
        public MyViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.name_food_get);
            name.setTextSize(16);
            kcal = view.findViewById(R.id.kcal_food_get);
            kcal.setTextSize(12);
            carbs = view.findViewById(R.id.carbs_food_get);
            carbs.setTextSize(12);
            fat = view.findViewById(R.id.fat_food_get);
            fat.setTextSize(12);
            protein = view.findViewById(R.id.protein_food_get);
            protein.setTextSize(12);
            delete = view.findViewById(R.id.button_food_get);
            view.setTag(view);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(breakfastItemClickListener !=null) breakfastItemClickListener.onClick(view,getAdapterPosition());
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_get_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,  int position) {
        FoodDataGet foodDataGet = list.get(position);
        holder.name.setText(foodDataGet.getFood_name());
        holder.kcal.setText(foodDataGet.getFood_kcal() + " : Kcal");
        holder.carbs.setText(foodDataGet.getFood_carbs() + " : Carbs");
        holder.fat.setText(foodDataGet.getFood_fat() + " : Fat");
        holder.protein.setText(foodDataGet.getFood_protein() + " : Protein");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setBreakfastItemClickListener(BreakfastItemClickListener breakfastItemClickListener){
        this.breakfastItemClickListener=breakfastItemClickListener;
    }
}

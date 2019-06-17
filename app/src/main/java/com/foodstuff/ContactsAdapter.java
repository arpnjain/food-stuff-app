package com.foodstuff;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.joooonho.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<FoodData> nameList;
    private List<FoodData> nameListFiltered;
    private ItemClickListener clickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, kcal, carbs, fat, protein;
        public SelectableRoundedImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_food);
            name.setTextSize(16);
            kcal = view.findViewById(R.id.kcal_food);
            kcal.setTextSize(12);
            carbs = view.findViewById(R.id.carbs_food);
            carbs.setTextSize(12);
            fat = view.findViewById(R.id.fat_food);
            fat.setTextSize(12);
            protein = view.findViewById(R.id.protein_food);
            protein.setTextSize(12);
            thumbnail = view.findViewById(R.id.thumbnail_food);
            thumbnail.setCornerRadiiDP(4, 4, 0, 0);
            view.setTag(view);
            view.setOnClickListener(this);



        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }


    public ContactsAdapter(Context context, List<FoodData> nameList) {
        this.context = context;
        this.nameList = nameList;
        this.nameListFiltered = nameList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final FoodData name = nameListFiltered.get(position);
        holder.name.setText(name.getName());
        holder.kcal.setText(name.getKcal() + " : Kcal");
        holder.carbs.setText(name.getCarbs() + " : Carbs");
        holder.fat.setText(name.getFat() + " : Fat");
        holder.protein.setText(name.getProtein() + " : Protein");

        Glide.with(context)
                .load(name.getImage())
                .into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return nameListFiltered.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    nameListFiltered = nameList;
                } else {
                    List<FoodData> filteredList = new ArrayList<>();
                    for (FoodData row : nameList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getKcal().contains(charSequence) || row.getCarbs().contains(charSequence)
                                || row.getFat().contains(charSequence) || row.getProtein().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    nameListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = nameListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                nameListFiltered = (ArrayList<FoodData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}

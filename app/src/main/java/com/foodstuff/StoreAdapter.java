package com.foodstuff;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.joooonho.SelectableRoundedImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
private Context context;
private List<Item> blogsList_item;
private HomeItemClickListener homeItemClickListener;

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView name, Header, date;
    public SelectableRoundedImageView thumbnail;

    public MyViewHolder(View view) {
        super(view);
        Header=view.findViewById(R.id.header);
        Header.setTextSize(16);
        name = view.findViewById(R.id.title);
        name.setTextSize(16);
        date=view.findViewById(R.id.pub_date);
        date.setTextSize(16);
        thumbnail = view.findViewById(R.id.thumbnail);
        thumbnail.setCornerRadiiDP(4, 4, 4, 4);
        view.setTag(view);
        view.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
      if(homeItemClickListener !=null) homeItemClickListener.onClick(view,getAdapterPosition());
    }
}


    public StoreAdapter(Context context, List<Item> blogsList_item) {
        this.context = context;
        this.blogsList_item = blogsList_item;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.home_item_row, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nullable MyViewHolder holder, final int position) {
        final Item blogs = blogsList_item.get(position);
        holder.name.setText(blogs.getTitle());
        List<String> labels= blogs.getLabels();
        holder.Header.setText(labels.toString().replace("[","").replace("]",""));
        String start_dt=blogs.getPublished().substring(0,10);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // Existing Pattern
        Date getStartDt =null; //Returns Date Format according to existing pattern
        try {
            getStartDt = (Date) formatter.parse(start_dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");// New Pattern
        String formattedDate = simpleDateFormat.format(getStartDt);
        holder.date.setText(formattedDate);
        Document document= Jsoup.parse(blogs.getContent());
        Elements elements = document.select("img");
        Glide.with(context)
                .load(elements.get(0).attr("src"))
                .into(holder.thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostWebView.class);
                intent.putExtra("url", blogs.getUrl());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return blogsList_item.size();
    }

    public void setHomeItemClickListener(HomeItemClickListener homeItemClickListener){
    this.homeItemClickListener=homeItemClickListener;
    }
}
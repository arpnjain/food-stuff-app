package com.foodstuff;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class FoodSearchRest extends AppCompatActivity implements ItemClickListener {
    private static final String TAG = FoodSearchRest.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<FoodData> nameList=new ArrayList<>();
    private ContactsAdapter mAdapter;
    private SearchView searchView;
    private android.support.v7.widget.Toolbar toolbar;
    private GridLayoutManager mLayoutManager;
    String [] dailyActivity;
    private DatabaseReference UserRef;
    private FirebaseDatabase databaseRef;
    private FirebaseAuth auth;
    String userID;


    // url to fetch contacts json
    private static final String URL = "http://api.myjson.com/bins/17a4t6/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search_rest);
        toolbar = findViewById(R.id.toolbar_Foods_search);
        toolbar.setTitle("Search");
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
       getSupportActionBar().setDisplayShowHomeEnabled(true);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth=FirebaseAuth.getInstance();
        userID=auth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance();
        UserRef= databaseRef.getReference().child("Daily Foods").child(userID);


        recyclerView = findViewById(R.id.recycler_view_food);
        mLayoutManager = new GridLayoutManager(this,1);
        mAdapter = new ContactsAdapter(this, nameList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        fetchName();


    }

    @Override
    public void onClick(View view, int position) {
        final FoodData name = nameList.get(position);
        Calendar calendar = Calendar.getInstance();
        String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(calendar.getTime());
        dailyActivity= new String [] {"Breakfast","Lunch","Dinner","Snacks/Other"};
        AlertDialog.Builder mBuilder= new AlertDialog.Builder(FoodSearchRest.this);
        mBuilder.setTitle("Choose an Item");
        mBuilder.setIcon(R.drawable.ic_daily_act);
        mBuilder.setSingleChoiceItems(dailyActivity, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               String food_activity=String.valueOf(dailyActivity[i]);
                String key=UserRef.push().getKey();
                switch (food_activity){

                    case "Breakfast":
                        UserRef.child(currentDateString).child("Breakfast").child(key).child("food_name").setValue(String.valueOf(name.getName()));
                        UserRef.child(currentDateString).child("Breakfast").child(key).child("food_kcal").setValue(String.valueOf(name.getKcal()));
                        UserRef.child(currentDateString).child("Breakfast").child(key).child("food_carbs").setValue(String.valueOf(name.getCarbs()));
                        UserRef.child(currentDateString).child("Breakfast").child(key).child("food_fat").setValue(String.valueOf(name.getFat()));
                        UserRef.child(currentDateString).child("Breakfast").child(key).child("food_protein").setValue(String.valueOf(name.getProtein()));
                        Toast.makeText(getApplicationContext(), name.getName() + " successfully added to breakfast", Toast.LENGTH_SHORT).show();
                        break;
                    case "Lunch":
                        UserRef.child(currentDateString).child("Lunch").child(key).child("food_name").setValue(String.valueOf(name.getName()));
                        UserRef.child(currentDateString).child("Lunch").child(key).child("food_kcal").setValue(String.valueOf(name.getKcal()));
                        UserRef.child(currentDateString).child("Lunch").child(key).child("food_carbs").setValue(String.valueOf(name.getCarbs()));
                        UserRef.child(currentDateString).child("Lunch").child(key).child("food_fat").setValue(String.valueOf(name.getFat()));
                        UserRef.child(currentDateString).child("Lunch").child(key).child("food_protein").setValue(String.valueOf(name.getProtein()));
                        Toast.makeText(getApplicationContext(), name.getName() + " successfully added to Lunch", Toast.LENGTH_SHORT).show();
                        break;
                    case "Dinner":

                        UserRef.child(currentDateString).child("Dinner").child(key).child("food_name").setValue(String.valueOf(name.getName()));
                        UserRef.child(currentDateString).child("Dinner").child(key).child("food_kcal").setValue(String.valueOf(name.getKcal()));
                        UserRef.child(currentDateString).child("Dinner").child(key).child("food_carbs").setValue(String.valueOf(name.getCarbs()));
                        UserRef.child(currentDateString).child("Dinner").child(key).child("food_fat").setValue(String.valueOf(name.getFat()));
                        UserRef.child(currentDateString).child("Dinner").child(key).child("food_protein").setValue(String.valueOf(name.getProtein()));
                        Toast.makeText(getApplicationContext(), name.getName() + " successfully added to Dinner", Toast.LENGTH_SHORT).show();
                        break;
                    case "Snacks/Other":
                        UserRef.child(currentDateString).child("Snacks&Other").child(key).child("food_name").setValue(String.valueOf(name.getName()));
                        UserRef.child(currentDateString).child("Snacks&Other").child(key).child("food_kcal").setValue(String.valueOf(name.getKcal()));
                        UserRef.child(currentDateString).child("Snacks&Other").child(key).child("food_carbs").setValue(String.valueOf(name.getCarbs()));
                        UserRef.child(currentDateString).child("Snacks&Other").child(key).child("food_fat").setValue(String.valueOf(name.getFat()));
                        UserRef.child(currentDateString).child("Snacks&Other").child(key).child("food_protein").setValue(String.valueOf(name.getProtein()));
                        Toast.makeText(getApplicationContext(), name.getName() + " successfully added to Snacks/Other", Toast.LENGTH_SHORT).show();
                        break;
                    default:


                }

                dialogInterface.dismiss();

            }
        });

        mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog mDialog= mBuilder.create();
        mDialog.show();

    }


    /**
     * fetches json by making http calls
     */
    private void fetchName() {
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<FoodData> items = new Gson().fromJson(response.toString(), new TypeToken<List<FoodData>>() {
                        }.getType());

                        // adding contacts to contacts list
                        nameList.clear();
                        nameList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {


        private int spacing;


        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {

            this.spacing = spacing;

        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int itemCount = state.getItemCount();
            outRect.right = spacing;
            outRect.left = spacing;
            outRect.top = spacing;
            outRect.bottom = position == itemCount - 1 ? spacing : 0;
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_food, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search_food)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search_food) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


}


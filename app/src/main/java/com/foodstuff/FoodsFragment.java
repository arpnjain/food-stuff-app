package com.foodstuff;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class FoodsFragment extends Fragment implements BreakfastItemClickListener, LunchItemClickListener,DinnerItemClickListener, SnacksItemClickListener  {

    private RecyclerView breakfastListView, lunchListView, dinnerListView, snacksListView;
    private List<FoodDataGet>  list = new ArrayList<>();
    private List<FoodDataGet>  list_lunch = new ArrayList<>();
    private List<FoodDataGet>  list_dinner = new ArrayList<>();
    private List<FoodDataGet>  list_snacks = new ArrayList<>();
    private ExpandableAdapter mAdapter;
    private LunchAdapter nAdapter;
    private DinnerAdapter oAdapter;
    private SnacksAdapter pAdapter;
    private SpinKitView progress;
    FoodDataGet foodDataGet=new FoodDataGet();

    private LinearLayoutManager mLayoutManager,nLayoutManager,oLayoutManager,pLayoutManager;
    private DatabaseReference UserRef, NewRef;
    private FirebaseDatabase databaseRef;
    private FirebaseAuth auth;
    String userID;
    public static String currentDateString="";



    TextView breakfast, lunch, dinner, snacks, getBreakfast,getLunch,getDinner,getSnacks;
    EditText datepick;

    static View view;

    public FoodsFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_foods, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_Foods);
        toolbar.setTitle("Food Dairy");
        toolbar.inflateMenu(R.menu.menu_main_search);
        Menu menu = toolbar.getMenu();

        setHasOptionsMenu(true);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_search:
                        Log.i("Menu item Selected", "Settings");
                        Intent intent = new Intent(getActivity(), FoodSearchRest.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
        auth=FirebaseAuth.getInstance();
        userID=auth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance();
        UserRef= databaseRef.getReference().child("Daily Foods").child(userID);
        NewRef=databaseRef.getReference();

        datepick=view.findViewById(R.id.date_set_food);
        Calendar calendar = Calendar.getInstance();
        currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(calendar.getTime());
        datepick.setText(currentDateString);
        datepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new SelectDateFragment();
                datePicker.show(getFragmentManager(),"date Picker");
                currentDateString=datepick.getText().toString();
            }
        });

        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getBreakfast = view.findViewById(R.id.breakfast_kcal);
        getLunch = view.findViewById(R.id.lunch_kcal);
        getDinner = view.findViewById(R.id.dinner_kcal);
        getSnacks = view.findViewById(R.id.snacks_kcal);


        breakfast = view.findViewById(R.id.breakfastText);
        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),FoodSearchRest.class);
                startActivity(intent);
            }
        });

        lunch = view.findViewById(R.id.lunchText);
        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),FoodSearchRest.class);
                startActivity(intent);
            }
        });

        dinner = view.findViewById(R.id.dinnerText);
        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),FoodSearchRest.class);
                startActivity(intent);            }
        });

        snacks = view.findViewById(R.id.snacksText);
        snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),FoodSearchRest.class);
                startActivity(intent);
            }
        });
        progress =(SpinKitView) view.findViewById(R.id.spin_kit_food);
        breakfastListView = view.findViewById(R.id.breakfastList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        breakfastListView.setLayoutManager(mLayoutManager);
        mAdapter = new ExpandableAdapter(getActivity(), list);
        breakfastListView.setAdapter(mAdapter);
        breakfastListView.setItemAnimator(new DefaultItemAnimator());
        breakfastListView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        breakfastListView.setNestedScrollingEnabled(true);

        lunchListView = view.findViewById(R.id.lunchList);
        nLayoutManager = new LinearLayoutManager(getActivity());
        lunchListView.setLayoutManager(nLayoutManager);
        nAdapter = new LunchAdapter(getActivity(), list_lunch);
        lunchListView.setAdapter(nAdapter);
        lunchListView.setItemAnimator(new DefaultItemAnimator());
        lunchListView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        lunchListView.setNestedScrollingEnabled(true);

        dinnerListView = view.findViewById(R.id.dinnerList);
        oLayoutManager = new LinearLayoutManager(getActivity());
        dinnerListView.setLayoutManager(oLayoutManager);
        oAdapter = new DinnerAdapter(getActivity(), list_dinner);
        dinnerListView.setAdapter(oAdapter);
        dinnerListView.setItemAnimator(new DefaultItemAnimator());
        dinnerListView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        dinnerListView.setNestedScrollingEnabled(true);

        snacksListView = view.findViewById(R.id.snacksList);
        pLayoutManager = new LinearLayoutManager(getActivity());
        snacksListView.setLayoutManager(pLayoutManager);
        pAdapter=new SnacksAdapter(getActivity(),list_snacks);
        snacksListView.setAdapter(pAdapter);
        snacksListView.setItemAnimator(new DefaultItemAnimator());
        snacksListView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        snacksListView.setNestedScrollingEnabled(true);

        UserRef.child("Daily Foods").child("Total KCal Target").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("Total needed Kcal")){
                        String req_kcal = dataSnapshot.child("Total needed Kcal").getValue().toString();
                        int value=((Integer.parseInt(req_kcal))*30)/100;
                        int value1=((Integer.parseInt(req_kcal))*10)/100;
                        getBreakfast.setText("0 of "+String.valueOf(value)+" Kcal");
                        getLunch.setText("0 of "+String.valueOf(value)+" Kcal");
                        getDinner.setText("0 of "+String.valueOf(value)+" Kcal");
                        getSnacks.setText("0 of "+String.valueOf(value1)+" Kcal");



                    }
                }
                else {
                    Log.i("database","isnotexist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        GetDataBreakfast();
        GetDataLunch();
        GetDataDinner();
        GetDataSnacks();

        return view;

    }

    private void GetDataSnacks() {
        UserRef.child(currentDateString).child("Total Intake").child("In snacks").setValue(String.valueOf("0"));
        UserRef.child(currentDateString).child("Total Intake").child("In lunch").setValue(String.valueOf("0"));
        UserRef.child(currentDateString).child("Total Intake").child("In dinner").setValue(String.valueOf("0"));
        UserRef.child(currentDateString).child("Total Intake").child("In breakfast").setValue(String.valueOf("0"));


        UserRef.child(currentDateString).child("Snacks&Other").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    FoodDataGet foodDataGet=new FoodDataGet();
                    foodDataGet = dataSnapshot.getValue(FoodDataGet.class);
                    list_snacks.add(foodDataGet);

                     int total_kcal=0;
                    for (int i = 0; i<list_snacks.size(); i++)
                    {
                      try {
                          if(list_snacks.get(i).getFood_kcal()!=null)
                          total_kcal += Integer.parseInt(list_snacks.get(i).getFood_kcal());
                          else {
                              total_kcal=0;
                          }
                      }catch (NumberFormatException e){

                          e.printStackTrace();
                      }

                    }
                    int finalTotal_kcal = total_kcal;
                    UserRef.child(currentDateString).child("Total Intake").child("In snacks").setValue(String.valueOf(finalTotal_kcal));
                    NewRef.child("Daily Foods").child("Total KCal Target").child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                if (dataSnapshot.hasChild("Total needed Kcal")){
                                    String req_kcal = dataSnapshot.child("Total needed Kcal").getValue().toString();
                                    int value=((Integer.parseInt(req_kcal))*10)/100;
                                    getSnacks.setText(String.valueOf(finalTotal_kcal)+" of "+String.valueOf(value)+" Kcal");

                                }
                            }
                            else {
                                Log.i("database","isnotexist");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    pAdapter.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("Error", "onCancelled", databaseError.toException());
                    Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();


                }
            });

        progress.setVisibility(View.INVISIBLE);
    }

    private void GetDataDinner() {

            UserRef.child(currentDateString).child("Dinner").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    progress.setVisibility(View.VISIBLE);
                    FoodDataGet foodDataGet = new FoodDataGet();
                    foodDataGet = dataSnapshot.getValue(FoodDataGet.class);
                    list_dinner.add(foodDataGet);
                    int total_kcal=0;
                    for (int i = 0; i<list_dinner.size(); i++)
                    {
                        try {
                            if(list_dinner.get(i).getFood_kcal()!=null)
                                total_kcal += Integer.parseInt(list_dinner.get(i).getFood_kcal());
                            else {
                                total_kcal=0;
                            }
                        }catch (NumberFormatException e){

                            e.printStackTrace();
                        }

                    }
                    int finalTotal_kcal = total_kcal;
                    UserRef.child(currentDateString).child("Total Intake").child("In dinner").setValue(String.valueOf(finalTotal_kcal));
                    NewRef.child("Daily Foods").child("Total KCal Target").child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                if (dataSnapshot.hasChild("Total needed Kcal")){
                                    String req_kcal = dataSnapshot.child("Total needed Kcal").getValue().toString();
                                    int value=((Integer.parseInt(req_kcal))*30)/100;
                                    getDinner.setText(String.valueOf(finalTotal_kcal)+" of "+String.valueOf(value)+" Kcal");

                                }
                            }
                            else {
                                Log.i("database","isnotexist");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    oAdapter.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("Error", "onCancelled", databaseError.toException());
                    Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
                }

            });
        progress.setVisibility(View.INVISIBLE);
    }

    private void GetDataLunch() {


            UserRef.child(currentDateString).child("Lunch").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    progress.setVisibility(View.VISIBLE);
                    FoodDataGet foodDataGet = new FoodDataGet();
                    foodDataGet = dataSnapshot.getValue(FoodDataGet.class);
                    list_lunch.add(foodDataGet);
                    int total_kcal=0;
                    for (int i = 0; i<list_lunch.size(); i++)
                    {
                        try {
                            if(list_lunch.get(i).getFood_kcal()!=null)
                                total_kcal += Integer.parseInt(list_lunch.get(i).getFood_kcal());
                            else {
                                total_kcal=0;
                            }
                        }catch (NumberFormatException e){

                            e.printStackTrace();
                        }

                    }
                    int finalTotal_kcal = total_kcal;
                    UserRef.child(currentDateString).child("Total Intake").child("In lunch").setValue(String.valueOf(finalTotal_kcal));
                    NewRef.child("Daily Foods").child("Total KCal Target").child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                if (dataSnapshot.hasChild("Total needed Kcal")){
                                    String req_kcal = dataSnapshot.child("Total needed Kcal").getValue().toString();
                                    int value=((Integer.parseInt(req_kcal))*30)/100;
                                    getLunch.setText(String.valueOf(finalTotal_kcal)+" of "+String.valueOf(value)+" Kcal");

                                }
                            }
                            else {
                                Log.i("database","isnotexist");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    nAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("Error", "onCancelled", databaseError.toException());
                    Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
                }
            });
        progress.setVisibility(View.INVISIBLE);
    }


   private void GetDataBreakfast(){



           UserRef.child(currentDateString).child("Breakfast").addChildEventListener(new ChildEventListener() {
               @Override
               public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                   progress.setVisibility(View.VISIBLE);
                   FoodDataGet foodDataGet = new FoodDataGet();
                   foodDataGet = dataSnapshot.getValue(FoodDataGet.class);
                   list.add(foodDataGet);
                   int total_kcal=0;
                   for (int i = 0; i<list.size(); i++)
                   {
                       try {
                           if(list.get(i).getFood_kcal()!=null)
                               total_kcal += Integer.parseInt(list.get(i).getFood_kcal());
                           else {
                               total_kcal=0;
                           }
                       }catch (NumberFormatException e){

                           e.printStackTrace();
                       }

                   }

                   int finalTotal_kcal = total_kcal;
                   UserRef.child(currentDateString).child("Total Intake").child("In breakfast").setValue(String.valueOf(finalTotal_kcal));
                   NewRef.child("Daily Foods").child("Total KCal Target").child(userID).addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           if (dataSnapshot.exists()){
                               if (dataSnapshot.hasChild("Total needed Kcal")){
                                   String req_kcal = dataSnapshot.child("Total needed Kcal").getValue().toString();
                                   int value=((Integer.parseInt(req_kcal))*30)/100;
                                   getBreakfast.setText(String.valueOf(finalTotal_kcal)+" of "+String.valueOf(value)+" Kcal");

                               }
                           }
                           else {
                               Log.i("database","isnotexist");
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });


                   mAdapter.notifyDataSetChanged();


               }

               @Override
               public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

               }

               @Override
               public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

               }

               @Override
               public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                   Log.w("Error", "onCancelled", databaseError.toException());
                   Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();

               }
           });
       progress.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View view, int position) {
        FoodDataGet foodDataGet = list.get(position);
        FoodDataGet foodDataGet1 = list_dinner.get(position);
        FoodDataGet foodDataGet2 = list_lunch.get(position);
        FoodDataGet foodDataGet3 = list_snacks.get(position);

    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener  {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            Calendar c= Calendar.getInstance();
            c.set(Calendar.YEAR, i);
            c.set(Calendar.MONTH, i1);
            c.set(Calendar.DAY_OF_MONTH, i2);
            String currentDate = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
            EditText textInputEditText=(EditText)view.findViewById(R.id.date_set_food);
            textInputEditText.setText(currentDate);
        }
    }
}



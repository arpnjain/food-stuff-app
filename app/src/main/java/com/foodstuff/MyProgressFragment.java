package com.foodstuff;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.collect.Range;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.select.Evaluator;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static com.foodstuff.FoodsFragment.currentDateString;

public class MyProgressFragment extends Fragment  {
    private FirebaseAuth.AuthStateListener authListener;

    private DatabaseReference UserRef;

    private FirebaseAuth auth;
    private int sum=0;
    private TextView calEatean, calTarget, calBurn;
    TextView Weight_process,Start_weight_process,Goal_weight_process,current_weight,goal_weight;
    PieChart pieChart,pieChart_goal;
    LinearLayout linearLayout;
    String uId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_progress, container, false);
        auth = FirebaseAuth.getInstance();



        calEatean = view.findViewById(R.id.calEatean);

        calBurn = view.findViewById(R.id.calBurn);

        calTarget = view.findViewById(R.id.calTarget);

        Weight_process = view.findViewById(R.id.weight_set);
        Start_weight_process = view.findViewById(R.id.start_wght);
        Goal_weight_process = view.findViewById(R.id.goal_wght);
        current_weight = view.findViewById(R.id.current_prog);
        goal_weight = view.findViewById(R.id.weight_prog);
        linearLayout=view.findViewById(R.id.linearLayout);


        pieChart = (PieChart)view.findViewById(R.id.idPieChart);
        pieChart_goal = (PieChart)view.findViewById(R.id.idPieChart_goal);

        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setCenterText("Intake Report");
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextSize(20f);


        ArrayList<PieEntry> yValue= new ArrayList<>();
        yValue.add(new PieEntry(0,"FAT"));
        yValue.add(new PieEntry(12,"CARBS"));
        yValue.add(new PieEntry(50,"PROTEIN"));

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet pieDataSet=new PieDataSet(yValue,"Intake Calroies in gram");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData=new PieData(pieDataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.BLUE);
        pieChart.setData(pieData);

        pieChart.invalidate();

        pieChart_goal.getDescription().setEnabled(false);
        pieChart_goal.setExtraOffsets(5,10,5,5);
        pieChart_goal.setDragDecelerationFrictionCoef(0.99f);
        pieChart_goal.setDrawHoleEnabled(true);
        pieChart_goal.setHoleColor(Color.WHITE);
        pieChart_goal.setTransparentCircleRadius(60f);
        pieChart_goal.setCenterText("Goal Intake Report");
        pieChart_goal.setCenterTextColor(Color.BLACK);
        pieChart_goal.setCenterTextSize(20f);

        ArrayList<PieEntry> yValue_goal= new ArrayList<>();
        yValue_goal.add(new PieEntry(20f,"FAT"));
        yValue_goal.add(new PieEntry(40,"CARBS"));
        yValue_goal.add(new PieEntry(60f,"PROTEIN"));

        pieChart_goal.animateY(1000, Easing.EasingOption.EaseInOutBounce);

        PieDataSet pieDataSet1=new PieDataSet(yValue_goal,"Goal Intake Calroies in gram");
        pieDataSet1.setSliceSpace(3f);
        pieDataSet1.setSelectionShift(5f);
        pieDataSet1.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData1=new PieData(pieDataSet1);
        pieData1.setValueTextSize(12f);
        pieData1.setValueTextColor(Color.MAGENTA);
        pieChart_goal.setData(pieData1);

        pieChart_goal.invalidate();


        Calendar calendar = Calendar.getInstance();
        String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(calendar.getTime());


        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();

        UserRef = FirebaseDatabase.getInstance().getReference();
        UserRef.child("Daily Foods").child(uId).child(currentDateString).child("Total Intake").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("In breakfast")&& dataSnapshot.hasChild("In lunch")){
                        String value_brk = dataSnapshot.child("In breakfast").getValue().toString();
                        String value_dinr = dataSnapshot.child("In lunch").getValue().toString();
                        String value_lunch = dataSnapshot.child("In dinner").getValue().toString();
                        String value_snacks= dataSnapshot.child("In snacks").getValue().toString();

                        sum=(int)(Integer.parseInt(value_brk)+Integer.parseInt(value_dinr)+
                                Integer.parseInt(value_lunch)+Integer.parseInt(value_snacks));


                        calEatean.setText(String.valueOf(sum)+" KCAL EATEN");
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

        UserRef.child("Users").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("BMR")&& dataSnapshot.hasChild("Burn Kcal")) {
                        String total_kcal = dataSnapshot.child("BMR").getValue().toString();
                        calTarget.setText(total_kcal+" KCAL PER DAY");
                        String burn_cal = dataSnapshot.child("Burn Kcal").getValue().toString();
                        calBurn.setText("0 / "+burn_cal+" KCAL BURNED");

                        UserRef.child("Daily Foods").child("Total KCal Target").child(uId).child("Total needed Kcal").setValue(total_kcal);
                        UserRef.child("Daily Foods").child("Total KCal Target").child(uId).child("Essential Burn Kcal").setValue(burn_cal);


                        UserRef.child("Daily Foods").child("Weight Goal").child(uId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    if(dataSnapshot.hasChild("Selected Target")){
                                        String select_spin=dataSnapshot.child("Selected Target").getValue().toString();
                                        int T_kcal=Integer.parseInt(total_kcal);
                                        int B_kcal=Integer.parseInt(burn_cal);
                                        if(select_spin.equalsIgnoreCase("Easy 0.25 Kg per week")){
                                          T_kcal=  T_kcal-275;
                                          B_kcal= B_kcal-55;
                                          calTarget.setText(String.valueOf(T_kcal)+" KCAL PER DAY");
                                          calBurn.setText("0 / "+String.valueOf((B_kcal))+" KCAL BURNED");

                                        }

                                        else {
                                            T_kcal=  T_kcal-550;
                                            B_kcal= B_kcal-110;
                                            calTarget.setText(String.valueOf(T_kcal)+" KCAL PER DAY");
                                            calBurn.setText("0 / "+String.valueOf((B_kcal))+" KCAL BURNED");
                                        }

                                        String final_target_kcal=String.valueOf(T_kcal);
                                        String final_burn_kcal=String.valueOf(B_kcal);
                                        UserRef.child("Daily Foods").child("Total KCal Target").child(uId).child("Total needed Kcal").setValue(final_target_kcal);
                                        UserRef.child("Daily Foods").child("Total KCal Target").child(uId).child("Essential Burn Kcal").setValue(final_burn_kcal);




                                    }
                                    else {
                                        Log.i("database","isnotexist");
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    else
                    {
                        Log.i("database","isnotexist");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    Intent mainIntent = new Intent(getActivity(), SignupActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    getActivity().finish();

                }
            }
        };
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarProgress);
        toolbar.setTitle("My Progress");

        toolbar.inflateMenu(R.menu.main_menu);
        Menu menu = toolbar.getMenu();

        setHasOptionsMenu(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                   // case R.id.settings:
                     //   Log.i("Menu item Selected", "Settings");
                       // return true;
                    case R.id.help:
                        Intent Email = new Intent(Intent.ACTION_SEND);
                        Email.setType("text/email");
                        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"sdcetfoodstuff2019@gmail.com"});
                        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        Email.putExtra(Intent.EXTRA_TEXT, "User Email Id: ");
                        startActivity(Intent.createChooser(Email, "Send Feedback:"));
                        Log.i("Menu item Selected", "Help");
                        return true;
                    case R.id.logout:
                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Warning!")
                                .setMessage("Are you sure you want to Logout ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.i("Menu item Selected", "Log Out");
                                        signOut();

                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        return true;
                }
                return false;
            }
        });

        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        UserRef.child("Daily Foods").child("Weight Goal").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    linearLayout.setVisibility(View.VISIBLE);
                    Weight_process.setTypeface(Typeface.DEFAULT_BOLD);
                    Weight_process.setTextSize(20f);
                    if (dataSnapshot.hasChild("current_weight") &&dataSnapshot.hasChild("goal_weight")) {
                        String Curweight = dataSnapshot.child("current_weight").getValue().toString();
                        current_weight.setText(Curweight+" Kg");
                        current_weight.setTextSize(15f);
                        current_weight.setTypeface(Typeface.DEFAULT_BOLD);
                        String Gurweight = dataSnapshot.child("goal_weight").getValue().toString();
                        goal_weight.setText(Gurweight+" Kg");
                        goal_weight.setTextSize(15f);
                        goal_weight.setTypeface(Typeface.DEFAULT_BOLD);
                        String Curdate = dataSnapshot.child("Current Weight Date").getValue().toString();
                        Start_weight_process.setText("Start Weight on "+Curdate);
                        String goaldate = dataSnapshot.child("Goal Weight Date").getValue().toString();
                        Goal_weight_process.setText("Target Weight on "+goaldate);
                    }
                    else
                    {
                        Log.i("database","isnotexist");
                    }
                }
                else {
                    Weight_process.setVisibility(View.INVISIBLE);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }




    private void signOut() {
        auth.signOut();
        Intent mainIntent = new Intent(getActivity(), SignupActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        getActivity().finish();


    }


}

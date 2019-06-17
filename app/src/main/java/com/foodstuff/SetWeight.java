package com.foodstuff;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Range;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SetWeight extends AppCompatActivity  {
    android.support.v7.widget.Toolbar toolbar;
    private FirebaseAuth mauth;
    private DatabaseReference UserRef, UserRefSec;
    String currentuserID ,userID;
    private AwesomeValidation awesomeValidation;
    private Button SaveInformation, ResetInfo;
    private Spinner spinner;
    EditText current_weight, goal_weight;
    TextView bmi_value,ideal_weight,lose_weight,reach_weight;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_weight);
        toolbar=findViewById(R.id.Toolbar_setgoal);
        toolbar.setTitle("Goal Progress");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mauth=FirebaseAuth.getInstance();
        userID=mauth.getCurrentUser().getUid();
        UserRefSec= FirebaseDatabase.getInstance().getReference().child("Daily Foods").child("Weight Goal").child(userID);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuserID=user.getUid();
        UserRef= FirebaseDatabase.getInstance().getReference();


        SaveInformation=(Button)findViewById(R.id.button_commit_goal);
        ResetInfo=(Button) findViewById(R.id.button_reset_goal);
        current_weight=(EditText)findViewById(R.id.currentweight);
        goal_weight=(EditText)findViewById(R.id.goalweight);
        bmi_value=(TextView)findViewById(R.id.value_bmi);
        ideal_weight=(TextView)findViewById(R.id.idea_value);
        lose_weight=(TextView)findViewById(R.id.lose_weight_value);
        reach_weight=(TextView)findViewById(R.id.reach_weight_goal);


        spinner=findViewById(R.id.spinner_weightcal);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.weightCalculator, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text= (String) adapterView.getItemAtPosition(i);
                String str1 = current_weight.getText().toString();
                String str2 = goal_weight.getText().toString();

                if(TextUtils.isEmpty(str1)){
                    current_weight.setError("Please enter your weight");
                    current_weight.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(str2)){
                    goal_weight.setError("Please enter your weight");
                    goal_weight.requestFocus();
                    return;
                }
                float Current_weight=Float.parseFloat(str1);
                float Goal_weight=Float.parseFloat(str2);
                float result= Math.abs(CalculatorWeight(Current_weight,Goal_weight));
                switch (text){
                    case "Easy 0.25 Kg per week":
                        String M=CalculateMonths(result);
                        String D=CalculateDays(result);
                        reach_weight.setText("You will reach your goal before "+M+" months "+D+" days");
                        break;
                    case "Medium 0.50 Kg per week":
                        String M1=CalculateMonthsOne(result);
                        String D1=CalculateDaysOne(result);
                        reach_weight.setText("You will reach your goal before "+M1+" months "+D1+" days");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ideal_weight.setText(getResources().getString(R.string.idea_weight_range)+ "\n 54-66 Kg");
        awesomeValidation.addValidation(this, R.id.currentweight, Range.closed(30f, 450.0f), R.string.weighterror);
        awesomeValidation.addValidation(this, R.id.goalweight, Range.closed(30f, 450.0f), R.string.weighterror);
        loadingBar= new ProgressDialog(this);





        goal_weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str1 = current_weight.getText().toString();
                String str2 = goal_weight.getText().toString();

                if(TextUtils.isEmpty(str1)){
                    current_weight.setError("Please enter your weight");
                    current_weight.requestFocus();
                    return;

                }

                if(TextUtils.isEmpty(str2)){
                    goal_weight.setError("Please enter your weight");
                    goal_weight.requestFocus();
                    return;

                }
                float Current_weight=Float.parseFloat(str1);
                float Goal_weight=Float.parseFloat(str2);
                float result= Math.abs(CalculatorWeight(Current_weight,Goal_weight));
                String result_str= String.format("%.1f",result);
                if(Current_weight > Goal_weight){
                lose_weight.setText("How Quickly do you want to lose "+result_str+" Kg ?");

                }
                if(Current_weight < Goal_weight){
                    goal_weight.setError("Please put Target Weight Lower than Current Weight ");
                    goal_weight.requestFocus();
                }
                else{
                    Log.i("result weight", "0 Kg");

                }


            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        UserRef.child("Users").child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("Bmi")&& dataSnapshot.hasChild("BMR") ) {
                        String bmi_set = dataSnapshot.child("Bmi").getValue().toString();
                        bmi_value.setText("Your BMI :"+bmi_set);
                        String wght=dataSnapshot.child("Weight").getValue().toString();
                        current_weight.setText(wght);
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
        UserRefSec.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("current_weight") &&dataSnapshot.hasChild("goal_weight")) {
                        String Curweight = dataSnapshot.child("current_weight").getValue().toString();
                        current_weight.setText(Curweight);
                        String Gurweight = dataSnapshot.child("goal_weight").getValue().toString();
                        goal_weight.setText(Gurweight);
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



        SaveInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == SaveInformation) {
                    submitForm();
                }
            }
        });

        ResetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userID != null){
                    UserRefSec.removeValue(null);
                    Toast.makeText(SetWeight.this,"Your Data is reset successfully",Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i("Reset","Unsuccessfull");
                }
            }
        });

    }



    private String CalculateDays(float result) {
        int week= (int) (result/.25);
        float months_float=(float) (week/4.345);
        int months=(int) (week/4.345);
        int days=(int ) ((months_float-months)*30.417);
        String Days=String.valueOf(days);
        return Days;

    }

    private String CalculateMonths(float result) {
        int week= (int) (result/.25);
        float months_float=(float) (week/4.345);
        int months=(int) (week/4.345);
        String Months= String.valueOf(months);
        return Months;
    }
    private String CalculateDaysOne(float result) {
        int week= (int) (result/.50);
        float months_float=(float) (week/4.345);
        int months=(int) (week/4.345);
        int days=(int ) ((months_float-months)*30.417);
        String Days=String.valueOf(days);
        return Days;

    }

    private String CalculateMonthsOne(float result) {
        int week= (int) (result/.50);
        float months_float=(float) (week/4.345);
        int months=(int) (week/4.345);
        String Months= String.valueOf(months);
        return Months;
    }


    private float CalculatorWeight(float current_weight, float goal_weight) {
        return (float)(current_weight-goal_weight);
    }

    private void submitForm() {

        String Currentweight = current_weight.getText().toString();
        String Goalweight = goal_weight.getText().toString();

        if(TextUtils.isEmpty(Currentweight))
        {
            current_weight.setError("Please write your weight");
            current_weight.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(Goalweight))
        {
            goal_weight.setError("Please write your weight");
            goal_weight.requestFocus();
            return;

        }
        float C_weight=Float.parseFloat(Currentweight);
        float G_weight=Float.parseFloat(Goalweight);
        float result=Math.abs(CalculatorWeight(C_weight,G_weight));
        String M=CalculateMonths(result);
        String D=CalculateDays(result);
        int addDays=Integer.parseInt(M);
        addDays= (int)((addDays*30.4167 )+1+ Integer.parseInt(D));
        DateFormat dateFormat1 = new SimpleDateFormat("MMM d, yyyy");
        Calendar c = Calendar.getInstance();
        String currentDateString = (dateFormat1.format(c.getTime())).toString();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DAY_OF_MONTH, addDays);
        String goal_weight_date =(dateFormat1.format(c.getTime())).toString();
        String M1=CalculateMonthsOne(result);
        String D1=CalculateDaysOne(result);
        int addDays1=Integer.parseInt(M1);
        addDays1= (int)((addDays1*30.4167 )+1+ Integer.parseInt(D1));
        DateFormat dateFormat2 = new SimpleDateFormat("MMM d, yyyy");
        Calendar c1 = Calendar.getInstance();
        String currentDateString1 = (dateFormat2.format(c1.getTime())).toString();
        c1.setTime(new Date()); // Now use today date.
        c1.add(Calendar.DAY_OF_MONTH, addDays1);
        String goal_weight_date1=(dateFormat2.format(c1.getTime())).toString();

        String result_str=String.format("%.1f",result);
        String myspinner=spinner.getSelectedItem().toString();

        if(C_weight < G_weight){
            goal_weight.setError("Please put Target Weight Lower than Current Weight ");
            goal_weight.requestFocus();
        }
        else {
            if (awesomeValidation.validate()) {
                Log.i("Validation Successfull","Completed");

                loadingBar.setTitle("Saving Information");
                loadingBar.setMessage("Please wait, while saving your information...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                HashMap userMap = new HashMap();
                userMap.put("current_weight", Currentweight);
                userMap.put("goal_weight", Goalweight);
                userMap.put("amount of weight needed",result_str);
                if(myspinner.equalsIgnoreCase("Easy 0.25 Kg per week"))
                {
                    userMap.put("No months",M);
                    userMap.put("No days",D);
                    userMap.put("Selected Target", myspinner);
                    userMap.put("Current Weight Date",currentDateString);
                    userMap.put("Goal Weight Date", goal_weight_date);

                }
                else {
                    userMap.put("No months",M1);
                    userMap.put("No days",D1);
                    userMap.put("Selected Target", myspinner);
                    userMap.put("Current Weight Date",currentDateString1);
                    userMap.put("Goal Weight Date", goal_weight_date1);
                }





                UserRefSec.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetWeight.this, "Your data is sucessfully updated", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }
                        else
                        {
                            String message= task.getException().getMessage();
                            Toast.makeText(SetWeight.this, "Error Occur:" + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }

                    }
                });

            }
        }




    }

}

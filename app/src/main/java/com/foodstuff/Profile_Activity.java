package com.foodstuff;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Range;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextInputEditText User_name,Phone_no,User_weight,User_age,User_height,User_bmi,User_city;
    private Button  SaveInformation;
    private RadioGroup rg;
    private RadioButton Gender;
    private TextView User_gender;
    private CircleImageView User_img;
    private FirebaseAuth mauth;
    private DatabaseReference UserRef;
    String currentuserID;
    private ProgressDialog loadingBar;
    private StorageReference UserProfileImageRef;
    final static int Gallery_pick=1;
    private Spinner spinner;
    String[] activity;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;


    android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        toolbar=findViewById(R.id.action_bar1);
        toolbar.setTitle(R.string.profile_activity);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        mauth=FirebaseAuth.getInstance();
        currentuserID=mauth.getCurrentUser().getUid();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserID);
        UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");

        User_img=(CircleImageView)findViewById(R.id.user_image);
        User_name=(TextInputEditText)findViewById(R.id.user_name);
        User_gender=(TextView) findViewById(R.id.user_gender);
        rg=(RadioGroup)findViewById(R.id.user_select_gender);
        Phone_no=(TextInputEditText)findViewById(R.id.user_phone);
        User_age=(TextInputEditText) findViewById(R.id.user_age);
        User_weight=(TextInputEditText)findViewById(R.id.user_weight);
        User_height=(TextInputEditText)findViewById(R.id.user_height);
        User_bmi=(TextInputEditText)findViewById(R.id.user_bmi);
        User_city=(TextInputEditText)findViewById(R.id.user_city);
        SaveInformation=(Button)findViewById(R.id.save_info);
        spinner=(Spinner) findViewById(R.id.activity_spinner);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.activity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text= (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loadingBar= new ProgressDialog(this);

        awesomeValidation.addValidation(this, R.id.user_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.user_height, Range.closed(94, 272), R.string.heighterror);
        awesomeValidation.addValidation(this, R.id.user_weight, Range.closed(30, 450), R.string.weighterror);
        awesomeValidation.addValidation(this, R.id.user_age, Range.closed(10, 100), R.string.ageerror);


       rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup radioGroup, int i) {
               RadioButton Gender=(RadioButton)findViewById(i);
               TextView UserGender=(TextView)findViewById(R.id.user_gender);
               UserGender.setText(Gender.getText());
           }
       });




        User_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date Picker");
            }
        });

        User_height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str1 = User_weight.getText().toString();
                String str2 = User_height.getText().toString();

                if(TextUtils.isEmpty(str1)){
                    User_weight.setError("Please enter your weight");
                    User_weight.requestFocus();
                    return;

                }

                if(TextUtils.isEmpty(str2)){
                    User_height.setError("Please enter your height");
                    User_height.requestFocus();
                    return;

                }


                //Get the user values from the widget reference
                float weight = Float.parseFloat(str1);
                float height = Float.parseFloat(str2)/100;
                //Calculate BMI value
                float bmiValue = calculateBMI(weight, height);
                //Define the meaning of the bmi value
                String bmiInterpretation = interpretBMI(bmiValue);
                String bmi_value_Str = String.format("%.1f", bmiValue);
                User_bmi.setText(String.valueOf(bmi_value_Str + "  " + bmiInterpretation));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        User_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent= new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_pick );
            }
        });


        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())

                {

                   if(dataSnapshot.hasChild("profileimage"))
                   {
                       String image = dataSnapshot.child("profileimage").getValue().toString();
                       Picasso.get().load(image)
                               .placeholder(R.drawable.profile_image).into(User_img);

                   }
                   if(dataSnapshot.hasChild("username") && dataSnapshot.hasChild("phone")
                   &&dataSnapshot.hasChild("Age") && dataSnapshot.hasChild("Gender")&& dataSnapshot.hasChild("Weight")
                   &&  dataSnapshot.hasChild("Height") && dataSnapshot.hasChild("Bmi") && dataSnapshot.hasChild("City")
                           && dataSnapshot.hasChild("Activity"))
                    {

                        String Username =dataSnapshot.child("username").getValue().toString();
                        User_name.setText(Username);
                        String Phone=dataSnapshot.child("phone").getValue().toString();
                        Phone_no.setText(Phone);
                        String Age=dataSnapshot.child("Age").getValue().toString();
                        User_age.setText(Age);
                        String gender=dataSnapshot.child("Gender").getValue().toString();
                        User_gender.setText(gender);
                        String Weight=dataSnapshot.child("Weight").getValue().toString();
                        User_weight.setText(Weight);
                        String Height=dataSnapshot.child("Height").getValue().toString();
                        User_height.setText(Height);
                        String Bmi=dataSnapshot.child("Bmi").getValue().toString();
                        User_bmi.setText(Bmi);
                        String City=dataSnapshot.child("City").getValue().toString();
                        User_city.setText(City);
                        String Activity=dataSnapshot.child("Activity").getValue().toString();

                    }

                   else
                   {
                       Toast.makeText(Profile_Activity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                   }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

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
    }


    private String interpretBMI(float bmiValue) {
        if (bmiValue < 16) {
            return "Severely underweight";
        } else if (bmiValue < 18.5) {

            return "Underweight";
        } else if (bmiValue < 25) {

            return "Normal";
        } else if (bmiValue < 30) {

            return "Overweight";

        } else {
            return "Obese";
        }
    }


    private float calculateBMI(float weight, float height) {
        return (float) (weight / (height * height));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_pick && resultCode==RESULT_OK && data!=null)
        {
            Uri Imageuri = data.getData();

            CropImage.activity(Imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
          CropImage.ActivityResult result= CropImage.getActivityResult(data);

          if(resultCode == RESULT_OK)
          {

              loadingBar.setTitle("Profile Image");
              loadingBar.setMessage("Please wait, while updating your profile image...");
              loadingBar.show();
              loadingBar.setCanceledOnTouchOutside(true);

              Uri resultUri = result.getUri();
              final StorageReference filepath= UserProfileImageRef.child(currentuserID + ".jpg");
              filepath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                  @Override
                  public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                      if (!task.isSuccessful()){
                          throw task.getException();
                      }
                      return filepath.getDownloadUrl();

                  }
              }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                  @Override
                  public void onComplete(@NonNull Task<Uri> task) {
                      if(task.isSuccessful())
                      {
                          Uri downUri= task.getResult();
                          Toast.makeText(Profile_Activity.this, "Profile image Store is sucessfully to firebase storage", Toast.LENGTH_SHORT).show();
                          final String downloadUrl= downUri.toString();

                          UserRef.child("profileimage").setValue(downloadUrl)
                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {

                                  if(task.isSuccessful())
                                  {
                                      //Intent selfIntent = new Intent(Profile_Activity.this, Profile_Activity.class);
                                      //startActivity(selfIntent);

                                      Toast.makeText(Profile_Activity.this, "Profile image Store is sucessfully to firebase storage", Toast.LENGTH_SHORT).show();
                                      loadingBar.dismiss();
                                  }
                                  else
                                      {
                                          String message= task.getException().getMessage();
                                          Toast.makeText(Profile_Activity.this, "Error Occur:" + message, Toast.LENGTH_SHORT).show();
                                          loadingBar.dismiss();
                                      }
                              }
                          });

                      }
                  }

              });
          }
          else
              {
                  Toast.makeText(this, "Error Occur: Image can not  be cropped...Try again " , Toast.LENGTH_SHORT).show();
                  loadingBar.dismiss();
              }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar c= Calendar.getInstance();
        c.set(Calendar.YEAR, i);
        c.set(Calendar.MONTH, i1);
        c.set(Calendar.DAY_OF_MONTH, i2);
        String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
        TextInputEditText textInputEditText=(TextInputEditText)findViewById(R.id.user_age);
        textInputEditText.setText(Integer.toString(CalculaterAge(c.getTimeInMillis())));


    }

   int CalculaterAge(long date){
        Calendar dob=Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today=Calendar.getInstance();
        int age= today.get(Calendar.YEAR)- dob.get(Calendar.YEAR);
        if(today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)){
            if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
                age--;
            }

        }
       if(today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)){
           if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
               age--;
           }

       }
        return age;
   }

    private void submitForm() {
        String username = User_name.getText().toString();
        String phone = Phone_no.getText().toString();
        String age = User_age.getText().toString();
        String gender = User_gender.getText().toString();
        String weight = User_weight.getText().toString();
        String height = User_height.getText().toString();
        String bmi = User_bmi.getText().toString();
        String city = User_city.getText().toString();
        String activity=spinner.getSelectedItem().toString();


        if(TextUtils.isEmpty(username))
        {

            User_name.setError("Please write your Name");
            User_name.requestFocus();
            return;


        }
        if(TextUtils.isEmpty(phone))
        {

            Phone_no.setError("Please write your phone");
            Phone_no.requestFocus();
            return;

        }

        if(TextUtils.isEmpty(age))
        {
            User_age.setError("Please write your age");
            User_age.requestFocus();
            return;

        }
        if(TextUtils.isEmpty(gender))
        {
            Toast.makeText(this, "Please select your Gender", Toast.LENGTH_SHORT).show();

        }
        if(TextUtils.isEmpty(weight))
        {

            User_weight.setError("Please write your weight");
            User_weight.requestFocus();
            return;

        }
        if(TextUtils.isEmpty(height))
        {

            User_height.setError("Please write your height");
            User_height.requestFocus();
            return;

        }
        if(TextUtils.isEmpty(city))
        {


            User_city.setError("Please write your city");
            User_city.requestFocus();
            return;

        }

        //calculating BMR value
        int calweg= Integer.parseInt(weight);
        int calhg= Integer.parseInt(height);
        int calag= Integer.parseInt(age);
        int BMR = CalculaterBMR(calweg,calhg,calag);

        switch (activity){
            case "Basal Metabolic Rate":
                BMR= (int) BMR;
                break;
            case "Little or no exercise":
                BMR= (int) (BMR*1.2);
                break;
            case "Little exercise/ Sports 1–3 days a week":
                BMR= (int) (BMR*1.3);
                break;
            case "Moderate exercise/ Sports 3–5 days a week":
                BMR= (int) (BMR*1.4);
                break;
            case "Hard exercise/ Sports 6–7 days a week":
                BMR= (int) (BMR*1.5);
                break;
            default:
                BMR= 2350;
                break;
        }
        int cal_burn= (int) ((BMR*20)/100);
        String bmr= String.valueOf(BMR);
        String burn_value=String.valueOf(cal_burn);
        //first validate the form then move ahead
        //if this becomes true that means validation is successfull
        if (awesomeValidation.validate()) {
            Log.i("Validation Successfull","Completed");

            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, while saving your information...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("phone", phone);
            userMap.put("Age", age);
            userMap.put("Gender", gender);
            userMap.put("Weight", weight);
            userMap.put("Height", height);
            userMap.put("Bmi", bmi);
            userMap.put("City", city);
            userMap.put("BMR",bmr);
            userMap.put("Activity", activity);
            userMap.put("Burn Kcal", burn_value);

            UserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(Profile_Activity.this, "Your data is sucessfully updated", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                    else
                    {
                        String message= task.getException().getMessage();
                        Toast.makeText(Profile_Activity.this, "Error Occur:" + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }

                }
            });

        }
    }

    private int CalculaterBMR(int calweg, int calhg, int calag) {
        String gender = User_gender.getText().toString();
        if(gender.equalsIgnoreCase("male")){
            return (int) (10*calweg + 6.25*calhg -5*calag +5);
        }
        else {
            return (int) (10*calweg + 6.25*calhg -5*calag +161);
        }
    }

}

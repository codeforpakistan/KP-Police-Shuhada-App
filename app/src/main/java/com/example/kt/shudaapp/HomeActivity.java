package com.example.kt.shudaapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    private static final int MY_LOCATION_PERMISSION_CODE = 2333;
    TextView mTextView;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor editor;
    CircleImageView mCircleImageView;
    String pin_code;
    ImageView logout_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTextView = findViewById(R.id.shaeed_name);
        mSharedPreferences = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String s_name = mSharedPreferences.getString("s_name", "No Data");
        mTextView.setText(s_name);
        checkLocation();
        mCircleImageView = findViewById(R.id.profile_image);
        logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setMessage("Are you sure want to logout!")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor = mSharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finish();
                            }

                        })
                        .setCancelable(false)
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });
        String path = mSharedPreferences.getString("path", "No Data");

        Picasso.get().load(path).into(mCircleImageView);

        final CardView new_complaint = findViewById(R.id.problem_btn);
        new_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ComplaintActivity.class));
            }
        });

        final CardView memories_share = findViewById(R.id.share_memories);
        memories_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ShareMemoriesActivity.class));
            }
        });

        final CardView pakages = findViewById(R.id.team_btn);
        pakages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ShaeedPakageLandscapeActivity.class));

            }
        });

        CardView help_desk = findViewById(R.id.help_desk_btn);
        help_desk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, HelpDesk.class));
            }
        });

    }

    private void checkLocation() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_PERMISSION_CODE);
        }
    }

    private void showInputDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        View viewInflated = LayoutInflater.from(HomeActivity.this).inflate(R.layout.pin_edit_text,null, false);
// Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pin_code = input.getText().toString();
                if (TextUtils.isEmpty(pin_code)){

                    input.setError("Enter pin code");

                }else {

                }

            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setCancelable(false);
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }
}

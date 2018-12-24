package com.example.kt.shudaapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kt.shudaapp.InterfaceClasses.UserAttachmentApi;
import com.example.kt.shudaapp.ModelClasses.AttachModel;
import com.example.kt.shudaapp.Utils.Config;
import com.example.kt.shudaapp.Utils.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShareMemoriesActivity extends AppCompatActivity {
    EditText description_et;
    RelativeLayout image_attach, video_attach;
    ImageView imageview1;

    private static final  int MY_Gallery_PERMISSION_CODE1 = 100;
    private static final  int MY_Gallery_PERMISSION_CODE2 = 200;
    MultipartBody.Part part1, part2;
    List<MultipartBody.Part> fileParts = new ArrayList<>();
    RelativeLayout submit;
    HashMap<String, RequestBody> mMap = new HashMap<>();
    ProgressDialog progressDialog;
    TextView delete_image, delete_video;
    String compressedVideoPath;
    ImageView video_preview;
    SharedPreferences mSharedPreferences;
    String member_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_memories);
        mSharedPreferences = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        member_id = mSharedPreferences.getString("member_id", "No Data");

        description_et = findViewById(R.id.description_et);
        image_attach = findViewById(R.id.image_attach);
        video_attach = findViewById(R.id.video_attach);
        imageview1 = findViewById(R.id.imageview1);
        delete_image = findViewById(R.id.delete_image2);
        delete_video = findViewById(R.id.delete_video);
        delete_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(2);
            }
        });
        video_preview = findViewById(R.id.video_preview);
        delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(1);
            }
        });
        submit = findViewById(R.id.submit);
        progressDialog = new ProgressDialog(this);
        image_attach.setEnabled(true);
        image_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ShareMemoriesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShareMemoriesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                            MY_Gallery_PERMISSION_CODE1);
                } else {
                    openImageGallery();

                }
            }
        });
        video_attach.setEnabled(true);
        video_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ShareMemoriesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShareMemoriesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                            MY_Gallery_PERMISSION_CODE1);
                } else {
                    openVideoGallery();

                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()){
                    new AlertDialog.Builder(ShareMemoriesActivity.this)
                            .setMessage("Are you sure to submit form")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String description = description_et.getText().toString();
                                    if (TextUtils.isEmpty(description)){
                                        description_et.setError("Enter description");
                                    }else {
                                        mMap.put("details", createPartFromString(description));
                                        mMap.put("s_id", createPartFromString("15"));
                                        if (!member_id.equals("No Data")){
                                            mMap.put("member_id", createPartFromString(member_id));
                                        }
                                        postData();
                                    }

                                }
                            })
                            .setNegativeButton("No", null)
                            .show();;


                }else {
                    Toast.makeText(ShareMemoriesActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openImageGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , MY_Gallery_PERMISSION_CODE1);
    }

    private void openVideoGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , MY_Gallery_PERMISSION_CODE2);
    }
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        try {
            switch (requestCode) {
                case MY_Gallery_PERMISSION_CODE1:
                    if(resultCode == RESULT_OK){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                delete_image.setVisibility(View.VISIBLE);
                                imageview1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                                imageview1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageview1.setImageURI(data.getData());
                                image_attach.setEnabled(false);

                            }
                        });

                        part1 = prepareFilePart("files[]", FileUtils.getFile(ShareMemoriesActivity.this, data.getData()));
                        fileParts.add(part1);
                    }
                    break;

                case MY_Gallery_PERMISSION_CODE2:
                    if(resultCode == RESULT_OK){
                        final String selectedVideoPath = data.getData().getPath();
                        Log.e( "selectedVideoPath: ", selectedVideoPath );

                        if(selectedVideoPath == null) {
                            Toast.makeText(this, "Picking video from gallery failed", Toast.LENGTH_SHORT).show();
                        } else {
                            final File galleryVideoFile = FileUtils.getFile(ShareMemoriesActivity.this, data.getData());
                            // Get length of file in bytes
                            long fileSizeInBytes = galleryVideoFile.length();
                            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                            long fileSizeInKB = fileSizeInBytes / 1024;
                            //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                            long fileSizeInMB = fileSizeInKB / 1024;
                            Log.e( "fileSizeInMB: ", ""+fileSizeInMB);
                            if (fileSizeInMB > 17){
                                Toast.makeText(this, "Video file must not be greater then 200 MB", Toast.LENGTH_LONG).show();
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/media/videos");
                                        if (f.mkdirs() || f.isDirectory())
                                            //compress and output new video specs
                                            new VideoCompressAsyncTask(ShareMemoriesActivity.this).execute(data.getData().toString(), f.getPath());

                                        // FileUtils.getFile(AttachmentSubmit.this, getUriFromPath(compressedVideoPath)


                                        //standalone player
                                        /*ImageView playbtn = findViewById(R.id.playbtn);
                                        playbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Uri video = FileUtils.getUri(galleryVideoFile);
                                                Intent intent = new Intent(ShareMemoriesActivity.this, FullScreenVideo.class);
                                                intent.putExtra("vidUri",String.valueOf(video));
                                                Log.e("onClick: ", String.valueOf(video));
                                                Log.e("uri: ", ""+video);
                                                startActivity(intent);
                                            }
                                        });*/
                                    }
                                });
                            }


                        }
                    }
                    break;
            }
        }catch (Exception e){
            Log.e("Exception: ", e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_Gallery_PERMISSION_CODE1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageGallery();
            } else {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, File file) {
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(FileUtils.getMimeType(file)), file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected())
        {
            isAvailable = true;

        }
        return isAvailable;
    }


    private void postData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("Uploading Data...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserAttachmentApi userComplaintApi = retrofit.create(UserAttachmentApi.class);
        Log.e( "files: ", fileParts.toString());
        Call<AttachModel> call = userComplaintApi.Post(mMap, fileParts);
        call.enqueue(new Callback<AttachModel>() {
            @Override
            public void onResponse(Call<AttachModel> call, Response<AttachModel> response) {
                Log.e("onResponse: ", response.toString());
                if(response.isSuccessful()){
                    Log.e("onResponse: ", response.body().getSuccess().toString());
                    if (response.body().getSuccess() == 1){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(ShareMemoriesActivity.this)
                                        .setTitle("Note!")
                                        .setMessage("Your shared memory has been uploaded")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                        });

                    }
                }
            }

            @Override
            public void onFailure(Call<AttachModel> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.share_memories), "Some thing went wrong", Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.text_color_red));
                        TextView textView =  snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(getResources().getColor(android.R.color.white));
                        snackbar.show();

                    }
                });

            }
        });
    }

    private void removeImage(int id) {
        switch (id){
            case 1:
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to remove image")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageview1.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                RelativeLayout.LayoutParams layoutParams =
                                        (RelativeLayout.LayoutParams)imageview1.getLayoutParams();
                                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                imageview1.setLayoutParams(layoutParams);

                                imageview1.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_black_24dp));
                                delete_image.setVisibility(View.GONE);
                                image_attach.setEnabled(true);


                                for(int i=0;i<fileParts.size();i++)
                                {
                                    if(fileParts.get(i) == part1)
                                    {
                                        fileParts.remove(i);
                                        Log.e("Image Removed: ", ""+part1 );
                                    }
                                }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;

            case 2:
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to remove video")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                video_preview.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                RelativeLayout.LayoutParams layoutParams =
                                        (RelativeLayout.LayoutParams)video_preview.getLayoutParams();
                                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                video_preview.setLayoutParams(layoutParams);
                                video_preview.setBackgroundDrawable(null);
                                video_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_call_black_24dp));
                                delete_video.setVisibility(View.GONE);
                                video_attach.setEnabled(true);


                                for(int i=0;i<fileParts.size();i++)
                                {
                                    if(fileParts.get(i) == part2)
                                    {
                                        fileParts.remove(i);
                                        Log.e("Image Removed: ", ""+part2 );
                                    }
                                }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;

        }


    }

    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;
        ProgressDialog mProgressDialog;

        public VideoCompressAsyncTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("preExecute", "Compressing....");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog = new ProgressDialog(ShareMemoriesActivity.this);
                    mProgressDialog.setTitle("Please Wait");
                    mProgressDialog.setMessage("Compressing Video Size...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                }
            });

        }

        @Override
        protected String doInBackground(final String... paths) {

            try {
                compressedVideoPath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);
                Log.e( "compressedVideoPath: ", compressedVideoPath);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Ex", e.toString());
            }
            return compressedVideoPath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);

            try {
                File compressFile = new File(compressedFilePath);
                Log.e( "compressFile: ", ""+compressFile);
                 part2 = prepareFilePart("file[]", compressFile);
                fileParts.add(part2);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("postExecute", "Compressed Successflly!");
                        //video_layout.setVisibility(View.VISIBLE);
                        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(compressedVideoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
                        video_preview.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                        video_preview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        video_preview.setImageBitmap(null);
                        video_preview.setBackgroundDrawable(bitmapDrawable);
                        video_attach.setEnabled(false);
                        delete_video.setVisibility(View.VISIBLE);
                        mProgressDialog.dismiss();
                    }
                });

            }catch (Exception e){
                Log.e( "run: ", e.toString() );
            }

        }
    }



    @NonNull
    private RequestBody createPartFromString(String val) {
        return RequestBody.create(okhttp3.MultipartBody.FORM,  val);
    }
}

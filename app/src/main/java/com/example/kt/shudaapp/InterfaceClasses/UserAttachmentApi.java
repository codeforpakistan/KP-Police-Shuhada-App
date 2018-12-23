package com.example.kt.shudaapp.InterfaceClasses;



import com.example.kt.shudaapp.ModelClasses.AttachModel;
import com.example.kt.shudaapp.Utils.Config;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface UserAttachmentApi {

    @Multipart
    @POST(Config.SEND_ATTACHEMENT)
    Call<AttachModel> Post(@PartMap Map<String, RequestBody> map, @Part List<MultipartBody.Part> files);
}

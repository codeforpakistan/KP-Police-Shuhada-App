package com.example.kt.shudaapp.InterfaceClasses;
import com.example.kt.shudaapp.ModelClasses.ComplaintRegisterModel;
import com.example.kt.shudaapp.ModelClasses.VerifiModel;
import com.example.kt.shudaapp.Utils.Config;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * Created by shahzaib on 02-Aug-18.
 */

public interface ComplaintiApi {
    @Multipart
    @POST(Config.COMPLAINT_REG)
    Call<ComplaintRegisterModel> post_complaint(@PartMap Map<String, RequestBody> map);
}

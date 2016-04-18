package com.binarycraft.acland.interfaces;

import com.binarycraft.acland.entity.UnionMouzaResponse;
import com.binarycraft.acland.entity.UpdateUnionMouzaResponse;
import com.binarycraft.acland.entity.VerifyDagResponse;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface APIUnionMouzaInterface {
    @GET("acland/getall")
    Call<UnionMouzaResponse> getAllUnionMouza();

    @GET("form/isdagAvailabel?")
    Call<VerifyDagResponse> verifyDasNumber(@Query("mouzaid") String mouzaid, @Query("unionid") String unionid,@Query("dagnumber") String dagnumber, @Query("dagtype") String dagtype);

    @GET("")
    Call<UpdateUnionMouzaResponse> getUpdatedUnionMouza();
}

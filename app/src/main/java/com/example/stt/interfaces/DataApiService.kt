package com.example.stt.interfaces

import com.example.stt.model.ttsReturn
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
// 수정중

data class POSTbody (
    val req_id :String,
    val lang_code : String,
    val audio : String
)

interface DataApiService {
    @POST("WiseASR/Recognition")
//    @GET("/api/v2/publicholidays/{year}/{locale}")
    fun transferSound(
        @Header("Authorization") auth_key:String,
        @Body body : POSTbody
    ): Call<List<ttsReturn>>
}
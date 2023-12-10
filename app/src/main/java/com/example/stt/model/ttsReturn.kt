package com.example.stt.model

import com.google.gson.annotations.SerializedName

data class ttsReturn(
    @SerializedName("result") val result: Int,
    @SerializedName("return_type") val return_type: String,
    @SerializedName("return_object") val return_object: String
)


package com.example.nafis.nf2024.organizeradminpanel.DiffutilCallBack

import com.example.nafis.nf2024.organizeradminpanel.Model.Video

interface VideoCallback {
    fun onVideoClick(item:Video)
    fun updateVideoClick(item:Video)
    fun deleteVideoClick(itemId:String)
}
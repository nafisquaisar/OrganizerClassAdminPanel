package com.example.nafisquaisarcoachingcenter.DIffUtilCallBack

import com.example.nafis.nf2024.organizeradminpanel.Model.TestObject


interface TotalTestItemCallback {
    fun onTotalTestClick(item: TestObject, position:Int)
    fun deleteTotalTestClick(itemId: String)
    fun updateTotalTestClick(item: TestObject)

}
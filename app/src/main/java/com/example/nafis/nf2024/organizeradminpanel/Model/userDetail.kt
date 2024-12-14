package com.example.nafis.nf2024.organizeradminpanel.Model

data class userDetail(
    var id: String,
    var Name:String,
    var Email:String,
    var Number:String,
    var password:String,
    var course:String,
    var ProUrl:String){
    constructor():this("","","","","","","")
}

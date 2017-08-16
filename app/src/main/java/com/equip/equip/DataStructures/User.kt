package com.equip.equip.DataStructures

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.HashMap

/**
 * Created by Curtis on 8/16/2017.
 */
@IgnoreExtraProperties
class User {
    lateinit var userId: String
    lateinit var email: String
    lateinit var displayName: String
    var equipmentListings: MutableList<String>? = null
    var picUrl: String = ""
    var googleConnected: Boolean = false
    var facebookConnected: Boolean = false;


    constructor(){
        //default
    }

    constructor(uId: String, email: String, displayName: String){
        this.userId = uId
        this.email = email
        this.displayName = displayName
    }

    @Exclude
    fun addEquipmentListing(key: String){
        if (equipmentListings == null){
            equipmentListings = ArrayList<String>()
        }
        equipmentListings!!.add(key)
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("userId", userId)
        result.put("email", email)
        result.put("displayName", displayName)
        if (equipmentListings != null) {
            result.put("equipmentListings", equipmentListings!!)
        }
        result.put("picUrl", picUrl)
        result.put("googleConnected", googleConnected)
        result.put("facebookConnected", facebookConnected)
        return result
    }

}
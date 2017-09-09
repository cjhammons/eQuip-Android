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
    var facebookConnected: Boolean = false
    var notificationTokens: MutableList<String>? = null
    var isSeller: Boolean = false

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
            equipmentListings = ArrayList<String>() as MutableList<String>?
        }
        equipmentListings!!.add(key)
    }

    @Exclude
    fun addToken(token: String){
        if (token.equals(""))
            return
        if (notificationTokens == null)
            notificationTokens = ArrayList<String>()
        if (notificationTokens!!.contains(token))
            return
        notificationTokens!!.add(token)
    }

    @Exclude
    fun removeToken(token: String){
        if (notificationTokens == null)
            return
        if (notificationTokens!!.contains(token))
            notificationTokens!!.remove(token)
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
        if (notificationTokens != null){
            result.put("notificationTokens", notificationTokens!!)
        }
        result.put("picUrl", picUrl)
        result.put("googleConnected", googleConnected)
        result.put("facebookConnected", facebookConnected)
        result.put("isSeller", isSeller)
        return result
    }

}
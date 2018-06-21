package com.equip.equip.DataStructures

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.JsonObject
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import org.json.JSONObject
import java.util.*

/**
 * Created by Curtis on 8/16/2017.
 */
@IgnoreExtraProperties
class User {
    lateinit var userId: String
    var email: String = ""
    var displayName: String = ""
    var picUrl: String = ""
    var googleConnected: Boolean = false
    var facebookConnected: Boolean = false
    var notificationTokens: MutableList<String> = ArrayList<String>()
    var isVendor: Boolean = false
    var reservationIds: MutableList<String> = ArrayList<String>()
    var rentalIds: MutableList<String> = ArrayList<String>()
    var historicRentalIds: MutableList<String> = ArrayList<String>()
    var phoneNumber: String = ""
    var geoloc: HashMap<String, Double> = HashMap<String, Double>()


    //Vendor only
    var ownedReservationIds: MutableList<String> = ArrayList<String>()
    var equipmentPresets: MutableList<EquipmentPreset> = ArrayList<EquipmentPreset>()
    var equipmentListings: MutableList<String>? = ArrayList<String>()
    var address: String = ""

    constructor(){
        //default
    }

    constructor(uId: String, email: String, displayName: String){
        this.userId = uId
        this.email = email
        this.displayName = displayName
    }

    //seller constructor
    constructor(uId: String, email: String, displayName: String, address: String){
        this.userId = uId
        this.email = email
        this.displayName = displayName
        this.address = address
        this.isVendor = true
    }

    @Exclude
    fun setGeolocation(lat: Double, lng: Double ): Boolean{
        geoloc = HashMap<String, Double>()
        if (lat > 90.0 || lat < 0.0)
            return false
        if (lng > 180 || lng < -180){
            return false
        }
        geoloc.put("lat", lat)
        geoloc.put("lng", lng)
        return true
    }

    @Exclude
    fun getLat(): Double {
        return geoloc.get("lat")!!.toDouble()
    }

    @Exclude
    fun getLng(): Double{
        return geoloc.get("lng")!!.toDouble()
    }

    /**
     * If the user has no set display name, return their email address instead
     */
    @Exclude
    fun muhDisplayName(): String {
        if (this.displayName.equals("")){
            return this.email
        } else {
           return this.displayName
        }
    }

    @Exclude
    fun addRentalId(rentalId: String){
        if (!rentalIds.contains(rentalId))
            rentalIds.add(rentalId)
    }

    fun removeRentalId(rentalId:String) {
        if (rentalIds.contains(rentalId))
            rentalIds.remove(rentalId)
    }

    @Exclude
    fun addEquipmentListing(key: String){
        if (equipmentListings == null){
            equipmentListings = ArrayList<String>() as MutableList<String>?
        }
        equipmentListings!!.add(key)
    }

    @Exclude
    fun removeEquipmentListing(key: String) {
        if (equipmentListings!!.contains(key))
            equipmentListings!!.remove(key)
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
        result.put("equipmentListings", equipmentListings!!)
        result.put("notificationTokens", notificationTokens!!)
        result.put("reservationIds", reservationIds)
        result.put("ownedReservationIds", ownedReservationIds)
        result.put("picUrl", picUrl)
        result.put("googleConnected", googleConnected)
        result.put("facebookConnected", facebookConnected)
        result.put("isVendor", isVendor)
        result.put("equipmentPresets", equipmentPresets)
        result.put("address", address)
        result.put("rentalIds", rentalIds)
        result.put("historicRentalIds", historicRentalIds)
        result.put("phoneNumber", phoneNumber)
        result.put("geoloc", geoloc)
        return result
    }

}
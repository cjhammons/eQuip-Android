package com.equip.equip.DataStructures

import android.net.Uri

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by curtis on 6/30/17.
 *
 * WEEEEE KOTLIN
 */
@IgnoreExtraProperties
class Equipment {
    lateinit var description: String
    lateinit var ownerId: String
    lateinit var borrowerId: String
    lateinit var category: String
    var name: String = ""
    var available: Boolean = false
    lateinit var key: String
    var dueDate: String = ""
    var reservationId: String = ""
    var geoloc: HashMap<String, Double> = HashMap<String, Double>()
    var ratePrice: Double = -1.0
    var rateUnit: RateUnit = RateUnit.HOURLY


    enum class RateUnit(val value: String) {
        HOURLY("Hourly"), DAILY( "Daily"), WEEKLY("Weekly")
    }

    constructor() {
        //Default
    }

    constructor(description: String, ownerId: String,
                borrowerId: String, category: String,
                available: Boolean,
                name: String,
                rate: Double,
                rateUnit: RateUnit) {

        this.description = description
        this.ownerId = ownerId
        this.borrowerId = borrowerId
        this.category = category
        this.available = available
        this.key = ""
        this.name = name
        this.ratePrice = rate
        this.rateUnit = rateUnit
    }

    @Exclude
    fun borrow(borrowerId: String){
        this.borrowerId = borrowerId
        this.available = false
    }

    @Exclude
    fun unBorrow(){
        this.available = true
        this.borrowerId = ""
    }

    @Exclude
    fun addKey(key: String) {
        this.key = key
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
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("key", key)
        result.put("description", description)
        result.put("ownerId", ownerId)
        result.put("borrowerId", borrowerId)
        result.put("category", category)
        result.put("available", available)
        result.put("name", name)
        result.put("reservationId", reservationId)
        result.put("duedate", dueDate)
        result.put("geoloc", geoloc)
        result.put("ratePrice", ratePrice)
        result.put("rateUnit", rateUnit)
        return result
    }


}

package com.equip.equip.DataStructures

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by curtis on 12/2/17.
 */
@IgnoreExtraProperties
class UserSearch {
    lateinit var query: String
    lateinit var userId: String
    lateinit var timeStamp: String
    var lat: Double
    var lng: Double

    constructor(query: String, userId: String, timeStamp: String, lat:Double, lng:Double) {
        this.query = query
        this.userId = userId
        this.timeStamp = timeStamp
        this.lat = lat
        this.lng = lng
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("query", query)
        result.put("userId", userId)
        result.put("timeStamp", timeStamp)
        result.put("lat", lat)
        result.put("lng", lng)
        return result
    }
}
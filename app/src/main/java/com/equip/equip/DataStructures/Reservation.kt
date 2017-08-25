package com.equip.equip.DataStructures

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.HashMap

/**
 * Created by Curtis on 8/24/2017.
 */
@IgnoreExtraProperties
class Reservation {
    lateinit var equipmentId: String
    lateinit var ownerId: String
    lateinit var borrowerId: String
    var dateTimeReserved: String = ""
    var reservePeriod: String = ""
    var confirmedByOwner: Boolean = false
    var dateTimeConfirmed: String = ""

    constructor(){
        //default
    }

    constructor(equipmentId: String, ownerId: String, borrowerId: String){
        this.equipmentId = equipmentId
        this.ownerId = ownerId
        this.borrowerId = borrowerId
    }

    @Exclude
    fun reserve(dateTime: String){
        this.dateTimeReserved = ""
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("equipmentId", equipmentId)
        result.put("ownerId", ownerId)
        result.put("borrowerId", borrowerId)
        result.put("dateTimeReserved", dateTimeReserved)
        result.put("reservedPeriod", reservePeriod)
        result.put("confirmedByOwner", confirmedByOwner)
        result.put("dateTimeConfirmed", dateTimeConfirmed)
        return result
    }



}
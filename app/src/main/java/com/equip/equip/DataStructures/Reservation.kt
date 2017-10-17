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
    var key: String = ""
    var dateTimeReserved: String = ""
    var reservedPeriodStartDate: String = ""
    var reservedPeriodEndDate: String = ""
    var confirmedByOwner: Boolean = false
    var dateTimeConfirmed: String = ""


    constructor(){
        //default
    }

    constructor(equipmentId: String, ownerId: String, borrowerId: String,
                startDate: String, endDate: String, timeReserved: String,
                key: String){
        this.equipmentId = equipmentId
        this.ownerId = ownerId
        this.borrowerId = borrowerId
        this.reservedPeriodStartDate = startDate
        this.reservedPeriodEndDate = endDate
        this.dateTimeReserved = timeReserved
        this.key = key
    }

    @Exclude
    fun setConfirmed(dateTimeConfirmed: String) {
        confirmedByOwner = true
        this.dateTimeConfirmed = dateTimeConfirmed
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("equipmentId", equipmentId)
        result.put("ownerId", ownerId)
        result.put("borrowerId", borrowerId)
        result.put("dateTimeReserved", dateTimeReserved)
        result.put("reservedPeriodStartDate", reservedPeriodStartDate)
        result.put("reservedPeriodEndDate", reservedPeriodEndDate)
        result.put("confirmedByOwner", confirmedByOwner)
        result.put("dateTimeConfirmed", dateTimeConfirmed)
        return result
    }



}
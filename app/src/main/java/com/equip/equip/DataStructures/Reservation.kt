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
    var reservePeriodStartDate: String = ""
    var reservePeriodEndDate: String = ""
    var confirmedByOwner: Boolean = false
    var dateTimeConfirmed: String = ""

    constructor(){
        //default
    }

    constructor(equipmentId: String, ownerId: String, borrowerId: String,
                startDate: String, endDate: String, timeReserved: String){
        this.equipmentId = equipmentId
        this.ownerId = ownerId
        this.borrowerId = borrowerId
        this.reservePeriodStartDate = startDate
        this.reservePeriodEndDate = endDate
        this.dateTimeReserved = timeReserved
    }


    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("equipmentId", equipmentId)
        result.put("ownerId", ownerId)
        result.put("borrowerId", borrowerId)
        result.put("dateTimeReserved", dateTimeReserved)
        result.put("reservedPeriodStartDate", reservePeriodStartDate)
        result.put("reservedPeriodEndDate", reservePeriodEndDate)
        result.put("confirmedByOwner", confirmedByOwner)
        result.put("dateTimeConfirmed", dateTimeConfirmed)
        return result
    }



}
package com.equip.equip.DataStructures

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by Curtis on 9/7/2017.
 */

@IgnoreExtraProperties
class EquipmentPreset {
    lateinit var name: String
    var fields: HashMap<String, String>? = null

    constructor(){
        //default
    }

    constructor(name: String){
        this.name = name
    }

    @Exclude
    fun addField(fieldName: String, fieldHint: String){
        if (fields == null){
            fields = HashMap<String, String>()
        }
        if (fields!!.contains(fieldName)){
            return
        }
        fields!!.put(fieldName, fieldHint)
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("name", name)
        if (fields != null) {
            result.put("fields", fields!!)
        }
        return result
    }
}
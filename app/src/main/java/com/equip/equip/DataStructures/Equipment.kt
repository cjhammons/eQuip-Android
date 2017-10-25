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
    var imagePaths: MutableList<String>? = null
    var available: Boolean = false
    lateinit var key: String
    var dueDate: String = ""
    var reservationId: String = ""

    constructor() {
        //Default
    }

    constructor(description: String, ownerId: String,
                borrowerId: String, category: String,
                imagePaths: MutableList<String>?, available: Boolean,
                name: String) {

        this.description = description
        this.ownerId = ownerId
        this.borrowerId = borrowerId
        this.category = category
        this.imagePaths = imagePaths
        this.available = available
        this.key = ""
        this.name = name
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
    fun addImageUri(imageUri: Uri) {
        if (imagePaths == null) {
            imagePaths = ArrayList<String>()
        }

        imagePaths!!.add(imageUri.toString())
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("key", key)
        result.put("description", description)
        result.put("ownerId", ownerId)
        result.put("borrowerId", borrowerId)
        result.put("category", category)
        if (imagePaths != null) {
            result.put("imagePaths", imagePaths!!)
        }
        result.put("available", available)
        result.put("name", name)
        result.put("reservationId", reservationId)
        result.put("duedate", dueDate)
        return result
    }


}

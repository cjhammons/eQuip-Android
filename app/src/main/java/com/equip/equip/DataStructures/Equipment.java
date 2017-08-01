package com.equip.equip.DataStructures;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by curtis on 6/30/17.
 */
@IgnoreExtraProperties
public class Equipment {
    @Deprecated
    public String uId;
    public String description;
    public String ownerId;
    public String borrowerId;
    public String category;
    public List<String> imagePaths;
    public boolean available;

    public Equipment(){
        //Default
    }

    @Deprecated
    public Equipment(String uId, String description, String ownerId, String borrowerId, String category, List<String> imagePaths, boolean available){
        this.uId = uId;
        this.description = description;
        this.ownerId = ownerId;
        this.borrowerId = borrowerId;
        this.category = category;
        this.imagePaths = imagePaths;
        this.available = available;
    }

    public Equipment(String description, String ownerId, String borrowerId, String category, List<String> imagePaths, boolean available){
        this.description = description;
        this.ownerId = ownerId;
        this.borrowerId = borrowerId;
        this.category = category;
        this.imagePaths = imagePaths;
        this.available = available;
    }

    @Exclude
    public void addImageUri(Uri imageUri){
        if (imagePaths == null){
            imagePaths = new ArrayList<>();
        }
        imagePaths.add(imageUri.toString());
    }

    @Exclude
    @Deprecated
    public Map<String, Object> toMap(boolean testBoolPlsIgnore){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uId", uId);
        result.put("description", description);
        result.put("ownerId", ownerId);
        result.put("borrowerId", borrowerId);
        result.put("category", category);
        result.put("imagePaths", imagePaths);
        result.put("available", available);
        return result;
    }
    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("description", description);
        result.put("ownerId", ownerId);
        result.put("borrowerId", borrowerId);
        result.put("category", category);
        result.put("imagePaths", imagePaths);
        result.put("available", available);
        return result;
    }



}

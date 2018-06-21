package com.equip.equip.Util.stripe;

import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * An implementation of {@link EphemeralKeyProvider} that can be used to generate
 * ephemeral keys on the backend.
 */
public class MyEphemeralKeyProvider implements EphemeralKeyProvider {

    private @NonNull CompositeSubscription mCompositeSubscription;
    private @NonNull StripeService mStripeService;
    private @NonNull ProgressListener mProgressListener;

    public MyEphemeralKeyProvider(@NonNull ProgressListener progressListener) {
        Retrofit retrofit = RetrofitFactory.getInstance();
        mStripeService = retrofit.create(StripeService.class);
        mCompositeSubscription = new CompositeSubscription();
        mProgressListener = progressListener;
    }

    @Override
    public void createEphemeralKey(@NonNull @Size(min = 4) final String apiVersion,
                                   @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {


        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("stripe_customers/" + uId + "/customer_id/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> apiParamMap = new HashMap<>();
                apiParamMap.put("api_version", apiVersion);
                apiParamMap.put("customerId", dataSnapshot.getValue().toString());

                mCompositeSubscription.add(
                        mStripeService.createEphemeralKey(apiParamMap)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<ResponseBody>() {
                                    @Override
                                    public void call(ResponseBody response) {
                                        try {
                                            String rawKey = response.string();
                                            keyUpdateListener.onKeyUpdate(rawKey);
                                            mProgressListener.onStringResponse(rawKey);
                                        } catch (IOException iox) {
                                            Log.e("Ephemeral error:", iox.getMessage());
                                        }
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        mProgressListener.onStringResponse(throwable.getMessage());
                                    }
                                }));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public interface ProgressListener {
        void onStringResponse(String string);
    }
}

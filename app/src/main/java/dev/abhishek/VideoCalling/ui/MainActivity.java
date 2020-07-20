package dev.abhishek.VideoCalling.ui;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.abhishek.VideoCalling.R;
import dev.abhishek.VideoCalling.application.MyApplication;
import dev.abhishek.VideoCalling.managers.ActivitySwitchManager;
import dev.abhishek.VideoCalling.managers.SharedPrefManager;
import dev.abhishek.VideoCalling.ui.auth.AuthActivity;
import dev.abhishek.VideoCalling.utils.Constants;
import dev.abhishek.VideoCalling.utils.Utils;

public class MainActivity extends AppCompatActivity {


    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    private MaterialButton online_counter;
    private MaterialButton startSearch;
    private MaterialButton btn_logout;
    private TextView tvUsername;
    private FrameLayout frameLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private SharedPrefManager sharedPrefManager;

    private static int flag = 0;
    private static int index = 0;
    public static long timestamp = 0;
    public static Handler handler;
    public static Dialog dialog;
    private static String channel_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        initConfig();

        initAdMob();
        setSearchingCounter();
    }

    private void initUi() {
        frameLayout = findViewById(R.id.fl_adplaceholder);
        online_counter = findViewById(R.id.onlineCounter);
        btn_logout = findViewById(R.id.btn_logout);
        startSearch = findViewById(R.id.startSearch);
        tvUsername = findViewById(R.id.tv_username);
    }

    private void initAdMob() {
        mNativeAds = new ArrayList<>();
        loadAdPlaceholder();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                adLoader.loadAds(new AdRequest.Builder().build(), 3);
                Log.e("onInitializationComp", initializationStatus.getAdapterStatusMap().toString());
            }
        });

        adLoader = new AdLoader.Builder(getApplicationContext(), "ca-app-pub-3940256099942544/2247696110")
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        if (isDestroyed()) {
                            unifiedNativeAd.destroy();
                            return;
                        }
                        // Show the ad.
                        mNativeAds.add(unifiedNativeAd);
                        Log.e("AD", unifiedNativeAd.getHeadline());
                        if (mNativeAds.size() == 3) {
                            inflateAds();
                        }
                        if (!adLoader.isLoading()) {
                            // The AdLoader is still loading ads.
                            // Expect more adLoaded or onAdFailedToLoad callbacks.

                        } else {
                            // The AdLoader has finished loading ads.

                        }
                    }
                })
                .withAdListener(new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(int errorCode) {
                                        // Handle the failure by logging, altering the UI, and so on.
                                        Log.d("Failedaf", String.valueOf(errorCode));
                                    }
                                }
                )
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
    }

    private void loadAdPlaceholder() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.native_ad_placeholder, frameLayout, false);
        frameLayout.addView(view);
    }

    private void inflateAds() {
        final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(index==3){
                    index = 0;
                }
                Log.e("ad", mNativeAds.get(index).getHeadline());
                View view = inflater.inflate(R.layout.native_ad_layout, frameLayout, false);

                TextView ad_headline = view.findViewById(R.id.ad_headline);
                TextView ad_advertiser = view.findViewById(R.id.ad_advertiser);
                TextView ad_body = view.findViewById(R.id.ad_body);
                RatingBar ad_rating = view.findViewById(R.id.ad_rating);
                ImageView ad_icon = view.findViewById(R.id.ad_icon);

                if (mNativeAds.get(index).getStarRating() == null) {
                    ad_rating.setVisibility(View.INVISIBLE);
                } else {
                    Log.e("rating", mNativeAds.get(index).getStarRating().toString());
                    ad_rating.setRating(mNativeAds.get(index).getStarRating().floatValue());
                    ad_rating.setVisibility(View.VISIBLE);
                }
                NativeAd.Image icon = mNativeAds.get(index).getIcon();
                if (icon == null) {
                    ad_icon.setVisibility(View.INVISIBLE);
                } else {
                    (ad_icon).setImageDrawable(icon.getDrawable());
                    ad_icon.setVisibility(View.VISIBLE);
                }

                ad_headline.setText(mNativeAds.get(index).getHeadline());
                ad_advertiser.setText(mNativeAds.get(index).getAdvertiser());
                ad_body.setText(mNativeAds.get(index).getBody());
                frameLayout.addView(view);
                index++;
                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    private void setSearchingCounter() {
        firestore.collection(Constants.COLLECTION_SEARCHING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("ERROR: COUNT SEARCH", e.getMessage());
                            return;
                        }

                        if (snapshots != null) {
                            int count = snapshots.getDocuments().size();
                            Resources res = getResources();
                            String text = String.format(res.getString(R.string.online_ct), String.valueOf(count));
                            online_counter.setText(text);
                            online_counter.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    private void initConfig() {
        firebaseAuth = MyApplication.getInstance().getFirebaseAuth();
        firestore = MyApplication.getInstance().getFirestore();
        sharedPrefManager = MyApplication.getInstance().getSharedPrefManager();
        String[] name = sharedPrefManager.getUser().getUserName().split(" ");
        tvUsername.setText("Hello, " + name[0]);


        //Remove from Searching queue if exists
        firestore.collection(Constants.COLLECTION_SEARCHING).document(firebaseAuth.getCurrentUser().getUid())
                .delete();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                sharedPrefManager.signOut();
                new ActivitySwitchManager(MainActivity.this, AuthActivity.class).openActivity();
            }
        });

        handler = new Handler();
        startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkConnected(MainActivity.this)) {
                    timestamp = System.currentTimeMillis();
                    dialog = Utils.showSearching(MainActivity.this);
                    startSearching();
                    final TextView tv_time = dialog.findViewById(R.id.tv_time);
                    dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            stopSearching();
                            dialog.dismiss();
                        }
                    });

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            long curr_time = System.currentTimeMillis();
                            long diff = (curr_time - timestamp) / 1000;
                            if (diff <= 15) {
                                handler.postDelayed(this, 1000);
                                if (diff < 10) {
                                    tv_time.setText("0:0" + diff);
                                } else {
                                    tv_time.setText("0:" + diff);
                                }
                            } else {
                                dialog.dismiss();
                                if (flag == 0) {
                                    stopSearching();
                                    Toast.makeText(MainActivity.this, "There are no users online for a video chat. Please try again after some time", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }, 1000);
                }
            }
        });
    }

    public static void stopSearching() {
        handler.removeCallbacksAndMessages(null);
        MyApplication.getInstance().getFirestore().collection(Constants.COLLECTION_SEARCHING).document(MyApplication.getInstance().getFirebaseAuth().getCurrentUser().getUid())
                .delete();
        if (!channel_id.equals("")) {
            MyApplication.getInstance().getFirestore().collection(Constants.COLLECTION_CHANNELS).document(channel_id)
                    .delete();
        }
    }

    public static void startSearching() {
        MyApplication.getInstance().getFirestore().collection(Constants.COLLECTION_SEARCHING)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshots) {
                        Log.e("OK", "OK");
                        if (snapshots != null && !snapshots.isEmpty()) {
                            List<DocumentSnapshot> documentSnapshots = snapshots.getDocuments();
                            final String userId_1 = documentSnapshots.get(0).getString("userId");
                            final String userName_1 = documentSnapshots.get(0).getString("userName_1");
                            final String channelId = documentSnapshots.get(0).getString("channel");

                            //update channel
                            HashMap<String, Object> updates = new HashMap<>();
                            updates.put("userId_2", MyApplication.getInstance().getFirebaseAuth().getCurrentUser().getUid());
                            updates.put("userName_2", MyApplication.getInstance().getSharedPrefManager().getUser().getUserName());
                            updates.put("joined_at", System.currentTimeMillis());
                            MyApplication.getInstance().getFirestore().collection(Constants.COLLECTION_CHANNELS).document(channelId)
                                    .update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e("Joined channel: ", channelId);
                                            //TODO: START VIDEO CALL
                                            flag = 1;
                                            HashMap<String, String> params = new HashMap<>();
                                            params.put("channel_id", channelId);
                                            params.put("userId_1", userId_1);
                                            params.put("userId_2", userName_1);
                                            params.put("userName_1", MyApplication.getInstance().getFirebaseAuth().getCurrentUser().getUid());
                                            params.put("userName_2", MyApplication.getInstance().getSharedPrefManager().getUser().getUserName());
                                            new ActivitySwitchManager(MyApplication.getInstance().getActivity(), VideoChatViewActivity.class, params).openActivityWthoutFinish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("ERROR: JOIN CHANNEL", e.getMessage());
                                        }
                                    });

                        } else if (snapshots != null && snapshots.getDocuments().size() == 0) {
                            final HashMap<String, Object> channelData = new HashMap<>();
                            channelData.put("created_at", System.currentTimeMillis());
                            channelData.put("userId_1", MyApplication.getInstance().getFirebaseAuth().getCurrentUser().getUid());
                            channelData.put("userName_1", MyApplication.getInstance().getSharedPrefManager().getUser().getUserName());
                            channelData.put("userId_2", "");
                            channelData.put("userName_2", "");

                            MyApplication.getInstance().getFirestore().collection(Constants.COLLECTION_CHANNELS)
                                    .add(channelData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            //TODO : listen to channel updates
                                            channel_id = documentReference.getId();

                                            //Add to queue
                                            HashMap<String, Object> searchingData = new HashMap<>();
                                            searchingData.put("timestamp", System.currentTimeMillis());
                                            searchingData.put("userId", MyApplication.getInstance().getFirebaseAuth().getCurrentUser().getUid());
                                            searchingData.put("channel", channel_id);
                                            MyApplication.getInstance().getFirestore().collection(Constants.COLLECTION_SEARCHING).document(MyApplication.getInstance().getFirebaseAuth().getCurrentUser().getUid())
                                                    .set(searchingData);

                                            MyApplication.getInstance().getFirestore().collection(Constants.COLLECTION_CHANNELS).document(channel_id)
                                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                                            if (e != null) {
                                                                Log.e("ERROR: CHANNEL", e.getMessage());
                                                                return;
                                                            }

                                                            if (snapshot != null) {
                                                                Log.e("snapshot", snapshot.getId() + " " + snapshot.getString("userId_2") + " " + snapshot.getString("userName_2"));
                                                                String participantId = snapshot.getString("userId_2");
                                                                if (participantId != null && !participantId.equals("")) {
                                                                    //TODO: START VIDEO CALL
                                                                    flag = 1;
                                                                    MyApplication.getInstance().getFirestore().collection(Constants.COLLECTION_SEARCHING).document(MyApplication.getInstance().getFirebaseAuth().getCurrentUser().getUid())
                                                                            .delete();
                                                                    HashMap<String, String> params = new HashMap<>();
                                                                    params.put("channel_id", channel_id);
                                                                    params.put("userId_1", snapshot.getString("userId_1"));
                                                                    params.put("userId_2", snapshot.getString("userId_2"));
                                                                    params.put("userName_1", snapshot.getString("userName_1"));
                                                                    params.put("userName_2", snapshot.getString("userName_2"));
                                                                    new ActivitySwitchManager(MyApplication.getInstance().getActivity(), VideoChatViewActivity.class, params).openActivityWthoutFinish();

                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //TODO: Channel Creating Failed
                                            Log.e("ERROR: ADD CHANNEL", e.getMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}

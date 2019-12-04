package com.example.cargotransportationdriverapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cargotransportationdriverapp.R;
import com.example.cargotransportationdriverapp.adapter.AdapterRidesHistory;
import com.example.cargotransportationdriverapp.controllers.MyFirebaseDatabase;
import com.example.cargotransportationdriverapp.models.RideDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRidesHistory extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    Context context;
    View view;

    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView ridesHistoryRecyclerView;
    private AdapterRidesHistory adapterRidesHistory;

    private List<RideDetails> ridesHistoryList;
    private ValueEventListener ridesValueEventListener;

    private FirebaseUser firebaseUser;

    public FragmentRidesHistory() {
        // Required empty public constructor
        ridesHistoryList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_rides_history, container, false);

            ridesHistoryRecyclerView = view.findViewById(R.id.rides_history_recycler_view);
            ridesHistoryRecyclerView.setHasFixedSize(true);
            ridesHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapterRidesHistory = new AdapterRidesHistory(context, ridesHistoryList);
            ridesHistoryRecyclerView.setAdapter(adapterRidesHistory);

            initSwipeRefreshLayout();


        }
        return view;
    }

    @Override
    public void onRefresh() {
        loadRidesHistoryRecyclerViewData();
    }

    private void initSwipeRefreshLayout() {
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        /*
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                loadRidesHistoryRecyclerViewData();
            }
        });
    }

    private void loadRidesHistoryRecyclerViewData() {

        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);
        removeRidesValueEventListener();
        ridesValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Stopping swipe refresh
                mSwipeRefreshLayout.setRefreshing(false);

                ridesHistoryList.clear();
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {

                    try {

                        Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                        for (DataSnapshot snapshot : snapshots) {
                            try {

                                RideDetails rideDetails = snapshot.getValue(RideDetails.class);
                                if (rideDetails != null)
                                    if (rideDetails.getDriverId().equals(firebaseUser.getUid()))
                                        ridesHistoryList.add(rideDetails);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                adapterRidesHistory.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Stopping swipe refresh
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        MyFirebaseDatabase.RIDES_REFERENCE.addListenerForSingleValueEvent(ridesValueEventListener);

    }

    private void removeRidesValueEventListener() {

        if (ridesValueEventListener != null)
            MyFirebaseDatabase.RIDES_REFERENCE.removeEventListener(ridesValueEventListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeRidesValueEventListener();
    }
}

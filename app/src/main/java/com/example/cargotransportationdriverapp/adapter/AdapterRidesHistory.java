package com.example.cargotransportationdriverapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cargotransportationdriverapp.CommonFunctionsClass;
import com.example.cargotransportationdriverapp.R;
import com.example.cargotransportationdriverapp.models.RideDetails;

import java.util.List;

public class AdapterRidesHistory extends RecyclerView.Adapter<AdapterRidesHistory.Holder> {

    Context context;
    List<RideDetails> list;

    public AdapterRidesHistory(Context context, List<RideDetails> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterRidesHistory.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rides_history_card, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRidesHistory.Holder holder, int position) {

        final RideDetails rideDetails = list.get(holder.getAdapterPosition());
        holder.placeRideCreatedDate.setText(rideDetails.getRideCreatedDate());
        holder.placePickupLoc.setText(rideDetails.getPickUpAddress());
        holder.placeDropOffLoc.setText(rideDetails.getDropOffAddress());
        holder.placeRideStatus.setText(CommonFunctionsClass.getRideStringStatus(rideDetails.getRideStatus()));
        holder.placeVehicleType.setText(CommonFunctionsClass.stringVehicleName(rideDetails.getVehicle()));
        holder.placeRideFare.setText(rideDetails.getCollectedRideFare());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView placeRideCreatedDate, placePickupLoc, placeDropOffLoc, placeRideStatus,
                placeVehicleType, placeRideFare;

        public Holder(@NonNull View itemView) {
            super(itemView);

            placeRideCreatedDate = itemView.findViewById(R.id.placeRideCreatedDate);
            placePickupLoc = itemView.findViewById(R.id.placePickupLoc);
            placeDropOffLoc = itemView.findViewById(R.id.placeDropOffLoc);
            placeRideStatus = itemView.findViewById(R.id.placeRideStatus);
            placeVehicleType = itemView.findViewById(R.id.placeVehicleType);
            placeRideFare = itemView.findViewById(R.id.placeRideFare);

        }
    }
}

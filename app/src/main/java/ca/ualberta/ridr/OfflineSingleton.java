package ca.ualberta.ridr;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

/**
 * A singleton to store the things that need to be done once
 *  back online
 */

public class OfflineSingleton {
    private static final OfflineSingleton instance =
            new OfflineSingleton();
    private ArrayList<Request> riderRequests;
    private ArrayList<Request> driverRequests;
    private ArrayList<Rider> riderList;

    private OfflineSingleton() {
        this.riderRequests = new ArrayList<>();
        this.driverRequests = new ArrayList<>();
        this.riderList = new ArrayList<>();
    };

    public static OfflineSingleton getInstance() {
        return instance;
    }

    public void addRiderRequest(Request request) {
        riderRequests.add(request);
    }

    public ArrayList<Request> getRiderRequests () {
        return riderRequests;
    }

    public void clearRiderRequests() {
        riderRequests.clear();
    }

    public void addDriverAcceptance(Request request) {
        driverRequests.add(request);
    }

    public boolean isPendingAcceptance() {
        return driverRequests.size() > 0;
    }

    public ArrayList<Request> getDriverRequests() {
        return driverRequests;
    }

    public void clearDriverRequests() {
        driverRequests.clear();
    }

    public ArrayList<Rider> getRiderList() {
        return riderList;
    }

    public void addRider(Rider rider) {
        riderList.add(rider);
    }

    public boolean isPendingNotification() {
        return riderList.size() > 0;
    }

    public void clearRiderList() {
        riderList.clear();
    }

    public boolean isPendingRequest() {
        return riderRequests.size() > 0;
    }
}

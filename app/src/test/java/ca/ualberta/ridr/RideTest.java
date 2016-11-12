package ca.ualberta.ridr;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by Justin on 2016-11-03.
 */

public class RideTest {
    @Test
    public void testRideEquality() {
        Vehicle vehicle = new Vehicle(1994, "chevy", "truck");
        Driver driver = new Driver("Jeff", new Date(), "111", "email", "8675309", vehicle, "123");
        Rider rider = new Rider("Steve", new Date(), "321", "goodemail", "9999999");

        Ride ride = new Ride(driver, rider, "University of Alberta", "West Edmonton Mall", new Date(), pickupCoords, dropOffCoords);
        assertTrue(ride.equals(ride));
    }
}

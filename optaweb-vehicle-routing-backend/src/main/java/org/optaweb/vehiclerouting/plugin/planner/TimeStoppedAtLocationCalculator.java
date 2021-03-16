package org.optaweb.vehiclerouting.plugin.planner;

import org.springframework.stereotype.Service;

@Service
public class TimeStoppedAtLocationCalculator {

    public static final long MINUTES_TAKEN_TO_DELIVER_PARCEL = 5L;

    /**
     * Given a travel time in millis add an additional X minutes to represent the time taken to collect/deliver a parcel
     * @param travelTimeInMillis
     * @return
     */
    public long calculate(long travelTimeInMillis) {

        // if the distance if over 15 seconds then add 5 minutes for the stop
        // otherwise it means that we're stopping in one place to deliver multiple packages (i.e. an office)
        if (travelTimeInMillis > 15 * 1000) {
            // add 5 minutes to emulate the time spent at the customer
            travelTimeInMillis += (MINUTES_TAKEN_TO_DELIVER_PARCEL * 60 * 1000);
        }
        return travelTimeInMillis;
    }
}

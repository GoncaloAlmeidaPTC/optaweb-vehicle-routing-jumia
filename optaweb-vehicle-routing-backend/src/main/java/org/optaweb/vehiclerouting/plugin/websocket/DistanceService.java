package org.optaweb.vehiclerouting.plugin.websocket;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.plugin.planner.TimeStoppedAtLocationCalculator;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistanceService {

    private static final Logger logger = LoggerFactory.getLogger(DistanceService.class);

    private final DistanceMatrix distanceMatrix;

    private final TimeStoppedAtLocationCalculator timeStoppedAtLocationCalculator;

    @Autowired
    public DistanceService(DistanceMatrix distanceMatrix, TimeStoppedAtLocationCalculator timeStoppedAtLocationCalculator) {
        this.distanceMatrix = distanceMatrix;
        this.timeStoppedAtLocationCalculator = timeStoppedAtLocationCalculator;
    }

    // TODO ugly hack just to get the total distance per route
    public void calculateTravelTime(PortableRoutingPlan portableRoutingPlan) {

        final List<PortableRoute> routes = portableRoutingPlan.getRoutes();

        for (PortableRoute route : routes) {

            final List<PortableLocation> visits = route.getVisits();

            Location fromLocation = null;
            long totalDistanceForRoute = 0L;

            final Location depotLocation = new Location(route.getDepot().getId(),
                                                        new Coordinates(route.getDepot().getLatitude(), route.getDepot().getLongitude()),
                                                        route.getDepot().getDescription());
            for (int i = 0; i < visits.size(); i++) {

                PortableLocation nextVisit = visits.get(i);
                Location toLocation = new Location(nextVisit.getId(),
                                                   new Coordinates(nextVisit.getLatitude(), nextVisit.getLongitude()),
                                                   nextVisit.getDescription());
                if (i == 0) {
                    fromLocation = depotLocation;
                }
                final Distance distanceBetweenPoints = distanceMatrix.calculateOrRestoreDistance(fromLocation, toLocation);

                final long totalDistanceBetweenPoints = timeStoppedAtLocationCalculator.calculate(distanceBetweenPoints.millis());
                totalDistanceForRoute += totalDistanceBetweenPoints;

                nextVisit.setDescription(Distance.ofMillis(totalDistanceBetweenPoints) + "; " + nextVisit.getDescription());

                logger.trace("Distance between {} and {} is {}", fromLocation, toLocation, distanceBetweenPoints);

                if (i == visits.size() - 1) {
                    totalDistanceForRoute += distanceMatrix.calculateOrRestoreDistance(toLocation, depotLocation).millis();
                }

                fromLocation = toLocation;
            }

            final Distance totalDistance = Distance.ofMillis(totalDistanceForRoute);

            route.setDistance(PortableDistance.fromDistance(totalDistance));
        }
    }
}

package org.optaweb.vehiclerouting.plugin.websocket;

import java.util.List;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.service.location.LocationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/api/locations")
    public void addLocations(@RequestBody List<PortableLocation> request) {

        for (PortableLocation location : request) {
            locationService.createLocation(
                    new Coordinates(location.getLatitude(), location.getLongitude()),
                    location.getDescription());
        }
    }
}

/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaweb.vehiclerouting.plugin.planner;

import org.optaweb.vehiclerouting.plugin.planner.domain.DistanceMap;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;

import java.util.Objects;

/**
 * Provides distances to {@link PlanningLocation}s by reading from a {@link DistanceMatrixRow}.
 */
public class DistanceMapImpl implements DistanceMap {

    private final DistanceMatrixRow distanceMatrixRow;

    public DistanceMapImpl(DistanceMatrixRow distanceMatrixRow) {
        this.distanceMatrixRow = Objects.requireNonNull(distanceMatrixRow);
    }

    @Override
    public long distanceTo(PlanningLocation location) {

        long travelTimeInMillis = distanceMatrixRow.distanceTo(location.getId()).millis();

        // if the distance if over 30 seconds then add 5 minutes for the stop
        // otherwise it means that we're stopping in one place to deliver multiple packages (i.e. an office)
        if (travelTimeInMillis > 30 * 1000) {
            // add 5 minutes to emulate the time spent at the customer
            travelTimeInMillis += (5L * 60 * 1000);
        }
        return travelTimeInMillis;
    }
}

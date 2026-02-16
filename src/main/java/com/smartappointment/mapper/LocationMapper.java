package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.location.responses.LocationResponse;
import com.smartappointment.model.entity.Location;

@Component
public class LocationMapper {

    public LocationResponse toResponse(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCity(),
                location.isActive(),
                location.getCreatedAt(),
                location.getUpdatedAt());
    }
}

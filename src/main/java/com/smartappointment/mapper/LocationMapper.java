package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.location.requests.CreateLocationRequest;
import com.smartappointment.dto.location.requests.UpdateLocationRequest;
import com.smartappointment.dto.location.responses.LocationResponse;
import com.smartappointment.model.entity.Location;

@Component
public class LocationMapper {

    public Location toEntity(CreateLocationRequest request) {
        if (request == null) {
            return null;
        }

        return Location.builder()
                .name(request.name())
                .address(request.address())
                .city(request.city())
                .active(true)
                .build();
    }

    public void updateEntityFromRequest(Location location, UpdateLocationRequest request) {
        if (location == null || request == null) {
            return;
        }

        if (request.name() != null) {
            location.setName(request.name());
        }
        if (request.address() != null) {
            location.setAddress(request.address());
        }
        if (request.city() != null) {
            location.setCity(request.city());
        }
    }

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

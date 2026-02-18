package com.smartappointment.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smartappointment.dto.location.requests.CreateLocationRequest;
import com.smartappointment.dto.location.requests.UpdateLocationRequest;
import com.smartappointment.dto.location.responses.LocationResponse;

public interface ILocationService {

    LocationResponse createLocation(CreateLocationRequest request);

    Page<LocationResponse> getAllLocations(Pageable pageable);

    Page<LocationResponse> getActiveLocations(Pageable pageable);

    Page<LocationResponse> getInactiveLocations(Pageable pageable);

    LocationResponse getLocationById(Long id);

    Page<LocationResponse> getLocationsByCity(String city, Pageable pageable);

    Page<LocationResponse> getActiveLocationsByCity(String city, Pageable pageable);

    Page<LocationResponse> searchLocationsByName(String name, Pageable pageable);

    boolean existsByNameAndCity(String name, String city);

    LocationResponse updateLocation(Long id, UpdateLocationRequest request);

    void softDeleteLocation(Long id);

    void hardDeleteLocation(Long id);

    void activateLocation(Long id);
}

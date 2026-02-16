package com.smartappointment.service.interfaces;

import com.smartappointment.dto.location.requests.CreateLocationRequest;
import com.smartappointment.dto.location.requests.UpdateLocationRequest;
import com.smartappointment.dto.location.responses.LocationResponse;

import java.util.List;

public interface ILocationService {

    LocationResponse createLocation(CreateLocationRequest request);

    List<LocationResponse> getAllLocations();

    List<LocationResponse> getActiveLocations();

    List<LocationResponse> getInactiveLocations();

    LocationResponse getLocationById(Long id);

    List<LocationResponse> getLocationsByCity(String city);

    List<LocationResponse> getActiveLocationsByCity(String city);

    List<LocationResponse> searchLocationsByName(String name);

    boolean existsByNameAndCity(String name, String city);

    LocationResponse updateLocation(Long id, UpdateLocationRequest request);

    void softDeleteLocation(Long id);

    void hardDeleteLocation(Long id);

    void activateLocation(Long id);
}

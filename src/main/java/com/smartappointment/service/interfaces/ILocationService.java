package com.smartappointment.service.interfaces;

import com.smartappointment.dto.location.LocationRequest;
import com.smartappointment.dto.location.LocationResponse;

import java.util.List;

public interface ILocationService {

    LocationResponse createLocation(LocationRequest request);

    List<LocationResponse> getAllLocations();

    List<LocationResponse> getActiveLocations();

    List<LocationResponse> getInactiveLocations();

    LocationResponse getLocationById(Long id);

    List<LocationResponse> getLocationsByCity(String city);

    List<LocationResponse> getActiveLocationsByCity(String city);

    List<LocationResponse> searchLocationsByName(String name);

    boolean existsByNameAndCity(String name, String city);

    LocationResponse updateLocation(Long id, LocationRequest request);

    void softDeleteLocation(Long id);

    void hardDeleteLocation(Long id);
}

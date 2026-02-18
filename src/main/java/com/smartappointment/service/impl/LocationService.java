package com.smartappointment.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartappointment.dto.location.requests.CreateLocationRequest;
import com.smartappointment.dto.location.requests.UpdateLocationRequest;
import com.smartappointment.dto.location.responses.LocationResponse;
import com.smartappointment.exception.BadRequestException;
import com.smartappointment.exception.ResourceNotFoundException;
import com.smartappointment.mapper.LocationMapper;
import com.smartappointment.model.entity.Location;
import com.smartappointment.repository.LocationRepository;
import com.smartappointment.service.interfaces.ILocationService;

@Service
public class LocationService implements ILocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    // ==================== CREATE ====================

    @Override
    @Transactional
    public LocationResponse createLocation(CreateLocationRequest request) {
        if (locationRepository.existsByNameAndCity(request.name(), request.city())) {
            throw new BadRequestException(
                    "Location already exists: " + request.name() + " in " + request.city());
        }

        Location location = locationMapper.toEntity(request);

        Location saved = locationRepository.save(location);
        return locationMapper.toResponse(saved);
    }

    // ==================== GET OPERATIONS ====================

    @Override
    public Page<LocationResponse> getAllLocations(Pageable pageable) {
        return locationRepository.findAll(pageable).map(locationMapper::toResponse);
    }

    @Override
    public Page<LocationResponse> getActiveLocations(Pageable pageable) {
        return locationRepository.findByActiveTrue(pageable).map(locationMapper::toResponse);
    }

    @Override
    public Page<LocationResponse> getInactiveLocations(Pageable pageable) {
        return locationRepository.findByActiveFalse(pageable).map(locationMapper::toResponse);
    }

    @Override
    public LocationResponse getLocationById(Long id) {
        Location location = findLocationOrThrow(id);
        return locationMapper.toResponse(location);
    }

    @Override
    public Page<LocationResponse> getLocationsByCity(String city, Pageable pageable) {
        return locationRepository.findByCity(city, pageable).map(locationMapper::toResponse);
    }

    @Override
    public Page<LocationResponse> getActiveLocationsByCity(String city, Pageable pageable) {
        return locationRepository.findByCityAndActiveTrue(city, pageable).map(locationMapper::toResponse);
    }

    @Override
    public Page<LocationResponse> searchLocationsByName(String name, Pageable pageable) {
        return locationRepository.findByNameContainingIgnoreCase(name, pageable).map(locationMapper::toResponse);
    }

    @Override
    public boolean existsByNameAndCity(String name, String city) {
        return locationRepository.existsByNameAndCity(name, city);
    }

    // ==================== UPDATE ====================

    @Override
    @Transactional
    public LocationResponse updateLocation(Long id, UpdateLocationRequest request) {
        Location location = findLocationOrThrow(id);
        locationMapper.updateEntityFromRequest(location, request);

        Location updated = locationRepository.save(location);
        return locationMapper.toResponse(updated);
    }

    // ==================== STATUS OPERATIONS ====================

    @Override
    @Transactional
    public void activateLocation(Long id) {
        Location location = findLocationOrThrow(id);
        location.setActive(true);
        locationRepository.save(location);
    }

    // ==================== DELETE OPERATIONS ====================

    @Override
    @Transactional
    public void softDeleteLocation(Long id) {
        Location location = findLocationOrThrow(id);
        location.setActive(false);
        locationRepository.save(location);
    }

    @Override
    @Transactional
    public void hardDeleteLocation(Long id) {
        Location location = findLocationOrThrow(id);
        locationRepository.delete(location);
    }

    // ==================== HELPER METHODS ====================

    private Location findLocationOrThrow(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }
}

package com.smartappointment.model.enums;

public enum AppointmentStatus {

    SCHEDULED, // Initial state when user creates appointment
    CONFIRMED, // Provider accepted the appointment
    COMPLETED, // Appointment successfully finished
    REJECTED, // Provider declined the appointment
    CANCELLED, // Cancelled by user or admin
    MISSED // User did not show up
}

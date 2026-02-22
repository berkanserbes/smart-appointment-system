package com.smartappointment.service.interfaces;

/**
 * Abstraction for outbound notification/message delivery.
 * Implementations can target different channels (email, SMS, push, etc.).
 * Consumers depend on this interface, never on a concrete sender class,
 * satisfying the Dependency Inversion Principle.
 */
public interface ISender {

    /**
     * Sends a message to the specified recipient.
     *
     * @param to      recipient address (e.g. email address, phone number)
     * @param subject subject or title of the message
     * @param body    full message content (plain text or markup depending on implementation)
     */
    void send(String to, String subject, String body);
}

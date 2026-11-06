package com.elibrary.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpiryScheduler {

    private final CirculationService circulationService;

    public ReservationExpiryScheduler(CirculationService circulationService) {
        this.circulationService = circulationService;
    }

    @Scheduled(fixedDelayString = "${elibrary.reservations.expiry-check-ms:60000}")
    public void processExpiredReservations() {
        circulationService.processExpiredReservations();
    }
}

package com.cnweb.bookingapi.controller;

import com.cnweb.bookingapi.dtos.request.BookingDto;
import com.cnweb.bookingapi.model.Booking;
import com.cnweb.bookingapi.model.BookingStatus;
import com.cnweb.bookingapi.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Booking>> getAllUserBookings(@PathVariable String userId) {
        return ResponseEntity.ok(bookingService.allUserBookings(userId));
    }

    @GetMapping("/{hotelId}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<List<Booking>> getAllHotelBookings(@PathVariable String hotelId) {
        return ResponseEntity.ok(bookingService.allHotelBookings(hotelId));
    }

    @PostMapping("/{hotelId}")
    public ResponseEntity<Booking> createBooking(@PathVariable String hotelId,
                                                 @Valid @RequestBody BookingDto bookingDto) {
        return ResponseEntity.ok(bookingService.createReservation(hotelId, bookingDto));
    }

    @PostMapping("/{bookingId}")
    public void confirmBooking(@PathVariable String bookingId, @RequestParam BookingStatus status) {
        bookingService.confirmBooking(bookingId, status);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable String bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.ok("The booking has been cancelled successfully");
    }
}

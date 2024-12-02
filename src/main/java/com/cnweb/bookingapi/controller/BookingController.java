package com.cnweb.bookingapi.controller;

import com.cnweb.bookingapi.dtos.request.BookingDto;
import com.cnweb.bookingapi.model.Booking;
import com.cnweb.bookingapi.model.BookingStatus;
import com.cnweb.bookingapi.service.BookingService;
import com.cnweb.bookingapi.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final EmailService emailService;

    @GetMapping("/users/{userId}")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Booking>> getBookingsOfUser(@PathVariable String userId) {
        return ResponseEntity.ok(bookingService.allUserBookings(userId));
    }

//    @GetMapping("/hotels/{hotelId}")
//    @PreAuthorize("hasRole('HOTEL')")
//    public ResponseEntity<List<Booking>> getBookingsOfHotel(@PathVariable String hotelId) {
//        return ResponseEntity.ok(bookingService.allHotelBookings(hotelId));
//    }

    @GetMapping("/hotels/{hotelId}/pending")
//    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<List<Booking>> getPendingBookings(@PathVariable String hotelId) {
        return ResponseEntity.ok(bookingService.pendingBookings(hotelId));
    }

    @GetMapping("/hotels/{hotelId}/recently")
//    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<List<Booking>> getRecentBookings(@PathVariable String hotelId) {
        return ResponseEntity.ok(bookingService.getRecentBookings(hotelId));
    }

    @GetMapping("/hotels/{hotelId}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable String hotelId,
                                                             @RequestParam(required = false) String status) {
        return ResponseEntity.ok(bookingService.getBookingsByStatus(hotelId, status));
    }

    @PostMapping("/hotels/{hotelId}")
    public ResponseEntity<Booking> createBooking(@PathVariable String hotelId,
                                                 @Valid @RequestBody BookingDto bookingDto) {
        return ResponseEntity.ok(bookingService.createReservation(hotelId, bookingDto));
    }

    @PutMapping("/{bookingId}")
    public void confirmBooking(@RequestBody Booking booking) throws Exception {
        bookingService.confirmBooking(booking);
        emailService.sendConfirmBookingEmail(booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable String bookingId,
                                                       @RequestBody Map<String, String> updatedStatus) throws MessagingException, UnsupportedEncodingException {
        Booking booking = bookingService.updateBookingStatus(bookingId, updatedStatus);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        if (booking.getBookingStatus().equals(BookingStatus.PAID)) emailService.sendConfirmBookingEmail(booking);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable String bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.ok("The booking has been cancelled successfully");
    }
}

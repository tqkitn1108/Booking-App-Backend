package com.cnweb.bookingapi.repository;

import com.cnweb.bookingapi.model.Booking;
import com.cnweb.bookingapi.model.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByHotelIdAndBookingStatus(String hotelId, BookingStatus bookingStatus);
    List<Booking> findByEmail(String email);
}

package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.dtos.request.BookingDto;
import com.cnweb.bookingapi.model.*;
import com.cnweb.bookingapi.repository.BookingRepository;
import com.cnweb.bookingapi.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final MongoTemplate mongoTemplate;

    public List<Booking> allUserBookings(String userId) {
        String userEmail = Objects.requireNonNull(mongoTemplate.findById(userId, User.class)).getEmail();
        return bookingRepository.findByEmail(userEmail);
    }

    public List<Booking> pendingBookings(String hotelId) {
        return Objects.requireNonNull(bookingRepository.findByHotelIdAndBookingStatus(hotelId, BookingStatus.valueOf("PENDING")));
    }

    public List<Booking> getBookingsByStatus(String hotelId, String status) {
        if (status == null) return Objects.requireNonNull(mongoTemplate.findById(hotelId, Hotel.class)).getBookings();
        return bookingRepository.findByHotelIdAndBookingStatus(hotelId, BookingStatus.valueOf(status));
    }

    public Booking createReservation(String hotelId, BookingDto bookingDto) {
        Booking booking = bookingDto.toBooking();
        List<Room> roomList = bookingDto.getRoomIds().stream().map(roomId ->
                mongoTemplate.findById(roomId, Room.class)).toList();
        booking.setHotelId(hotelId);
        booking.setRooms(roomList);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setIsRated(false);
        bookingRepository.save(booking);
        mongoTemplate.update(Hotel.class)
                .matching(Criteria.where("id").is(hotelId))
                .apply(new Update().push("bookings", booking))
                .first();
        return booking;
    }

    public void confirmBooking(Booking booking) {
        if (booking.getBookingStatus() == BookingStatus.ACCEPTED
                || booking.getBookingStatus() == BookingStatus.PAID) {
            List<LocalDate> stayDateList = new ArrayList<>();
            LocalDate date = booking.getCheckInDate();
            while (!date.isAfter(booking.getCheckOutDate())) {
                stayDateList.add(date);
                date = date.plusDays(1);
            }
            booking.getRooms().forEach(room -> {
                if (!room.isAvailableBetween(booking.getCheckInDate(), booking.getCheckOutDate()))
                    try {
                        throw new Exception("Some of these rooms have been placed");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            });
            booking.getRooms().forEach(room -> {
                room.deletePastDate();
                room.getUnavailableDates().addAll(stayDateList);
                if (room.getBookingIds() == null) room.setBookingIds(new ArrayList<String>());
                room.getBookingIds().add(booking.getId());
                roomRepository.save(room);
            });
        }
        bookingRepository.save(booking);
    }

    public void deleteBooking(String bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    public Booking updateBookingStatus(String bookingId, Map<String, String> updatedStatus) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            return null;
        }
        Booking booking = optionalBooking.get();
        booking.setBookingStatus(BookingStatus.valueOf(updatedStatus.get("status")));
        confirmBooking(booking);
        return booking;
    }

    public List<Booking> getRecentBookings(String hotelId) {
        List<BookingStatus> excludedStatuses = Arrays.asList(BookingStatus.COMPLETED, BookingStatus.CANCELLED, BookingStatus.PENDING);
        return bookingRepository
                .findByHotelIdAndBookingStatusNotInOrderByIdDesc(hotelId, excludedStatuses);
    }
}

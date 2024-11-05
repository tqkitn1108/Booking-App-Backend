package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.dtos.request.BookingDto;
import com.cnweb.bookingapi.model.Booking;
import com.cnweb.bookingapi.model.BookingStatus;
import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.model.Room;
import com.cnweb.bookingapi.repository.BookingRepository;
import com.cnweb.bookingapi.repository.HotelRepository;
import com.cnweb.bookingapi.repository.RoomRepository;
import com.cnweb.bookingapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    public BookingService(BookingRepository bookingRepository, UserRepository userRepository,
                          HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }
    public List<Booking> allUserBookings(String userId) {
        return userRepository.findById(userId).orElseThrow().getBookings();
    }
    public List<Booking> allHotelBookings(String hotelId) {
        return hotelRepository.findById(hotelId).orElseThrow().getBookings();
    }
    public Booking reservation(String hotelId, BookingDto bookingDto) {
        Booking booking = bookingDto.toBooking();
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow();
        List<Room> roomList = bookingDto.getRoomIds().stream().map(roomId ->
                roomRepository.findById(roomId).orElseThrow()).toList();
        booking.setHotel(hotel);
        booking.setRooms(roomList);
        booking.setBookingStatus(BookingStatus.PENDING);
        List<LocalDate> stayDateList = new ArrayList<>();
        LocalDate date = bookingDto.getCheckInDate();
        while (!date.isAfter(bookingDto.getCheckOutDate())) {
            stayDateList.add(date);
            date = date.plusDays(1);
        }
        roomList.forEach(room -> {
            room.deletePastDate();
            room.getUnavailableDates().addAll(stayDateList);
        });
        return bookingRepository.save(booking);
    }

    public Booking confirmBooking(String bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        booking.setBookingStatus(status);
        return bookingRepository.save(booking);
    }

    public void deleteBooking(String bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}

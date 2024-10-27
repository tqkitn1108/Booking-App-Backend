package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.model.Room;
import com.cnweb.bookingapi.repository.HotelRepository;
import com.cnweb.bookingapi.repository.RoomRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class RoomService {
    private RoomRepository roomRepository;
    private HotelRepository hotelRepository;
    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository){
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }
    public List<Room> allRooms(ObjectId hotelId) {
        return Objects.requireNonNull(hotelRepository.findById(hotelId).orElse(null)).getRooms();
    }
    public List<Room> availableRooms(ObjectId hotelId, LocalDate checkIn, LocalDate checkOut) {
        Hotel hotel = Objects.requireNonNull(hotelRepository.findById(hotelId).orElse(null));
        return null;
    }
}

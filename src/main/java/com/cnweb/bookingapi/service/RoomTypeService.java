package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.dtos.request.RoomTypeDto;
import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.model.Room;
import com.cnweb.bookingapi.model.RoomType;
import com.cnweb.bookingapi.repository.HotelRepository;
import com.cnweb.bookingapi.repository.RoomRepository;
import com.cnweb.bookingapi.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypeService {
    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final MongoTemplate mongoTemplate;

    public List<RoomType> allRoomTypes(String hotelId) {
        return hotelRepository.findById(hotelId).orElseThrow().getRoomTypes();
    }

    public RoomType singleRoomType(String id) {
        return roomTypeRepository.findById(id).orElse(null);
    }

    public List<RoomType> availableRoomTypes(String hotelId, LocalDate checkIn, LocalDate checkOut) {
        return allRoomTypes(hotelId).stream()
                .filter(roomType -> roomType.countAvailableRooms(checkIn, checkOut) > 0).toList();
    }

    public List<Room> availableRooms(String roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        return roomTypeRepository.findById(roomTypeId).orElseThrow().getRooms().stream()
                .filter(room -> room.isAvailableBetween(checkIn, checkOut)).toList();
    }

    public RoomType newRoomType(String hotelId, RoomTypeDto roomTypeDto) {
        List<Room> rooms = roomTypeDto.getRoomNumbers().stream()
                .map(roomNumber -> roomRepository.save(new Room(roomNumber))).toList();
        RoomType roomType = roomTypeDto.toRoomType();
        roomType.setRooms(rooms);
        roomType = roomTypeRepository.save(roomType);
        mongoTemplate.update(Hotel.class)
                .matching(Criteria.where("id").is(hotelId))
                .apply(new Update().push("roomTypes", roomType))
                .first();
        String roomTypeId = roomType.getId();
        roomType.getRooms().forEach(room -> {
            room.setRoomTypeId(roomTypeId);
            roomRepository.save(room);
        });
        return roomType;
    }

    public RoomType updateRoomType(String id, RoomType roomType) {
        roomType.setId(id);
        return roomTypeRepository.save(roomType);
    }

    public void deleteRoomType(String hotelId, String roomTypeId) {
        mongoTemplate.update(Hotel.class)
                .matching(Criteria.where("id").is(hotelId))
                .apply(new Update().pull("roomTypes", roomTypeRepository.findById(roomTypeId).orElseThrow()))
                .first();
        roomTypeRepository.deleteById(roomTypeId);
    }
}

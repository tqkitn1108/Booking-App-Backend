package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.model.Room;
import com.cnweb.bookingapi.repository.HotelRepository;
import com.cnweb.bookingapi.repository.RoomRepository;
import com.cnweb.bookingapi.utils.validate.CheckAvailableRoom;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final MongoTemplate mongoTemplate;

    public List<Room> allRooms(ObjectId hotelId) {
        return hotelRepository.findById(hotelId).orElseThrow().getRooms();
    }

    public Room singleRoom(ObjectId id) {
        return roomRepository.findById(id).orElse(null);
    }

    public List<Room> availableRooms(ObjectId hotelId, LocalDate checkIn, LocalDate checkOut) {
        return allRooms(hotelId).stream()
                .filter(room -> CheckAvailableRoom.checkAvailable(room, checkIn, checkOut)).toList();
    }

    public Room newRoom(ObjectId hotelId, Room room) {
        mongoTemplate.update(Hotel.class)
                .matching(Criteria.where("id").is(hotelId))
                .apply(new Update().push("rooms", room))
                .first();
        return roomRepository.save(room);
    }
    public Room updateRoom(ObjectId roomId, Room room) {
        Room currentRoom = roomRepository.findById(roomId).orElseThrow();
        Class<? extends Room> roomClass = room.getClass();
        for (Field field : roomClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(room);
                if (value != null) {
                    field.set(currentRoom, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return roomRepository.save(currentRoom);
    }

    public void deleteRoom(ObjectId hotelId, ObjectId roomId) {
        mongoTemplate.update(Hotel.class)
                .matching(Criteria.where("id").is(hotelId))
                .apply(new Update().pull("rooms", roomRepository.findById(roomId).orElseThrow()))
                .first();
        roomRepository.deleteById(roomId);
    }
}

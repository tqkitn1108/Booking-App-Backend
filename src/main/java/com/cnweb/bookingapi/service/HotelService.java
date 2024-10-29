package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.model.Room;
import com.cnweb.bookingapi.repository.HotelRepository;
import com.cnweb.bookingapi.utils.validate.CheckAvailableRoom;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final MongoTemplate mongoTemplate;
    public Hotel singleHotel(ObjectId id) {
        return hotelRepository.findById(id).orElse(null);
    }
    public List<Hotel> allHotels() {
        return hotelRepository.findAll();
    }

    public List<Hotel> availableHotels(String locality, LocalDate checkIn, LocalDate checkOut,
                                       Integer adults, Integer children, Integer roomQuantity) {
        List<Hotel> hotelList = hotelRepository.findAllByLocality(locality);
        return hotelList.stream().filter(hotel -> {
            int maxPeople = 0, numOfRooms = 0;
            for (Room room : hotel.getRooms()) {
                if (CheckAvailableRoom.checkAvailable(room, checkIn, checkOut)) {
                    maxPeople += room.getCapacity();
                    numOfRooms++;
                }
            }
            return maxPeople >= adults || numOfRooms >= roomQuantity - 1;
        }).toList();
    }

    public void newHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }
    public Hotel updatedHotel(ObjectId id, Hotel hotel) {
        Hotel existingHotel = hotelRepository.findById(id).orElse(hotel);
        Class<? extends Hotel> hotelClass = hotel.getClass();
        for (Field field : hotelClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(hotel);
                if (value != null) {
                    field.set(existingHotel, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return hotelRepository.save(existingHotel);
    }
    public void deletedHotel(ObjectId id) {
        hotelRepository.deleteById(id);
    }
}
package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.dtos.request.HotelDto;
import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.model.RoomType;
import com.cnweb.bookingapi.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final MongoTemplate mongoTemplate;

    public Hotel singleHotel(String id) {
        return hotelRepository.findById(id).orElse(null);
    }

    public Page<Hotel> allHotels(String dest, int page, int size) {
        Pageable paging = PageRequest.of(page, size, Sort.by("rating").descending());
        Page<Hotel> hotelsPage;
        if (dest == null)
            hotelsPage = hotelRepository.findAll(paging);
        else
            hotelsPage = hotelRepository.findByDest(dest, paging);
        return hotelsPage;
    }

    public Page<Hotel> availableHotels(int page, int size, Map<String, String> filters) {
        String dest = filters.get("dest");
        LocalDate checkIn = LocalDate.parse(filters.get("checkin"));
        LocalDate checkOut = LocalDate.parse(filters.get("checkout"));
        int adults = Integer.parseInt(filters.get("adults"));
        int children = Integer.parseInt(filters.get("children"));
        int noRooms = Integer.parseInt(filters.get("no_rooms"));
        List<Hotel> hotelList = hotelRepository.findByDest(dest).stream().filter(hotel -> {
            int maxPeople = 0, numOfRooms = 0;
            for (RoomType roomType : hotel.getRoomTypes()) {
                int numAvailableRooms = roomType.countAvailableRooms(checkIn, checkOut);
                maxPeople += numAvailableRooms * roomType.getCapacity();
                numOfRooms += numAvailableRooms;
            }
            return maxPeople >= adults + children / 4 || numOfRooms >= noRooms - 1;
        }).toList();
        return new PageImpl<>(hotelList,
                PageRequest.of(page, size, Sort.by("rating").descending()), hotelList.size());
    }

    public Hotel newHotel(HotelDto hotelDto) {
        Hotel hotel = hotelDto.toHotel();
        hotel.setRating(0F);
        return hotelRepository.save(hotel);
    }

    public Hotel updatedHotel(String id, Hotel hotel) {
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

    public void deletedHotel(String id) {
        hotelRepository.deleteById(id);
    }

    public Map<String, Integer> countByDest(List<String> destinations) {
        Map<String, Integer> map = new HashMap<>();
        destinations.forEach(dest -> map.put(dest, hotelRepository.findByDest(dest).size()));
        return map;
    }

    public Map<String, Integer> countByType(List<String> types) {
        Map<String, Integer> map = new HashMap<>();
        types.forEach(type -> map.put(type, hotelRepository.findByType(type).size()));
        return map;
    }

    public Page<Hotel> filterHotels(String dest, List<String> star, List<String> types, List<String> rating,
                                    List<String> facilities, List<String> amenities,
                                    Integer pageNumber, Integer pageSize) {
        return null;
    }
}
package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.dtos.request.HotelDto;
import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.repository.FacilityRepository;
import com.cnweb.bookingapi.repository.HotelRepository;
import com.cnweb.bookingapi.repository.PropertyTypeRepository;
import com.cnweb.bookingapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final PropertyTypeRepository typeRepository;
    private final FacilityRepository facilityRepository;
    public List<Hotel> allHotels(String businessId) {
        String email = userRepository.findById(businessId).orElseThrow().getEmail();
        return hotelRepository.findByEmail(email);
    }
    public Hotel singleHotel(String id) {
        return hotelRepository.findById(id).orElse(null);
    }
    public Hotel newHotel(HotelDto hotelDto) {
        Hotel hotel = hotelDto.toHotel();
        hotel.setType(typeRepository.findByName(hotelDto.getType()).orElse(null));
        hotel.setFacilities(hotelDto.getFacilities().stream().map(facility ->
                facilityRepository.findByName(facility).orElse(null)).toList());
        hotel.setRating(0F);
        return hotelRepository.save(hotel);
    }
    public Hotel updatedHotel(String id, Hotel hotel) {
        hotel.setId(id);
        return hotelRepository.save(hotel);
    }
}

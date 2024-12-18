package com.cnweb.bookingapi.repository;

import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.model.hotelattributemodels.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends MongoRepository<Hotel, String> {
    Optional<Hotel> findByName(String name);

    List<Hotel> findByDest(String dest);

    Page<Hotel> findByDest(String dest, Pageable pageable);

    List<Hotel> findByType(PropertyType type);

    List<Hotel> findByEmail(String email);

    Page<Hotel> findByStar(Integer star, Pageable pageable);

    Page<Hotel> findByNameRegex(String regex, Pageable pageable);

    List<Hotel> findByNameRegex(String regex);
}

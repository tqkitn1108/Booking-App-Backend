package com.cnweb.bookingapi.repository;

import com.cnweb.bookingapi.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends MongoRepository<Hotel, String> {
    Optional<Hotel> findByName(String name);
    Page<Hotel> findByLocation(String location, Pageable pageable);
    Page<Hotel> findByStar(Integer star, Pageable pageable);
    Page<Hotel> findByCheapestPriceBetween(int from, int to, Pageable pageable);
    Page<Hotel> findByNameContaining(String name, Pageable pageable);
}

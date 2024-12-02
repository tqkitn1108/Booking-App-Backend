package com.cnweb.bookingapi.controller;

import com.cnweb.bookingapi.dtos.request.HotelDto;
import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.service.BusinessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business")
public class BusinessController {
    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping("/{businessId}/hotels")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<List<Hotel>> getAllHotels(@PathVariable String businessId) {
        return ResponseEntity.ok(businessService.allHotels(businessId));
    }

    @GetMapping("/hotels/{id}")
    public ResponseEntity<Hotel> getSingleHotel(@PathVariable String id) {
        return new ResponseEntity<>(businessService.singleHotel(id), HttpStatus.OK);
    }

    @PostMapping("/hotels")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<Hotel> createHotel(@Valid @RequestBody HotelDto hotelDto) {
        Hotel createdHotel = businessService.newHotel(hotelDto);
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(hotel.getId())
//                .toUri();
//        return ResponseEntity.created(location).build();
        return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    }

    @PutMapping("/hotels/{id}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<Hotel> updateHotel(@RequestBody Hotel hotel, @PathVariable String id) {
        return new ResponseEntity<>(businessService.updatedHotel(id, hotel), HttpStatus.OK);
    }

    @DeleteMapping("/hotels/{id}")
    @PreAuthorize("hasRole('HOTEL')")
    public ResponseEntity<String> deleteHotel(@PathVariable String id) {
        businessService.deletedHotel(id);
        return ResponseEntity.ok("Hotel has been deleted");
    }
}

package com.cnweb.bookingapi.controller;

import com.cnweb.bookingapi.model.Room;
import com.cnweb.bookingapi.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<Room>> getAllRooms(@PathVariable String hotelId) {
        return new ResponseEntity<>(roomService.allRooms(hotelId), HttpStatus.OK);
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<Room> getSingleRoom(@PathVariable String id) {
        return new ResponseEntity<>(roomService.singleRoom(id), HttpStatus.OK);
    }

    @PostMapping("/rooms")
    public ResponseEntity<Room> addNewRoom(@RequestBody Room room) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(room.getId())
                .toUri();
        return ResponseEntity.created(location).body(roomService.newRoom(room));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable String id) {
        return ResponseEntity.ok(roomService.deleteRoom(id));
    }
}
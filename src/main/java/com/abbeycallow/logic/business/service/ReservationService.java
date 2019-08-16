package com.abbeycallow.logic.business.service;

import com.abbeycallow.logic.business.domain.RoomReservation;
import com.abbeycallow.logic.data.entity.Guest;
import com.abbeycallow.logic.data.entity.Reservation;
import com.abbeycallow.logic.data.entity.Room;
import com.abbeycallow.logic.data.repository.GuestRepository;
import com.abbeycallow.logic.data.repository.ReservationRepository;
import com.abbeycallow.logic.data.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReservationService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;

    @Autowired
    public ReservationService(RoomRepository roomRepository, ReservationRepository reservationRepository, GuestRepository guestRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
    }

    public List<RoomReservation> getRoomReservationForDate(Date date) {
        var rooms = this.roomRepository.findAll();
        Map<Long, RoomReservation> roomReservationMap = new HashMap<>();
        rooms.forEach(room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomId(room.getId());
            roomReservation.setLastName(room.getName());
            roomReservation.setRoomNumber(room.getNumber());
            roomReservationMap.put(room.getId(), roomReservation);
        });
        Iterable <Reservation> reservations = this.reservationRepository.findByDate(new java.sql.Date(date.getTime()));
        if (null != reservations) {
            reservations.forEach(reservation -> {
                Optional<Guest> guestResp = this.guestRepository.findById(reservation.getGuestId());
                if (guestResp.isPresent()) {
                    Guest guest = guestResp.get();
                    RoomReservation roomReservations = roomReservationMap.get(reservation.getId());
                    roomReservations.setDate(date);
                    roomReservations.setFirstNAme(guest.getFirstName());
                    roomReservations.setLastName(guest.getLastName());
                    roomReservations.setGuestId(guest.getId());
                }
            });
        }
        List<RoomReservation> roomReservations = new ArrayList<>();
        for(Long roomId:roomReservationMap.keySet()){
            roomReservations.add(roomReservationMap.get(roomId));
        }
        return roomReservations;
    }

}



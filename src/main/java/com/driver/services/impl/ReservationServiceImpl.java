package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        try
        {
            Optional<ParkingLot> optionalParkingLot = parkingLotRepository3.findById(parkingLotId);
            Optional<User> optionalUser = userRepository3.findById(userId);

            if(!optionalParkingLot.isPresent() || !optionalUser.isPresent())
            {
                throw new Exception("Cannot make reservation");
            }

            ParkingLot parkingLot = optionalParkingLot.get();
            User user = optionalUser.get();

            int min_price = Integer.MAX_VALUE;
            Spot spot = null;

            for(Spot s : parkingLot.getSpotList())
            {
                int wheels = 0;
                if(s.getSpotType() == SpotType.TWO_WHEELER) wheels = 2;
                else if (s.getSpotType() == SpotType.FOUR_WHEELER) {
                    wheels = 4;
                }
                else
                {
                    wheels = Integer.MAX_VALUE;
                }

                if(wheels >= numberOfWheels && !s.getOccupied())
                {
                    int total_Price = s.getPricePerHour()*timeInHours;
                    min_price = Math.min(total_Price,min_price);
                    spot = s;
                }
            }

            if(spot == null)throw  new Exception("Cannot make reservation");

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setSpot(spot);
            reservation.setNumberOfHours(numberOfWheels);

            spot.setOccupied(true);
            spot.getReservationList().add(reservation);

            user.getReservationList().add(reservation);

            spotRepository3.save(spot);
            userRepository3.save(user);

            return reservation;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}

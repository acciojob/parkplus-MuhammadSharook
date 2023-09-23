package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {

        Optional<Reservation> optionalReservation = reservationRepository2.findById(reservationId);

        if(!optionalReservation.isPresent())
        {
            throw new Exception("reservation doesn't exist");
        }

        Reservation reservation = optionalReservation.get();

        int total_bill = reservation.getNumberOfHours()*reservation.getSpot().getPricePerHour();

        if(mode.equalsIgnoreCase("cash") || mode.equalsIgnoreCase("card") || mode.equalsIgnoreCase("upi"))
        {
            if(total_bill != amountSent)
            {
                throw new Exception("Insufficient Amount");
            }

            Payment payment = new Payment();
            PaymentMode paymentMode;

            if(mode.equalsIgnoreCase("cash")) paymentMode = PaymentMode.CASH;
            else if (mode.equalsIgnoreCase("card")) {
                paymentMode = PaymentMode.CARD;
            }
            else {
                paymentMode = PaymentMode.UPI;
            }

            payment.setPaymentMode(paymentMode);
            payment.setPaymentCompleted(true);

            reservation.getSpot().setOccupied(false);
            payment.setReservation(reservation);

            reservation.setPayment(payment);


            reservationRepository2.save(reservation);
            return payment;
        }
        else {
            throw new Exception("Payment mode not detected");
        }
    }
}

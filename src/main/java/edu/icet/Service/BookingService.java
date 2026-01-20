package edu.icet.Service;

import edu.icet.Annotation.AuditFailure;
import edu.icet.Enum.BookingStatus;
import edu.icet.Enum.SeatStatus;
import edu.icet.Exceptions.InvalidBookingException;
import edu.icet.Exceptions.ResourceNotFoundException;
import edu.icet.Model.Dto.BookingResponse;
import edu.icet.Model.Dto.PriceCalculationResponse;
import edu.icet.Model.Entity.Booking;
import edu.icet.Model.Entity.Event;
import edu.icet.Model.Entity.Seat;
import edu.icet.Model.Entity.User;
import edu.icet.Repository.BookingRepository;
import edu.icet.Repository.EventRepository;
import edu.icet.Repository.SeatRepository;
import edu.icet.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PriceCalculatorService priceCalculatorService;

    @AuditFailure
    @Transactional
    public BookingResponse confirmBooking(Long userId, Long seatId) {
        log.info("Confirming booking for user {} and seat {}", userId, seatId);

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Fetch seat with lock
        Seat seat = seatRepository.findByIdWithLock(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID: " + seatId));

        // Validation checks
        if (seat.getStatus() == SeatStatus.SOLD) {
            throw new InvalidBookingException("Seat is already sold");
        }

        if (seat.getStatus() == SeatStatus.AVAILABLE) {
            throw new InvalidBookingException("Seat must be held before booking. Please hold the seat first.");
        }

        if (seat.getStatus() == SeatStatus.HELD) {
            // Check if hold belongs to this user
            if (!seat.getHeldByUserId().equals(userId)) {
                throw new InvalidBookingException("Seat is held by another user");
            }

            // Check if hold has expired
            if (seat.isHoldExpired()) {
                throw new InvalidBookingException("Seat hold has expired. Please hold the seat again.");
            }
        }

        // Get event for price calculation
        Event event = seat.getEvent();

        // Calculate price
        PriceCalculationResponse priceResponse = priceCalculatorService.calculatePrice(user, event);
        BigDecimal finalPrice = priceResponse.getFinalPrice();

        // Update seat status to SOLD
        seat.setStatus(SeatStatus.SOLD);
        seat.setHoldExpiry(null);
        seatRepository.save(seat);

        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .seat(seat)
                .amountPaid(finalPrice)
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        log.info("Booking confirmed - ID: {}, User: {}, Seat: {}, Amount: {}",
                savedBooking.getId(), userId, seatId, finalPrice);

        return BookingResponse.builder()
                .bookingId(savedBooking.getId())
                .userId(user.getId())
                .userName(user.getName())
                .seatId(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .eventName(event.getName())
                .amountPaid(finalPrice)
                .status(savedBooking.getStatus().name())
                .bookedAt(savedBooking.getCreatedAt())
                .build();
    }

    @AuditFailure
    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new InvalidBookingException("You can only cancel your own bookings");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("Booking is already cancelled");
        }

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Make seat available again
        Seat seat = booking.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setHeldByUserId(null);
        seat.setHoldExpiry(null);
        seatRepository.save(seat);

        log.info("Booking {} cancelled by user {}", bookingId, userId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @AuditFailure
    @Transactional(readOnly = true)
    public PriceCalculationResponse calculateBookingPrice(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        return priceCalculatorService.calculatePrice(user, event);
    }
}
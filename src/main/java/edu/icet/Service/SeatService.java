package edu.icet.Service;


import edu.icet.Enum.SeatStatus;
import edu.icet.Exceptions.ResourceNotFoundException;
import edu.icet.Exceptions.SeatLockedException;
import edu.icet.Exceptions.SeatNotAvailableException;
import edu.icet.Model.Dto.HoldSeatResponse;
import edu.icet.Model.Entity.Seat;
import edu.icet.Model.Entity.User;
import edu.icet.Repository.SeatRepository;
import edu.icet.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {

    private static final int HOLD_DURATION_MINUTES = 10;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Transactional
    public HoldSeatResponse holdSeat(Long seatId, Long userId) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Use pessimistic locking to prevent concurrent modifications
        Seat seat = seatRepository.findByIdWithLock(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID: " + seatId));

        log.info("Attempting to hold seat {} for user {}", seatId, userId);

        // Check current seat status
        if (seat.getStatus() == SeatStatus.SOLD) {
            throw new SeatNotAvailableException("Seat is already sold");
        }

        if (seat.getStatus() == SeatStatus.HELD) {
            // Check if hold has expired
            if (seat.isHoldExpired()) {
                log.info("Previous hold on seat {} has expired. Overwriting with new hold.", seatId);
                // Overwrite with new hold
                updateSeatHold(seat, userId);
            } else {
                // Hold is still active - calculate remaining time
                long remainingSeconds = Duration.between(
                        LocalDateTime.now(),
                        seat.getHoldExpiry()
                ).getSeconds();

                log.warn("Seat {} is currently held. {} seconds remaining.", seatId, remainingSeconds);
                throw new SeatLockedException(remainingSeconds);
            }
        } else {
            // Seat is available - hold it
            updateSeatHold(seat, userId);
        }

        Seat savedSeat = seatRepository.save(seat);

        log.info("Successfully held seat {} for user {} until {}",
                seatId, userId, savedSeat.getHoldExpiry());

        return HoldSeatResponse.builder()
                .seatId(savedSeat.getId())
                .seatNumber(savedSeat.getSeatNumber())
                .status(savedSeat.getStatus().name())
                .heldByUserId(savedSeat.getHeldByUserId())
                .holdExpiry(savedSeat.getHoldExpiry())
                .message("Seat successfully held for 10 minutes")
                .build();
    }

    private void updateSeatHold(Seat seat, Long userId) {
        seat.setStatus(SeatStatus.HELD);
        seat.setHeldByUserId(userId);
        seat.setHoldExpiry(LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES));
    }

    @Transactional(readOnly = true)
    public List<Seat> getAvailableSeats(Long eventId) {
        return seatRepository.findByEventIdAndStatus(eventId, SeatStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public List<Seat> getAllSeatsForEvent(Long eventId) {
        return seatRepository.findByEventId(eventId);
    }
}

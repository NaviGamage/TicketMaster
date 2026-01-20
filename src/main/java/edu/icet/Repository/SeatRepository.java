package edu.icet.Repository;

import edu.icet.Enum.SeatStatus;
import edu.icet.Model.Entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :seatId")
    Optional<Seat> findByIdWithLock(@Param("seatId") Long seatId);

    List<Seat> findByEventIdAndStatus(Long eventId, SeatStatus status);

    @Query("SELECT s FROM Seat s WHERE s.event.id = :eventId")
    List<Seat> findByEventId(@Param("eventId") Long eventId);
}

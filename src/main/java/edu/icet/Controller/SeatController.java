package edu.icet.Controller;


import edu.icet.Model.Dto.HoldSeatRequest;
import edu.icet.Model.Dto.HoldSeatResponse;
import edu.icet.Model.Entity.Seat;
import edu.icet.Service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping("/{id}/hold")
    public ResponseEntity<HoldSeatResponse> holdSeat(
            @PathVariable Long id,
            @RequestBody HoldSeatRequest request) {

        HoldSeatResponse response = seatService.holdSeat(id, request.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event/{eventId}/available")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Long eventId) {
        List<Seat> seats = seatService.getAvailableSeats(eventId);
        return ResponseEntity.ok(seats);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Seat>> getAllSeatsForEvent(@PathVariable Long eventId) {
        List<Seat> seats = seatService.getAllSeatsForEvent(eventId);
        return ResponseEntity.ok(seats);
    }
}

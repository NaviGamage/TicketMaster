package edu.icet.Controller;

import edu.icet.Model.Dto.BookingResponse;
import edu.icet.Model.Dto.ConfirmBookingRequest;
import edu.icet.Model.Dto.PriceCalculationRequest;
import edu.icet.Model.Dto.PriceCalculationResponse;
import edu.icet.Model.Entity.Booking;
import edu.icet.Service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @RequestBody ConfirmBookingRequest request) {

        BookingResponse response = bookingService.confirmBooking(
                request.getUserId(),
                request.getSeatId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/calculate-price")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(
            @RequestBody PriceCalculationRequest request) {

        PriceCalculationResponse response = bookingService.calculateBookingPrice(
                request.getUserId(),
                request.getEventId()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam Long userId) {

        bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }
}
package edu.icet.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private Long userId;
    private String userName;
    private Long seatId;
    private String seatNumber;
    private String eventName;
    private BigDecimal amountPaid;
    private String status;
    private LocalDateTime bookedAt;
}
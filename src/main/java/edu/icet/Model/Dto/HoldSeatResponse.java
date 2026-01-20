package edu.icet.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldSeatResponse {
    private Long seatId;
    private String seatNumber;
    private String status;
    private Long heldByUserId;
    private LocalDateTime holdExpiry;
    private String message;
}

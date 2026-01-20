package edu.icet.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmBookingRequest {
    private Long userId;
    private Long seatId;
}

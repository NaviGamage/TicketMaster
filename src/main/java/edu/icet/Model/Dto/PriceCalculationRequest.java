package edu.icet.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationRequest {
    private Long userId;
    private Long eventId;
}

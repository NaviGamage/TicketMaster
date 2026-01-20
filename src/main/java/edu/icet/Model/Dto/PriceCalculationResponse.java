package edu.icet.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationResponse {
    private BigDecimal basePrice;
    private BigDecimal finalPrice;
    private String userTier;
    private boolean priorityAccess;
    private String priceBreakdown;
}

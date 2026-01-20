package edu.icet.Service;


import edu.icet.Enum.UserTier;
import edu.icet.Model.Dto.PriceCalculationResponse;
import edu.icet.Model.Entity.Event;
import edu.icet.Model.Entity.User;
import edu.icet.Strategy.PlatinumPricingStrategy;
import edu.icet.Strategy.PricingStrategy;
import edu.icet.Strategy.RegularPricingStrategy;
import edu.icet.Strategy.VIPPricingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PriceCalculatorService {

    private final RegularPricingStrategy regularPricingStrategy;
    private final VIPPricingStrategy vipPricingStrategy;
    private final PlatinumPricingStrategy platinumPricingStrategy;

    public PriceCalculationResponse calculatePrice(User user, Event event) {
        PricingStrategy strategy = selectStrategy(user.getTier());

        BigDecimal finalPrice = strategy.calculatePrice(user, event);
        boolean priorityAccess = strategy.hasPriorityAccess();

        String breakdown = generatePriceBreakdown(user, event, finalPrice);

        return PriceCalculationResponse.builder()
                .basePrice(event.getBasePrice())
                .finalPrice(finalPrice)
                .userTier(user.getTier().name())
                .priorityAccess(priorityAccess)
                .priceBreakdown(breakdown)
                .build();
    }

    private PricingStrategy selectStrategy(UserTier tier) {
        Map<UserTier, PricingStrategy> strategyMap = new HashMap<>();
        strategyMap.put(UserTier.REGULAR, regularPricingStrategy);
        strategyMap.put(UserTier.VIP, vipPricingStrategy);
        strategyMap.put(UserTier.PLATINUM, platinumPricingStrategy);

        return strategyMap.getOrDefault(tier, regularPricingStrategy);
    }

    private String generatePriceBreakdown(User user, Event event, BigDecimal finalPrice) {
        StringBuilder breakdown = new StringBuilder();
        breakdown.append("Base Price: $").append(event.getBasePrice());

        if (user.getTier() == UserTier.VIP && !event.getIsHighDemand()) {
            BigDecimal discount = event.getBasePrice().subtract(finalPrice);
            breakdown.append(", VIP Discount (10%): -$").append(discount);
        } else if (user.getTier() == UserTier.VIP && event.getIsHighDemand()) {
            breakdown.append(", High Demand Event: No Discount");
        } else if (user.getTier() == UserTier.PLATINUM) {
            breakdown.append(", Platinum Member: Priority Access Granted");
        }

        breakdown.append(", Final Price: $").append(finalPrice);
        return breakdown.toString();
    }
}

package edu.icet.Strategy;


import edu.icet.Model.Entity.Event;
import edu.icet.Model.Entity.User;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class VIPPricingStrategy implements PricingStrategy {

    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("0.10");

    @Override
    public BigDecimal calculatePrice(User user, Event event) {
        // VIP gets 10% off, UNLESS the event is HIGH_DEMAND
        if (event.getIsHighDemand()) {
            return event.getBasePrice(); // Full price for high demand events
        }

        // Apply 10% discount
        BigDecimal discount = event.getBasePrice().multiply(DISCOUNT_PERCENTAGE);
        return event.getBasePrice().subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean hasPriorityAccess() {
        return false;
    }
}
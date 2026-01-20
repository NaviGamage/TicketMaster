package edu.icet.Strategy;

import edu.icet.Model.Entity.Event;
import edu.icet.Model.Entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RegularPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(User user, Event event) {
        return event.getBasePrice();
    }

    @Override
    public boolean hasPriorityAccess() {
        return false;
    }
}

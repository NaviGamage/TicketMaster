package edu.icet.Strategy;


import edu.icet.Model.Entity.Event;
import edu.icet.Model.Entity.User;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class PlatinumPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(User user, Event event) {
        // Platinum always pays base price
        return event.getBasePrice();
    }

    @Override
    public boolean hasPriorityAccess() {
        // Platinum users get priority access
        return true;
    }
}

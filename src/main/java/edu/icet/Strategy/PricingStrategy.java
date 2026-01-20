package edu.icet.Strategy;


import edu.icet.Model.Entity.Event;
import edu.icet.Model.Entity.User;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(User user, Event event);
    boolean hasPriorityAccess();
}
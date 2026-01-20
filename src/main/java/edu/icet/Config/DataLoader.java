package edu.icet.Config;


import edu.icet.Enum.SeatStatus;
import edu.icet.Enum.UserTier;
import edu.icet.Model.Entity.Event;
import edu.icet.Model.Entity.Seat;
import edu.icet.Model.Entity.User;
import edu.icet.Repository.EventRepository;
import edu.icet.Repository.SeatRepository;
import edu.icet.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Loading sample data...");
            loadSampleData();
            log.info("Sample data loaded successfully!");
        } else {
            log.info("Data already exists. Skipping initialization.");
        }
    }

    private void loadSampleData() {
        // Create Users
        User regularUser = User.builder()
                .name("Navindu Gamage ")
                .tier(UserTier.REGULAR)
                .email("navindu@example.com")
                .build();

        User vipUser = User.builder()
                .name("Sahani Sankalpani")
                .tier(UserTier.VIP)
                .email("sahani@example.com")
                .build();

        User platinumUser = User.builder()
                .name("Kamal Amaradewa")
                .tier(UserTier.PLATINUM)
                .email("kamal@example.com")
                .build();

        userRepository.save(regularUser);
        userRepository.save(vipUser);
        userRepository.save(platinumUser);

        log.info("Created 3 users: Regular, VIP, Platinum");

        // Create Events
        Event concert1 = Event.builder()
                .name("Rock Concert - The Beatles Tribute")
                .basePrice(new BigDecimal("100.00"))
                .isHighDemand(false)
                .eventDate(LocalDateTime.now().plusDays(30))
                .build();

        Event concert2 = Event.builder()
                .name("Pop Concert - Taylor Swift")
                .basePrice(new BigDecimal("150.00"))
                .isHighDemand(true) // High demand event
                .eventDate(LocalDateTime.now().plusDays(45))
                .build();

        eventRepository.save(concert1);
        eventRepository.save(concert2);

        log.info("Created 2 events");

        // Create Seats for Concert 1
        for (int i = 1; i <= 20; i++) {
            Seat seat = Seat.builder()
                    .event(concert1)
                    .seatNumber("A" + i)
                    .status(SeatStatus.AVAILABLE)
                    .build();
            seatRepository.save(seat);
        }

        // Create Seats for Concert 2
        for (int i = 1; i <= 20; i++) {
            Seat seat = Seat.builder()
                    .event(concert2)
                    .seatNumber("B" + i)
                    .status(SeatStatus.AVAILABLE)
                    .build();
            seatRepository.save(seat);
        }

        log.info("Created 40 seats (20 per event)");
    }
}

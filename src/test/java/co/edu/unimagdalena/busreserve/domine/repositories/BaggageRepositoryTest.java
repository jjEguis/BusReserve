package co.edu.unimagdalena.busreserve.domine.repositories;

import co.edu.unimagdalena.busreserve.domine.entities.*;
import co.edu.unimagdalena.busreserve.domine.entities.Role;
import co.edu.unimagdalena.busreserve.domine.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaggageRepositoryTest extends AbstractRepositoryIT{
    @Autowired
    private BaggageRepository baggageRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private BusRepository busRepository;

    private Ticket ticket;
    private Baggage baggage1;
    private Baggage baggage2;

    @BeforeEach
    void setUp() {

        // ===============================
        // GIVEN: Route + Stops
        // ===============================
        Route route = routeRepository.save(
                Route.builder()
                        .code("R20")
                        .name("Ruta Norte")
                        .origin("A")
                        .destination("B")
                        .distanceKm(80.0)
                        .durationMin(90)
                        .build()
        );

        Stop stopA = stopRepository.save(
                Stop.builder().name("Stop A").route(route).stopOrder(1).lat(1.).lng(1.).build()
        );

        Stop stopB = stopRepository.save(
                Stop.builder().name("Stop B").route(route).stopOrder(2).lat(2.).lng(2.).build()
        );

        // ===============================
        // GIVEN: Bus
        // ===============================
        Bus bus = busRepository.save(
                Bus.builder()
                        .plate("BUS100")
                        .capacity(40)
                        .available(true)
                        .amenities(List.of())
                        .build()
        );

        // ===============================
        // GIVEN: Trip
        // ===============================
        Trip trip = tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(bus)
                        .date(LocalDate.now())
                        .departureAt(LocalDateTime.now().plusHours(1))
                        .arrivalEta(LocalDateTime.now().plusHours(3))
                        .status(TripStatus.SCHEDULED)
                        .build()
        );

        // ===============================
        // GIVEN: User (Passenger)
        // ===============================
        User passenger = userRepository.save(
                User.builder()
                        .name("Carlos")
                        .email("c@mail.com")
                        .phone("3001112222")
                        .passwordHash("1234")
                        .role(Role.PASSENGER)
                        .build()
        );

        // ===============================
        // GIVEN: Ticket
        // ===============================
        ticket = ticketRepository.save(
                Ticket.builder()
                        .trip(trip)
                        .passenger(passenger)
                        .fromStop(stopA)
                        .toStop(stopB)
                        .seatNumber(5)
                        .price(java.math.BigDecimal.valueOf(20000))
                        .status(TicketStatus.SOLD)
                        .build()
        );

        // ===============================
        // GIVEN: Baggage items
        // ===============================
        baggage1 = baggageRepository.save(
                Baggage.builder()
                        .ticket(ticket)
                        .weightKg(12.5)
                        .fee(15000.0)
                        .tagCode("BGG-001")
                        .build()
        );

        baggage2 = baggageRepository.save(
                Baggage.builder()
                        .ticket(ticket)
                        .weightKg(5.0)
                        .fee(8000.0)
                        .tagCode("BGG-002")
                        .build()
        );
    }


    // ===============================
    // TEST 1 — findByTicketId
    // ===============================
    @Test
    void shouldFindBaggageByTicketId() {

        List<Baggage> result = baggageRepository.findByTicketId(ticket.getId());

        assertEquals(2, result.size());
        assertTrue(result.contains(baggage1));
        assertTrue(result.contains(baggage2));
    }


    // ===============================
    // TEST 2 — findByTagCode
    // ===============================
    @Test
    void shouldFindByTagCode() {

        Optional<Baggage> result = baggageRepository.findByTagCode("BGG-001");

        assertTrue(result.isPresent());
        assertEquals(baggage1.getId(), result.get().getId());
    }


    // ===============================
    // TEST 3 — findByTagCode returns empty
    // ===============================
    @Test
    void shouldReturnEmptyWhenTagNotFound() {

        Optional<Baggage> result = baggageRepository.findByTagCode("INVALID");

        assertTrue(result.isEmpty());
    }
}

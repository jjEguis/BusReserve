package co.edu.unimagdalena.busreserve.domine.repositories;

import co.edu.unimagdalena.busreserve.domine.entities.*;
import co.edu.unimagdalena.busreserve.domine.entities.Role;
import co.edu.unimagdalena.busreserve.domine.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TicketRepositoryTest extends AbstractRepositoryIT{
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusRepository busRepository;

    private Route route;
    private Stop s1, s2, s3, s4;
    private Bus bus;
    private Trip trip;

    private User passenger1;
    private User passenger2;
    private User passengerX;

    private Ticket t1, t2, t3, tX;

    @BeforeEach
    void setUp() {

        // ===========================================
        // GIVEN: Usuarios reales
        // ===========================================
        passenger1 = userRepository.save(
                User.builder()
                        .name("Juan")
                        .email("juan@mail.com")
                        .phone("3011112222")
                        .role(Role.PASSENGER)
                        .passwordHash("test")
                        .build()
        );

        passenger2 = userRepository.save(
                User.builder()
                        .name("Maria")
                        .email("maria@mail.com")
                        .phone("3023334444")
                        .role(Role.PASSENGER)
                        .passwordHash("test")
                        .build()
        );

        passengerX = userRepository.save(
                User.builder()
                        .name("Extra")
                        .email("extra@mail.com")
                        .phone("3035556666")
                        .role(Role.PASSENGER)
                        .passwordHash("test")
                        .build()
        );

        // ===========================================
        // GIVEN: Crear Ruta
        // ===========================================
        route = routeRepository.save(
                Route.builder()
                        .code("R100")
                        .name("Bogotá - Tunja")
                        .origin("Bogotá")
                        .destination("Tunja")
                        .distanceKm(150.0)
                        .durationMin(180)
                        .build()
        );

        // Paradas ordenadas
        s1 = stopRepository.save(Stop.builder().name("Terminal Salitre").stopOrder(1).lat(4.65).lng(-74.10).route(route).build());
        s2 = stopRepository.save(Stop.builder().name("Briceño").stopOrder(2).lat(4.90).lng(-73.95).route(route).build());
        s3 = stopRepository.save(Stop.builder().name("Puente Boyacá").stopOrder(3).lat(5.52).lng(-73.37).route(route).build());
        s4 = stopRepository.save(Stop.builder().name("Tunja Terminal").stopOrder(4).lat(5.53).lng(-73.36).route(route).build());

        // ===========================================
        // GIVEN: Crear Bus
        // ===========================================
        bus = busRepository.save(
                Bus.builder()
                        .plate("ABC123")
                        .capacity(40)
                        .available(true)
                        .amenities(List.of("WiFi", "USB"))
                        .build()
        );

        // ===========================================
        // GIVEN: Crear Trip
        // ===========================================
        trip = tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(bus)
                        .date(LocalDate.now())
                        .departureAt(LocalDateTime.now().plusHours(1))
                        .arrivalEta(LocalDateTime.now().plusHours(3))
                        .status(TripStatus.SCHEDULED)
                        .build()
        );

        // ===========================================
        // GIVEN: Crear Tickets para varios tramos
        // ===========================================

        // Ticket 1: passenger1 - tramo 1 → 3
        t1 = ticketRepository.save(
                Ticket.builder()
                        .trip(trip)
                        .seatNumber(10)
                        .fromStop(s1)
                        .toStop(s3)
                        .passenger(passenger1)
                        .price(BigDecimal.valueOf(20000))
                        .status(TicketStatus.SOLD)
                        .paymentMethod(PaymentMethod.CARD)
                        .build()
        );

        // Ticket 2: passenger2 - tramo 2 → 4
        t2 = ticketRepository.save(
                Ticket.builder()
                        .trip(trip)
                        .seatNumber(11)
                        .fromStop(s2)
                        .toStop(s4)
                        .passenger(passenger2)
                        .price(BigDecimal.valueOf(25000))
                        .status(TicketStatus.SOLD)
                        .paymentMethod(PaymentMethod.CARD)
                        .build()
        );

        // Ticket 3: passenger1 - tramo 3 → 4
        t3 = ticketRepository.save(
                Ticket.builder()
                        .trip(trip)
                        .seatNumber(12)
                        .fromStop(s3)
                        .toStop(s4)
                        .passenger(passenger1)
                        .price(BigDecimal.valueOf(15000))
                        .status(TicketStatus.SOLD)
                        .paymentMethod(PaymentMethod.CARD)
                        .build()
        );

        // Ticket X: passengerX - tramo 2 → 3 (cancelado)
        tX = ticketRepository.save(
                Ticket.builder()
                        .trip(trip)
                        .seatNumber(13)
                        .fromStop(s2)
                        .toStop(s3)
                        .passenger(passengerX)
                        .price(BigDecimal.valueOf(18000))
                        .status(TicketStatus.CANCELLED)
                        .paymentMethod(PaymentMethod.CASH)
                        .build()
        );
    }


    // =======================================================
    // TEST 1: findByPassenger(User)
    // =======================================================
    @Test
    void shouldFindTicketsByPassengerObject() {
        List<Ticket> result = ticketRepository.findByPassenger(passenger1);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getPassenger().getId().equals(passenger1.getId())));
    }


    // =======================================================
    // TEST 2: findByPassengerId(Long)
    // =======================================================
    @Test
    void shouldFindTicketsByPassengerId() {
        List<Ticket> result = ticketRepository.findByPassengerId(passenger2.getId());

        assertEquals(1, result.size());
        assertEquals(t2.getId(), result.get(0).getId());
    }


    // =======================================================
    // TEST 3: findByTripId
    // =======================================================
    @Test
    void shouldFindTicketsByTripId() {
        List<Ticket> result = ticketRepository.findByTripId(trip.getId());

        assertEquals(4, result.size());
    }


    // =======================================================
    // TEST 4: findByStatus
    // =======================================================
    @Test
    void shouldFindByStatusSold() {
        List<Ticket> result = ticketRepository.findByStatus(TicketStatus.SOLD);

        assertEquals(3, result.size());
    }


    // =======================================================
    // TEST 5: findByTripIdAndFromStopIdAndToStopId
    // =======================================================
    @Test
    void shouldFindTicketsByExactSegment() {
        List<Ticket> result = ticketRepository.findByTripIdAndFromStopIdAndToStopId(
                trip.getId(),
                s1.getId(),
                s3.getId()
        );

        assertEquals(1, result.size());
        assertEquals(t1.getId(), result.get(0).getId());
    }


    // =======================================================
    // TEST 6: findOverlappingTickets
    //
    // Tramo PROBADO: 2 → 3
    //
    // Tickets solapados:
    //  - t1: 1 → 3   (solapa)
    //  - t2: 2 → 4   (solapa)
    //  - tX: 2 → 3   (solapa)
    //  - t3: 3 → 4   (NO solapa)
    // =======================================================
    @Test
    void shouldFindOverlappingTicketsForSegment() {

        List<Ticket> result = ticketRepository.findOverlappingTickets(
                trip.getId(),
                2, // fromOrder
                3  // toOrder
        );

        assertEquals(3, result.size());

        assertTrue(result.contains(t1));
        assertTrue(result.contains(t2));
        assertTrue(result.contains(tX));
        assertFalse(result.contains(t3));
    }
}

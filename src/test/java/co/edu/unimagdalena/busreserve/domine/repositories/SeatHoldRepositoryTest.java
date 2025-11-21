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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeatHoldRepositoryTest extends AbstractRepositoryIT{
    @Autowired
    private SeatHoldRepository seatHoldRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    private Route route;
    private Trip trip;
    private Bus bus;

    private SeatHold hold1;     // activo
    private SeatHold hold2;     // activo
    private SeatHold holdExpired; // expirado

    @BeforeEach
    void setUp() {

        // ==================================
        // GIVEN: Usuarios
        // ==================================
        user1 = userRepository.save(
                User.builder()
                        .name("Carlos")
                        .email("carlos@mail.com")
                        .phone("3001112222")
                        .role(Role.PASSENGER)
                        .passwordHash("test")
                        .build()
        );

        user2 = userRepository.save(
                User.builder()
                        .name("Ana")
                        .email("ana@mail.com")
                        .phone("3003334444")
                        .role(Role.PASSENGER)
                        .passwordHash("test")
                        .build()
        );

        // ==================================
        // GIVEN: Ruta y stops
        // ==================================
        route = routeRepository.save(
                Route.builder()
                        .code("R101")
                        .name("Bogotá - Tunja")
                        .origin("Bogotá")
                        .destination("Tunja")
                        .distanceKm(150.0)
                        .durationMin(180)
                        .build()
        );

        stopRepository.save(Stop.builder()
                .name("Salitre")
                .stopOrder(1)
                .lat(4.65)
                .lng(-74.10)
                .route(route)
                .build());

        stopRepository.save(Stop.builder()
                .name("Briceño")
                .stopOrder(2)
                .lat(4.90)
                .lng(-73.95)
                .route(route)
                .build());

        // ==================================
        // GIVEN: Bus con boolean available
        // ==================================
        bus = busRepository.save(
                Bus.builder()
                        .plate("AAA111")
                        .capacity(40)
                        .available(true)          // <── Correcto
                        .amenities(List.of("WiFi"))
                        .build()
        );

        // ==================================
        // GIVEN: Trip
        // ==================================
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

        // ==================================
        // GIVEN: SeatHold activos
        // ==================================
        hold1 = seatHoldRepository.save(
                SeatHold.builder()
                        .trip(trip)
                        .seatNumber(5)
                        .user(user1)
                        .expiresAt(LocalDateTime.now().plusMinutes(10))
                        .status(HoldStatus.HOLD)
                        .build()
        );

        hold2 = seatHoldRepository.save(
                SeatHold.builder()
                        .trip(trip)
                        .seatNumber(8)
                        .user(user2)
                        .expiresAt(LocalDateTime.now().plusMinutes(5))
                        .status(HoldStatus.HOLD)
                        .build()
        );

        // ==================================
        // GIVEN: SeatHold expirado
        // ==================================
        holdExpired = seatHoldRepository.save(
                SeatHold.builder()
                        .trip(trip)
                        .seatNumber(12)
                        .user(user1)
                        .expiresAt(LocalDateTime.now().minusMinutes(2)) // expirado
                        .status(HoldStatus.HOLD)
                        .build()
        );
    }

    // ======================================================
    // TEST 1: findByTripIdAndSeatNumber
    // ======================================================
    @Test
    void shouldFindHoldByTripAndSeatNumber() {

        var result = seatHoldRepository.findByTripIdAndSeatNumber(trip.getId(), 5);

        assertTrue(result.isPresent());
        assertEquals(hold1.getId(), result.get().getId());
    }


    // ======================================================
    // TEST 2: findByUserIdAndStatus
    // ======================================================
    @Test
    void shouldFindActiveHoldsByUser() {
        var result = seatHoldRepository.findByUserIdAndStatus(user1.getId(), HoldStatus.HOLD);

        assertEquals(2, result.size()); // hold1 + holdExpired (status is HOLD even if expired time)
        assertTrue(result.contains(hold1));
        assertTrue(result.contains(holdExpired));
    }


    // ======================================================
    // TEST 3: findByTripIdAndStatus
    // ======================================================
    @Test
    void shouldFindActiveHoldsForTrip() {
        var result = seatHoldRepository.findByTripIdAndStatus(trip.getId(), HoldStatus.HOLD);

        assertEquals(3, result.size());
    }


    // ======================================================
    // TEST 4: findByStatusAndExpiresAtBefore
    // ======================================================
    @Test
    void shouldFindExpiredHolds() {
        var result = seatHoldRepository.findByStatusAndExpiresAtBefore(
                HoldStatus.HOLD,
                LocalDateTime.now()
        );

        assertEquals(1, result.size());
        assertEquals(holdExpired.getId(), result.get(0).getId());
    }


    // ======================================================
    // TEST 5: findByUserIdAndTripIdAndStatus
    // ======================================================
    @Test
    void shouldFindUserHoldsForTrip() {
        var result = seatHoldRepository.findByUserIdAndTripIdAndStatus(
                user2.getId(),
                trip.getId(),
                HoldStatus.HOLD
        );

        assertEquals(1, result.size());
        assertEquals(hold2.getId(), result.get(0).getId());
    }
}

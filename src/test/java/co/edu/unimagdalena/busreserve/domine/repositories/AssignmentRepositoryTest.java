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

public class AssignmentRepositoryTest extends AbstractRepositoryIT{

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private UserRepository userRepository;

    private User driver1;
    private User driver2;
    private User dispatcher;

    private Route route;
    private Bus busA;
    private Bus busB;

    private Trip trip1;
    private Trip trip2;
    private Trip trip3;

    private Assignment a1;
    private Assignment a2;
    private Assignment a3;

    @BeforeEach
    void setUp() {

        driver1 = userRepository.save(
                User.builder().name("Pedro").email("p@mail.com").role(Role.DRIVER).passwordHash("x").build()
        );

        driver2 = userRepository.save(
                User.builder().name("Juan").email("j@mail.com").role(Role.DRIVER).passwordHash("x").build()
        );

        dispatcher = userRepository.save(
                User.builder().name("Luis").email("l@mail.com").role(Role.DISPATCHER).passwordHash("x").build()
        );

        route = routeRepository.save(
                Route.builder()
                        .code("R10")
                        .name("Ruta X")
                        .origin("A")
                        .destination("B")
                        .distanceKm(120.0)
                        .durationMin(150)
                        .build()
        );

        stopRepository.save(Stop.builder().name("A1").route(route).stopOrder(1).lat(1.0).lng(1.0).build());
        stopRepository.save(Stop.builder().name("A2").route(route).stopOrder(2).lat(1.1).lng(1.1).build());

        busA = busRepository.save(
                Bus.builder().plate("AAA111").capacity(40).available(true).amenities(List.of()).build()
        );

        busB = busRepository.save(
                Bus.builder().plate("BBB222").capacity(40).available(true).amenities(List.of()).build()
        );

        LocalDate today = LocalDate.now();

        trip1 = tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(busA)
                        .date(today)
                        .departureAt(LocalDateTime.now().plusHours(1))
                        .arrivalEta(LocalDateTime.now().plusHours(3))
                        .status(TripStatus.SCHEDULED)
                        .build()
        );

        trip2 = tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(busA)
                        .date(today)
                        .departureAt(LocalDateTime.now().plusHours(2))
                        .arrivalEta(LocalDateTime.now().plusHours(4))
                        .status(TripStatus.SCHEDULED)
                        .build()
        );

        trip3 = tripRepository.save(
                Trip.builder()
                        .route(route)
                        .bus(busB)
                        .date(today)
                        .departureAt(LocalDateTime.now().plusHours(5))
                        .arrivalEta(LocalDateTime.now().plusHours(7))
                        .status(TripStatus.SCHEDULED)
                        .build()
        );

        a1 = assignmentRepository.save(
                Assignment.builder().trip(trip1).driver(driver1).dispatcher(dispatcher)
                        .assignedAt(LocalDateTime.now()).checklistOk(true).build()
        );

        a2 = assignmentRepository.save(
                Assignment.builder().trip(trip2).driver(driver1).dispatcher(dispatcher)
                        .assignedAt(LocalDateTime.now()).checklistOk(false).build()
        );

        a3 = assignmentRepository.save(
                Assignment.builder().trip(trip3).driver(driver2).dispatcher(dispatcher)
                        .assignedAt(LocalDateTime.now()).checklistOk(true).build()
        );
    }


    @Test
    void shouldFindByTrip() {
        var result = assignmentRepository.findByTripId(trip1.getId());
        assertEquals(1, result.size());
    }

    @Test
    void shouldFindByDriver() {
        var result = assignmentRepository.findByDriverId(driver1.getId());
        assertEquals(2, result.size());
    }

    @Test
    void shouldFindByDispatcher() {
        var result = assignmentRepository.findByDispatcherId(dispatcher.getId());
        assertEquals(3, result.size());
    }

    @Test
    void shouldDetectDriverOverlapping() {
        var result = assignmentRepository.findDriverAssignmentsOverlapping(
                driver1.getId(),
                trip2.getDepartureAt(),
                trip2.getArrivalEta()
        );
        assertEquals(2, result.size()); // overlap trip1-trip2
    }

    @Test
    void shouldReturnEmptyWhenNoOverlapping() {
        var result = assignmentRepository.findDriverAssignmentsOverlapping(
                driver2.getId(),
                trip1.getDepartureAt(),
                trip1.getArrivalEta()
        );
        assertTrue(result.isEmpty());
    }
}

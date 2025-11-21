package co.edu.unimagdalena.busreserve.domine.repositories;

import co.edu.unimagdalena.busreserve.domine.entities.Ticket;
import co.edu.unimagdalena.busreserve.domine.entities.TicketStatus;
import co.edu.unimagdalena.busreserve.domine.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {

    List<Ticket> findByPassenger(User passenger);

    List<Ticket> findByPassengerId(Long passengerId);

    List<Ticket> findByTripId(Long tripId);

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByTripIdAndFromStopIdAndToStopId(
            Long tripId,
            Long fromStopId,
            Long toStopId
    );

    @Query("""
        SELECT t FROM Ticket t
        WHERE t.trip.id = :tripId
          AND t.fromStop.stopOrder < :toOrder
          AND t.toStop.stopOrder   > :fromOrder
        """)
    List<Ticket> findOverlappingTickets(
            @Param("tripId") Long tripId,
            @Param("fromOrder") Integer fromOrder,
            @Param("toOrder") Integer toOrder
    );

    Optional<Ticket> findByTripIdAndSeatNumber(Long tripId, Integer seatNumber);

    Optional<Ticket> findByQrCode(String qrCode);

    Long countByTripIdAndStatus(Long tripId, TicketStatus status);

    Collection<Object> findByStatusAndTrip_DepartureAtBefore(TicketStatus status, LocalDateTime tripDepartureAtBefore);
}

package co.edu.unimagdalena.busreserve.services;

import co.edu.unimagdalena.busreserve.api.dto.TicketDtos.*;
import co.edu.unimagdalena.busreserve.domine.entities.*;
import co.edu.unimagdalena.busreserve.domine.repositories.*;
import co.edu.unimagdalena.busreserve.exception.NotFoundException;
import co.edu.unimagdalena.busreserve.domine.entities.User;
import co.edu.unimagdalena.busreserve.services.interfaces.TicketService;
import co.edu.unimagdalena.busreserve.services.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepo;
    private final TripRepository tripRepo;
    private final UserRepository userRepo;
    private final StopRepository stopRepo;
    private final FareRuleRepository fareRepo;
    private final TicketMapper mapper;

    @Override
    public TicketResponse create(TicketCreateRequest req) {

        Trip trip = tripRepo.findById(req.tripId())
                .orElseThrow(() -> new NotFoundException("Trip %d not found".formatted(req.tripId())));

        User passenger = userRepo.findById(req.passengerId())
                .orElseThrow(() -> new NotFoundException("Passenger %d not found".formatted(req.passengerId())));

        Stop fromStop = stopRepo.findById(req.fromStopId())
                .orElseThrow(() -> new NotFoundException("Stop %d not found".formatted(req.fromStopId())));

        Stop toStop = stopRepo.findById(req.toStopId())
                .orElseThrow(() -> new NotFoundException("Stop %d not found".formatted(req.toStopId())));

        if (!fromStop.getRoute().getId().equals(trip.getRoute().getId()) ||
                !toStop.getRoute().getId().equals(trip.getRoute().getId())) {
            throw new IllegalStateException("Stops do not belong to the trip route");
        }

        if (req.seatNumber() <= 0 || req.seatNumber() > trip.getBus().getCapacity()) {
            throw new IllegalStateException("Invalid seat number");
        }

        if (ticketRepo.findByTripIdAndSeatNumber(req.tripId(), req.seatNumber()).isPresent()) {
            throw new IllegalStateException("Seat %d already sold".formatted(req.seatNumber()));
        }

        FareRule rule = fareRepo.findByRouteAndStops(
                        trip.getRoute().getId(),
                        req.fromStopId(),
                        req.toStopId()
                )
                .orElseThrow(() -> new IllegalStateException("No fare rule for this segment"));

        Ticket ticket = mapper.toEntity(req);
        ticket.setTrip(trip);
        ticket.setPassenger(passenger);
        ticket.setFromStop(fromStop);
        ticket.setToStop(toStop);
        ticket.setPrice(rule.getBasePrice());
        ticket.setQrCode(UUID.randomUUID().toString());

        return mapper.toResponse(ticketRepo.save(ticket));
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse get(Long id) {
        return ticketRepo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Ticket %d not found".formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getByQrCode(String qrCode) {
        return ticketRepo.findByQrCode(qrCode)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Ticket not found for QR code"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> findByTrip(Long tripId) {
        return ticketRepo.findByTripId(tripId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> findByPassenger(Long passengerId) {
        return ticketRepo.findByPassengerId(passengerId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> findByStatus(TicketStatus status) {
        return ticketRepo.findByStatus(status)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public TicketResponse update(Long id, TicketUpdateRequest req) {
        Ticket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket %d not found".formatted(id)));

        mapper.patch(ticket, req);

        return mapper.toResponse(ticketRepo.save(ticket));
    }

    @Override
    public void cancel(Long id) {
        Ticket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket %d not found".formatted(id)));

        if (ticket.getStatus() == TicketStatus.CANCELLED) return;

        if (ticket.getStatus() == TicketStatus.NO_SHOW) {
            throw new IllegalStateException("Cannot cancel a NO_SHOW ticket");
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        ticket.setQrCode(null);

        ticketRepo.save(ticket);
    }

    @Override
    public void markAsNoShow(Long id) {
        Ticket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket %d not found".formatted(id)));

        if (ticket.getStatus() != TicketStatus.SOLD) {
            throw new IllegalStateException("Ticket must be SOLD to mark as NO_SHOW");
        }

        ticket.setStatus(TicketStatus.NO_SHOW);
        ticketRepo.save(ticket);
    }

    @Override
    public void validateTicket(String qrCode) {
        Ticket ticket = ticketRepo.findByQrCode(qrCode)
                .orElseThrow(() -> new NotFoundException("Invalid QR code"));

        if (ticket.getStatus() != TicketStatus.SOLD) {
            throw new IllegalStateException("Ticket is not valid for validation");
        }

        ticket.setStatus(TicketStatus.NO_SHOW); // Si no hay CHECKED_IN, el estado válido más cercano es NO_SHOW o SOLD
        ticketRepo.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countSoldSeatsByTrip(Long tripId) {
        return ticketRepo.countByTripIdAndStatus(tripId, TicketStatus.SOLD);
    }
}
package co.edu.unimagdalena.busreserve.services;

import co.edu.unimagdalena.busreserve.api.dto.SeatDtos.*;
import co.edu.unimagdalena.busreserve.domine.entities.Bus;
import co.edu.unimagdalena.busreserve.domine.entities.Seat;
import co.edu.unimagdalena.busreserve.domine.entities.SeatType;
import co.edu.unimagdalena.busreserve.domine.repositories.BusRepository;
import co.edu.unimagdalena.busreserve.domine.repositories.SeatRepository;
import co.edu.unimagdalena.busreserve.exception.NotFoundException;
import co.edu.unimagdalena.busreserve.services.interfaces.SeatService;
import co.edu.unimagdalena.busreserve.services.mapper.SeatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepo;
    private final BusRepository busRepo;
    private final SeatMapper mapper;

    @Override
    public SeatResponse create(SeatCreateRequest req) {
        Bus bus = busRepo.findById(req.busId())
                .orElseThrow(() -> new NotFoundException("Bus %d not found".formatted(req.busId())));

        if (seatRepo.findByBusIdAndNumber(req.busId(), req.number()).isPresent()) {
            throw new IllegalStateException("Seat %s already exists for this bus".formatted(req.number()));
        }

        Seat seat = mapper.toEntity(req);
        seat.setBus(bus);

        return mapper.toResponse(seatRepo.save(seat));
    }

    @Override
    @Transactional(readOnly = true)
    public SeatResponse get(Long id) {
        return seatRepo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Seat %d not found".formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponse> listByBus(Long busId) {
        if (!busRepo.existsById(busId)) {
            throw new NotFoundException("Bus %d not found".formatted(busId));
        }
        return seatRepo.findByBusId(busId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!seatRepo.existsById(id)) {
            throw new NotFoundException("Seat %d not found".formatted(id));
        }
        seatRepo.deleteById(id);
    }

    @Override
    public void createSeatsForBus(Long busId, int totalSeats) {
        Bus bus = busRepo.findById(busId)
                .orElseThrow(() -> new NotFoundException("Bus %d not found".formatted(busId)));

        List<Seat> seats = new ArrayList<>();
        int preferencialSeats = Math.min(4, totalSeats / 10);

        for (int i = 1; i <= totalSeats; i++) {
            Integer seatNumber = i;
            SeatType type = i <= preferencialSeats ? SeatType.PREFERENTIAL : SeatType.STANDARD;

            Seat seat = Seat.builder()
                    .bus(bus)
                    .number(seatNumber)
                    .type(type)
                    .build();
            seats.add(seat);
        }

        seatRepo.saveAll(seats);
    }
}
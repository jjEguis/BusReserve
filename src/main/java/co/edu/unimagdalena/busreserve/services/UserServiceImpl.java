// ==================== UserServiceImpl.java ====================
package co.edu.unimagdalena.busreserve.services;

import co.edu.unimagdalena.busreserve.api.dto.UserDtos.*;
import co.edu.unimagdalena.busreserve.domine.entities.Role;
import co.edu.unimagdalena.busreserve.domine.entities.User;
import co.edu.unimagdalena.busreserve.domine.repositories.UserRepository;
import co.edu.unimagdalena.busreserve.exception.NotFoundException;
import co.edu.unimagdalena.busreserve.services.mapper.UserMapper;
import co.edu.unimagdalena.busreserve.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserCreateRequest req) {
        boolean emailTaken = userRepo.findByEmail(req.email()).isPresent();
        if (emailTaken)
            throw new IllegalStateException("Email already registered");

        boolean phoneTaken = userRepo.findByPhone(req.phone()).isPresent();
        if (phoneTaken)
            throw new NotFoundException("Phone already registered");

        User user = mapper.toEntity(req);
        user.setPasswordHash(passwordEncoder.encode(req.password()));

        return mapper.toResponse(userRepo.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return userRepo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        return userRepo.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findByRole(Role role) {
        return userRepo.findAll()
                .stream()
                .filter(u -> u.getRole() == role)
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest req) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean emailTaken = req.email() != null &&
                userRepo.findByEmail(req.email())
                        .filter(u -> !u.getId().equals(id))
                        .isPresent();

        if (emailTaken)
            throw new NotFoundException("Email already registered");

        boolean phoneTaken = req.phone() != null &&
                userRepo.findByPhone(req.phone())
                        .filter(u -> !u.getId().equals(id))
                        .isPresent();

        if (phoneTaken)
            throw new NotFoundException("Phone already registered");

        mapper.patch(user, req);

        return mapper.toResponse(userRepo.save(user));
    }

    @Override
    public void delete(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepo.delete(user);
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
}
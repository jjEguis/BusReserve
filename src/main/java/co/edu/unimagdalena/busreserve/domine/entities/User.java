package co.edu.unimagdalena.busreserve.domine.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column()
    private String name;
    @Column()
    private String email;
    @Column()
    private String phone;
    @Column()
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column()
    private Boolean status;
    @Column()
    private String passwordHash;
    @Column()
    private LocalDateTime createdAt;

}

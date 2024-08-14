package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userid; // Ensure the field name is 'userid'

    @NotBlank(message = "First name is mandatory")
    @Column(name = "firstname")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Column(name = "lastname")
    private String lastName;

    @NotBlank(message = "Username is mandatory")
    @Column(name = "username", unique = true)
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "Password hash is mandatory")
    @Column(name = "passwordhash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "user_role")
    private UserRole role = UserRole.USER;  // Default role value

    public enum UserRole {
        USER,
        ADMIN
    }

    @PrePersist
    protected void onCreate() {
        if (role == null ) {
            role = UserRole.USER;
        }
    }

}

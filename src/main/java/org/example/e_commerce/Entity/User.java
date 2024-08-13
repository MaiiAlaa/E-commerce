package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
    private String firstname;

    @NotBlank(message = "Last name is mandatory")
    @Column(name = "lastname")
    private String lastname;

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

    @NotBlank(message = "Role is mandatory")
    @Column(name = "role")
    private String role;

    public @NotBlank(message = "Role is mandatory") String getRole() {
        return role;
    }

    public void setRole(@NotBlank(message = "Role is mandatory") String role) {
        this.role = role;
    }
}
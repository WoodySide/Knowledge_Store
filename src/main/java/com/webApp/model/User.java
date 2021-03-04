package com.webApp.model;

import com.webApp.model.audit.AuditModel;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity(name = "User")
@Table(name = "users", schema = "knowledge_data")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@ApiModel(description = "All available users")
public class User extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "name should not be empty")
    @Size(max = 40)
    private String name;

    @Column(name = "username")
    @NotBlank(message = "username should not be empty")
    @Size(max = 20)
    private String username;

    @Column(name = "email")
    @NaturalId
    @NotBlank(message = "email should not be empty")
    @Size(max = 40)
    @Email
    private String email;

    @Column(name = "password")
    @NotBlank(message = "password should not be empty")
    @Size(max = 100)
    private String password;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> userRoles;
}

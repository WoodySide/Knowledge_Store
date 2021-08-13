package com.webApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.webApp.model.audit.AuditModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity(name = "Title")
@Table(name = "titles")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@ApiModel(value = "Title", description = "Title entity")
public class Title extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "The database generated title ID",
                      example = "10")
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Title name should not be empty")
    @Size(min = 2, max = 50, message = "Title name should be greater " +
            "than 2 and less than 50 symbols")
    @ApiModelProperty(value = "Name of the title which is going to be created by user",
                      example = "History")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "title",
             fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonManagedReference
    private Set<Category> categories;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Title(String name) {
        this.name = name;
    }

    public Title(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}

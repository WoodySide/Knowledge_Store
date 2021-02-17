package com.webApp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.webApp.audit.AuditModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity(name = "Title")
@Table(name = "titles", schema = "knowledge_data", catalog = "knowledge_store")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@ApiModel(description = "All details about the Title")
public class Title extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "The database generated title ID")
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Title name should not be empty")
    @Size(min = 2, max = 50, message = "Title name should be greater " +
            "than 2 and less than 50 symbols")
    @ApiModelProperty(notes = "Name of the title")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "title",
             fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @JsonManagedReference
    private Set<Category> categories;

    public Title(String name) {
        this.name = name;
    }

    public Title(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

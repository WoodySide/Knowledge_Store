package com.webApp.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.webApp.model.audit.AuditModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Set;

@Entity(name = "Category")
@Table(name = "categories", schema = "knowledge_data")
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@ApiModel(description = "All details about the Category")
public class Category extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "The database generated category ID")
    private Long id;

    @Column(name = "name",nullable = false)
    @NotBlank(message = "Category name should not be empty")
    @Size(min = 2, max = 50, message = "Category name should be greater than 2 " +
            "and less than 50 symbols")
    @ApiModelProperty(notes = "Category name")
    private String name;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "title_id")
    @JsonBackReference
    private Title title;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category",
             fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @JsonManagedReference
    private Set<Link> links;

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

package com.webApp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name",nullable = false)
    @NotBlank(message = "Category name should not be empty")
    @Size(min = 2, max = 50, message = "Category name should be greater than 2 " +
            "and less than 50 symbols")
    private String name;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "title_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Title title;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category",
             fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Link> links;


    public void setLinks(Set<Link> links) {
        this.links = links;
        
        for(Link link: links) {
            link.setCategory(this);
        }
    }
}

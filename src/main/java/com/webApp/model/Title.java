package com.webApp.model;

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
public class Title {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Title name should not be empty")
    @Size(min = 2, max = 50, message = "Title name should be greater " +
            "than 2 and less than 50 symbols")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "title",
             fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Category> categories;

    public Title(String name) {
        this.name = name;
    }

    public Title(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;

        for(Category category: categories) {
            category.setTitle(this);
        }
    }
}

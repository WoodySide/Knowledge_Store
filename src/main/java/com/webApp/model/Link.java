package com.webApp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.webApp.model.audit.AuditModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Entity(name = "Link")
@Table(name = "links")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@ApiModel(description = "All details about the Links")
public class Link extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "The database generated ID")
    private Long id;

    @Column(name = "link_name", nullable = false)
    @Pattern(regexp = "(https://|http://).+", message = "Please use following pattern: http:// or https://")
    @ApiModelProperty(notes = "Name of the link")
    private String linkName;

    @Column(name = "description")
    @Size(min = 0, max = 500, message = "Description should not be greater than 500 symbols")
    @ApiModelProperty(notes = "Description about the link")
    private String linkDescription;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

    public Link(Long id, String linkName) {
        this.id = id;
        this.linkName = linkName;
    }

    public Link(String linkName) {
        this.linkName = linkName;
    }
}

package ua.org.ubts.stats.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name = "program")
@Getter
@Setter
@NoArgsConstructor
public class ProgramEntity extends BaseEntity<Integer> {

    public ProgramEntity(String name) {
        this.name = name;
    }

    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "program")
    private List<GroupEntity> groups;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;

}

package ua.org.ubts.stats.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name = "organization")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationEntity extends BaseEntity<Integer> {

    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "organization")
    private List<ProgramEntity> programs;

}

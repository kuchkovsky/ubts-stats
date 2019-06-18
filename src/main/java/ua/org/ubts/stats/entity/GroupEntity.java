package ua.org.ubts.stats.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name = "group_")
@Getter
@Setter
@NoArgsConstructor
public class GroupEntity extends BaseEntity<Integer> {

    public GroupEntity(String name) {
        this.name = name;
    }

    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "program_id")
    private ProgramEntity program;

    @OneToMany(mappedBy = "group")
    private List<UserEntity> users;

}

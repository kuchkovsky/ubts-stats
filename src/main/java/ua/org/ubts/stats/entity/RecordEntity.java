package ua.org.ubts.stats.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "record")
@Getter
@Setter
@NoArgsConstructor
public class RecordEntity extends BaseEntity<Long> {

    @Column(name = "prayer_minutes", nullable = false)
    private Integer prayerMinutes;

    @Column(name = "christ_witnesses", nullable = false)
    private Integer christWitnesses;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

}

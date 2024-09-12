package fern.nail.art.nailscheduler.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user_procedure_times")
public class UserProcedureTime {
    @EmbeddedId
    private Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer duration;

    public UserProcedureTime(User user, ProcedureType procedure, Integer duration) {
        this.id = new Id(user.getId(), procedure);
        this.user = user;
        this.duration = duration;
    }

    @Getter
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id implements Serializable {
        private Long userId;

        @Enumerated(EnumType.STRING)
        private ProcedureType procedure;
    }
}

package in.madhav.moneymanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String profileImageUrl;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    @Column(name = "activation_token", unique = true)
    private String activationToken;

    @PrePersist
    public void prePersist() {
        if (isActive == null) {
            isActive = false;
        }
    }
}

// package in.bushansirgur.moneymanager.entity;

// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import org.hibernate.annotations.CreationTimestamp;
// import org.hibernate.annotations.UpdateTimestamp;

// import java.time.LocalDateTime;

// @Entity
// @Table(name = "tbl_profiles")
// @Data
// @AllArgsConstructor
// @NoArgsConstructor
// @Builder
// public class ProfileEntity {

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;
// private String fullName;
// @Column(unique = true)
// private String email;
// private String password;
// private String profileImageUrl;
// @Column(updatable = false)
// @CreationTimestamp
// private LocalDateTime createdAt;
// @UpdateTimestamp
// private LocalDateTime updatedAt;
// private Boolean isActive;
// private String activationToken;

// @PrePersist
// public void prePersist() {
// if (this.isActive == null) {
// isActive = false;
// }
// }

// }

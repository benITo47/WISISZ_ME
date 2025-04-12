package me.wisisz.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "refresh_token", schema = "wisiszme")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "issued_at", nullable = false)
    private OffsetDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked;

    public Integer getId() {
        return id;
    }

    public void setId(Integer tokenId) {
        this.id = tokenId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public OffsetDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(OffsetDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsRevoked() {
        return isRevoked;
    }

    public void setIsRevoked(Boolean isRevoked) {
        this.isRevoked = isRevoked;
    }
}

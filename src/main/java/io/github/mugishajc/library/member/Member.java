package io.github.mugishajc.library.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(name = "joined_on", nullable = false, updatable = false)
    private LocalDate joinedOn;

    protected Member() {
    }

    public Member(String fullName, String email, String phone, LocalDate joinedOn) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.joinedOn = joinedOn;
    }

    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }

    public void update(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public LocalDate getJoinedOn() {
        return joinedOn;
    }
}

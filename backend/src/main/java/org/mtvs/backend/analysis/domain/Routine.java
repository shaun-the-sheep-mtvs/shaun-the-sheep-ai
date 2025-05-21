package org.mtvs.backend.analysis.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.mtvs.backend.analysis.domain.enums.Kind;
import org.mtvs.backend.analysis.domain.enums.Time;

import java.time.LocalDateTime;

@Entity
@Table(name = "routines")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    private Kind kind;

    @Enumerated(EnumType.STRING)
    private Time time;

    @Column(name = "order")
    private int order;

    @Column(name = "method")
    private String method;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private User user;

    @CurrentTimestamp
    private LocalDateTime created_at;

    public Routine() {
    }

    public Routine(String name, Kind kind, Time time, int order, String method, User user) {
        this.name = name;
        this.kind = kind;
        this.time = time;
        this.order = order;
        this.method = method;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}

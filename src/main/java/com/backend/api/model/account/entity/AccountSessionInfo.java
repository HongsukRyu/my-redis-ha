package com.backend.api.model.account.entity;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "account_session_info")
public class AccountSessionInfo {

    @Id
    @Size(max = 50)
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Size(max = 200)
    @NotNull
    @Column(name = "client_ip", nullable = false, length = 200)
    private String clientIp;

    @Column(name = "last_login_date")
    private Date lastLoginDate;

    @Size(max = 600)
    @Column(name = "token", length = 600)
    private String token;

    @Column(name = "last_refresh_time")
    private Date lastRefreshTime;

    @Column(name = "expiration_time")
    private Integer expirationTime;

    @Size(max = 600)
    @Column(name = "refresh_token", length = 600)
    private String refreshToken;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AccountSessionInfo that = (AccountSessionInfo) o;
        return getUserId() != null && Objects.equals(getUserId(), that.getUserId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
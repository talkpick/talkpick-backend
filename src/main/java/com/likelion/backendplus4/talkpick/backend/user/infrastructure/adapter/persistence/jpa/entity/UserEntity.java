package com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity;

import static jakarta.persistence.EnumType.STRING;

import java.time.LocalDate;

import org.hibernate.annotations.SQLRestriction;

import com.likelion.backendplus4.talkpick.backend.common.entity.BaseSoftDeleteEntity;
import com.likelion.backendplus4.talkpick.backend.user.domain.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 사용자 정보를 저장하는 JPA Entity.
 *
 * @since 2025-05-16
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "users")
@SQLRestriction("is_delete = false")
public class UserEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "account", nullable = false, unique = true, length = 20)
    private String account;

    @Column(name = "password", nullable = false, length = 256)
    private String password;

    @Enumerated(STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "birth_day", nullable = false)
    private LocalDate birthday;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "nickname", length = 20)
    private String nickName;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    /**
     * 전달받은 User 객체의 정보로 현재 UserEntity 객체를 업데이트한다.
     *
     * @param user 업데이트할 사용자 정보가 담긴 User 객체
     * @author 박찬병
     * @since 2025-05-16
     */
    public void updateUser(User user) {
        this.gender = user.getGender();
        this.birthday = user.getBirthday();
        this.name = user.getName();
        this.nickName = user.getNickName();
        this.email = user.getEmail();
    }
}

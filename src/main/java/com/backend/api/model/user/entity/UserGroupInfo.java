package com.backend.api.model.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "user_group_info")
public class UserGroupInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "group_name", nullable = false, length = 50)
    private String groupName;

    @Size(max = 5)
    @NotNull
    @Column(name = "is_enable", nullable = false, length = 5)
    private String isEnable;

}
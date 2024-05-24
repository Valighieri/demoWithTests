package com.example.demowithtests.domain;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Table(name = "roles_names")
@Getter
public class RoleName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

}




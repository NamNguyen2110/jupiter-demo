package com.example.springclass;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Table(name = "USERS")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @OneToMany(mappedBy = "users")
    private Set<UserPermission> userPermissions;
}

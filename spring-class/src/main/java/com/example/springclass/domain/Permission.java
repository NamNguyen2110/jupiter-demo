package com.example.springclass;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Table(name = "PERMISSION")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @OneToMany(mappedBy = "permission")
    private Set<UserPermission> userPermissions;
}

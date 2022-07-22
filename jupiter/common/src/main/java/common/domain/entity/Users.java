package common.domain.entity;

import common.domain.converter.GenderEnumConverter;
import common.domain.enums.Gender;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users implements Serializable {
    @Id
    @Column(name = "users_id", unique = true, nullable = false, length = 36)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String usersId;
    @Convert(converter = GenderEnumConverter.class)
    private Gender gender;
}

package ru.mirea.newrav1k.userservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "t_users",
        schema = "user_management",
        indexes = {
                @Index(name = "idx_user_ids", columnList = "id")
        }
)
public class User {

    @Id
    private UUID id;

    private String username;

}
package ru.job4j.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.job4j.auth.validation.Operation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Id must be non null", groups = {
            Operation.OnUpdate.class, Operation.OnDelete.class
    })
    private int id;
    @NotBlank(message = "Login must be not empty", groups = {
            Operation.OnCreate.class, Operation.OnUpdate.class, Operation.OnDelete.class})
    private String login;
    @NotBlank(message = "Password must be not empty", groups = {
            Operation.OnCreate.class, Operation.OnUpdate.class, Operation.OnDelete.class})
    @Size(min = 6, message = "Password is too short")
    private String password;
}

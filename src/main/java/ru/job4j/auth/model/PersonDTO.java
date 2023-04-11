package ru.job4j.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
    @NotBlank(message = "Login must be not empty")
    private String login;
    @NotBlank(message = "Password must be not empty")
    @Size(min = 6, message = "Password is too short")
    private String password;
}

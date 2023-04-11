package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.model.PersonDTO;
import ru.job4j.auth.service.PersonService;
import ru.job4j.auth.validation.Operation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());
    private final ObjectMapper objectMapper;
    private final PersonService persons;
    private BCryptPasswordEncoder encoder;

    @GetMapping("/")
    public ResponseEntity<List<Person>> findAll() {
        return new ResponseEntity<>(
                this.persons.findAll(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<>(
                person.orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person is not found. Please, check login and password."
                )), HttpStatus.OK
        );
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Login and password mustn't be empty");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password");
        }
        return new ResponseEntity<>(
                this.persons.save(person),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Login and password mustn't be empty");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password");
        }
        return new ResponseEntity<>(this.persons.update(person)
                ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public ResponseEntity<Void> delete(@Valid @PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        return new ResponseEntity<>(this.persons.delete(person)
                ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping("/sign-up")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<?> signUp(@Valid @RequestBody Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Login and password mustn't be empty");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        persons.save(person);
        return ResponseEntity.ok("success");
    }

    @PatchMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> updateDTO(@Valid @RequestBody PersonDTO personDTO) {
        var person = this.persons.findByUsername(personDTO.getLogin());
        if (person.getLogin() == null) {
            throw new NullPointerException("Login mustn't be empty");
        }
        person.setPassword(personDTO.getPassword());
        return new ResponseEntity<>(this.persons.update(person)
                ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    public void handleNullPointerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {
            {
                put("message", "Some of fields empty");
                put("details", e.getMessage());
            }
        }));
        LOGGER.error(e.getMessage());
    }
}

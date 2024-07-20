package com.crackit.SpringSecurityJWT.secured;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crackit/v1/management")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    @GetMapping
    public String getTeacher() {
        return "Secured Endpoint :: GET - Teacher controller";
    }

    @PostMapping
    public String post() {
        return "POST:: management controller";
    }
}

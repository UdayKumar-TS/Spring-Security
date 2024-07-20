package com.crackit.SpringSecurityJWT.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "student")
public class Student {
        @Id
        private String id;
        private String email;
        private String name;
        private String age;
        private String grade;
        private String address;

    }
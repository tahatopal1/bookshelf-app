package com.example.bookshelf.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser implements Serializable {

    private String id;

    private String username;
    private String password;
    private String name;
    private String surname;
    private String manager;
    private int authCode; // 1-User, 2-Admin, 3-AppMaster
    private int approved; // 0-Not Approved, 1-Approved, 2-Partially Approved
}

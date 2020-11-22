package com.example.bookshelf.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionResult {
    private String errorDesc;
    private int errorCode;
    private String usage; // Holds data could be needed
}

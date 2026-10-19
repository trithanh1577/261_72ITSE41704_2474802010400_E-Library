package com.elibrary.web.form;

import jakarta.validation.constraints.Size;

public class BorrowRequestForm {

    @Size(max = 500, message = "Note must be at most 500 characters")
    private String note;

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

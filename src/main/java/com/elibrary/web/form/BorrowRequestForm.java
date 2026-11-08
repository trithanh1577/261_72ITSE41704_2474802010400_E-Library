package com.elibrary.web.form;

import jakarta.validation.constraints.Size;

public class BorrowRequestForm {

    @Size(max = 500, message = "{validation.note.max}")
    private String note;

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

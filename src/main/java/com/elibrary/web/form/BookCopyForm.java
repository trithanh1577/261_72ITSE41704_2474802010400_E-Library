package com.elibrary.web.form;

import com.elibrary.domain.CopyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookCopyForm {

    @NotBlank(message = "Copy code is required")
    @Size(max = 50, message = "Copy code must be at most 50 characters")
    private String copyCode;

    @NotNull(message = "Copy status is required")
    private CopyStatus status = CopyStatus.AVAILABLE;

    public BookCopyForm() { }

    public BookCopyForm(String copyCode, CopyStatus status) {
        this.copyCode = copyCode;
        this.status = status;
    }

    public String getCopyCode() { return copyCode; }
    public void setCopyCode(String copyCode) { this.copyCode = copyCode; }
    public CopyStatus getStatus() { return status; }
    public void setStatus(CopyStatus status) { this.status = status; }
}

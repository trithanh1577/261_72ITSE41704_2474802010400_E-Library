package com.elibrary.web;

import com.elibrary.domain.BookCopy;
import com.elibrary.domain.CopyStatus;
import com.elibrary.service.BookCopyService;
import com.elibrary.service.BookService;
import com.elibrary.web.form.BookCopyForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookCopyController {

    private final BookService bookService;
    private final BookCopyService copyService;

    public BookCopyController(BookService bookService, BookCopyService copyService) {
        this.bookService = bookService;
        this.copyService = copyService;
    }

    @GetMapping("/books/{bookId}/copies/new")
    public String newCopy(@PathVariable Long bookId, Model model) {
        model.addAttribute("book", bookService.getRequired(bookId));
        model.addAttribute("copyForm", new BookCopyForm());
        model.addAttribute("statuses", CopyStatus.values());
        model.addAttribute("pageTitle", "Add physical copy");
        model.addAttribute("formAction", "/books/" + bookId + "/copies");
        return "copies/form";
    }

    @GetMapping("/copies/{id}/edit")
    public String editCopy(@PathVariable Long id, Model model) {
        BookCopy copy = copyService.getRequired(id);
        model.addAttribute("book", copy.getBook());
        model.addAttribute("copy", copy);
        model.addAttribute("copyForm", new BookCopyForm(copy.getCopyCode(), copy.getStatus()));
        model.addAttribute("statuses", CopyStatus.values());
        model.addAttribute("pageTitle", "Edit physical copy");
        model.addAttribute("formAction", "/copies/" + id);
        return "copies/form";
    }

    @PostMapping("/books/{bookId}/copies")
    public String addCopy(@PathVariable Long bookId,
                          @Valid @ModelAttribute("copyForm") BookCopyForm form,
                          BindingResult bindingResult, Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(bookId, model, "Add physical copy");
            return "copies/form";
        }
        try {
            copyService.addCopy(bookId, form.getCopyCode());
            redirectAttributes.addFlashAttribute("success", "Physical copy added.");
            return "redirect:/books/" + bookId;
        } catch (IllegalArgumentException exception) {
            prepareFormModel(bookId, model, "Add physical copy");
            model.addAttribute("formError", exception.getMessage());
            return "copies/form";
        }
    }

    @PostMapping("/copies/{id}")
    public String updateCopy(@PathVariable Long id,
                             @Valid @ModelAttribute("copyForm") BookCopyForm form,
                             BindingResult bindingResult, Model model,
                             RedirectAttributes redirectAttributes) {
        BookCopy existing = copyService.getRequired(id);
        if (bindingResult.hasErrors()) {
            prepareEditModel(existing, model, "Edit physical copy");
            return "copies/form";
        }
        try {
            copyService.update(id, form.getCopyCode(), form.getStatus());
            redirectAttributes.addFlashAttribute("success", "Physical copy updated.");
            return "redirect:/books/" + existing.getBook().getId();
        } catch (IllegalArgumentException exception) {
            prepareEditModel(existing, model, "Edit physical copy");
            model.addAttribute("formError", exception.getMessage());
            return "copies/form";
        }
    }

    private void prepareFormModel(Long bookId, Model model, String pageTitle) {
        model.addAttribute("book", bookService.getRequired(bookId));
        model.addAttribute("statuses", CopyStatus.values());
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("formAction", "/books/" + bookId + "/copies");
    }

    private void prepareEditModel(BookCopy copy, Model model, String pageTitle) {
        model.addAttribute("book", copy.getBook());
        model.addAttribute("copy", copy);
        model.addAttribute("statuses", CopyStatus.values());
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("formAction", "/copies/" + copy.getId());
    }
}

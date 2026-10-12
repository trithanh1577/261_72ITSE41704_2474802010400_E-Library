package com.elibrary.web;

import com.elibrary.domain.Book;
import com.elibrary.service.BookService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) { this.bookService = bookService; }

    @GetMapping("/books")
    public String list(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("books", bookService.search(keyword));
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "books/list";
    }

    @GetMapping("/books/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Book book = bookService.getRequired(id);
        model.addAttribute("book", book);
        model.addAttribute("copies", bookService.getCopies(id));
        model.addAttribute("availableCount", bookService.countAvailableCopies(id));
        return "books/detail";
    }

    @GetMapping("/books/new")
    public String newBook(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("pageTitle", "Add book");
        return "books/form";
    }

    @GetMapping("/books/{id}/edit")
    public String editBook(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getRequired(id));
        model.addAttribute("pageTitle", "Edit book");
        return "books/form";
    }

    @PostMapping("/books/save")
    public String save(@Valid @ModelAttribute("book") Book book,
                       BindingResult bindingResult, Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", book.getId() == null ? "Add book" : "Edit book");
            return "books/form";
        }
        try {
            Book saved = bookService.save(book);
            redirectAttributes.addFlashAttribute("success", "Book saved successfully.");
            return "redirect:/books/" + saved.getId();
        } catch (IllegalArgumentException exception) {
            model.addAttribute("pageTitle", book.getId() == null ? "Add book" : "Edit book");
            model.addAttribute("formError", exception.getMessage());
            return "books/form";
        }
    }

    @PostMapping("/books/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.deactivate(id);
        redirectAttributes.addFlashAttribute("success", "Book was deactivated.");
        return "redirect:/books";
    }
}

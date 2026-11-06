package com.elibrary.web;

import com.elibrary.service.CirculationService;
import com.elibrary.domain.FineSettlementType;
import com.elibrary.web.form.BorrowRequestForm;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.time.LocalDate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CirculationController {

    private final CirculationService circulationService;

    public CirculationController(CirculationService circulationService) {
        this.circulationService = circulationService;
    }

    @PostMapping("/books/{bookId}/requests")
    public String submitRequest(@PathVariable Long bookId,
                                @Valid @ModelAttribute("requestForm") BorrowRequestForm form,
                                BindingResult bindingResult,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "The request note must be at most 500 characters.");
            return "redirect:/books/" + bookId;
        }
        try {
            circulationService.submitRequest(authentication.getName(), bookId, form.getNote());
            redirectAttributes.addFlashAttribute("success", "Borrowing request submitted for librarian review.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/books/{bookId}/reservations")
    public String reserve(@PathVariable Long bookId, Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        try {
            circulationService.submitReservation(authentication.getName(), bookId);
            redirectAttributes.addFlashAttribute("success", "Reservation added to the queue.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/books/" + bookId;
    }

    @GetMapping("/circulation/reservations")
    public String reservations(Model model) {
        model.addAttribute("reservations", circulationService.listReservations());
        return "circulation/reservations";
    }

    @PostMapping("/reservations/{id}/claim")
    public String claimReservation(@PathVariable Long id, Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            circulationService.claimReservation(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Reserved copy checked out successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/circulation/mine";
    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            circulationService.cancelReservation(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Reservation cancelled.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/circulation/mine";
    }

    @GetMapping("/circulation/requests")
    public String requests(Model model) {
        model.addAttribute("requests", circulationService.listRequests());
        return "circulation/requests";
    }

    @PostMapping("/circulation/requests/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            circulationService.approveRequest(id);
            redirectAttributes.addFlashAttribute("success", "Request approved and loan created.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/circulation/requests";
    }

    @PostMapping("/circulation/requests/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(required = false) String reason,
                         RedirectAttributes redirectAttributes) {
        try {
            circulationService.rejectRequest(id, reason);
            redirectAttributes.addFlashAttribute("success", "Request rejected.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/circulation/requests";
    }

    @GetMapping("/circulation/loans")
    public String loans(@RequestParam(defaultValue = "") String query,
                        @RequestParam(defaultValue = "ALL") String status,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        var loanPage = circulationService.searchLoans(query, status, page);
        model.addAttribute("loans", loanPage.getContent());
        model.addAttribute("loanPage", loanPage);
        model.addAttribute("query", query);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("popularBooks", circulationService.mostBorrowedTitles());
        model.addAttribute("librarianView", true);
        return "circulation/loans";
    }

    @PostMapping("/circulation/loans/{id}/fine-settlements")
    public String settleFine(@PathVariable Long id,
                             @RequestParam FineSettlementType type,
                             @RequestParam String note,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            var settlement = circulationService.settleFine(id, type, note, authentication.getName());
            String action = type == FineSettlementType.PAID ? "Payment recorded" : "Fine waived";
            redirectAttributes.addFlashAttribute("success", action + ": VND "
                    + settlement.getAmount().toPlainString());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/circulation/loans";
    }

    @GetMapping("/circulation/mine")
    public String mine(Authentication authentication, Model model) {
        model.addAttribute("requests", circulationService.listRequestsForMember(authentication.getName()));
        model.addAttribute("loans", circulationService.listLoansForMember(authentication.getName()));
        model.addAttribute("reservations", circulationService.listReservationsForMember(authentication.getName()));
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("librarianView", false);
        return "circulation/mine";
    }

    @PostMapping("/circulation/loans/{id}/return")
    public String returnLoan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var loan = circulationService.returnLoan(id);
            redirectAttributes.addFlashAttribute("success", "Book returned. Fine recorded: VND "
                    + loan.getFineAmount().toPlainString());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/circulation/loans";
    }
}

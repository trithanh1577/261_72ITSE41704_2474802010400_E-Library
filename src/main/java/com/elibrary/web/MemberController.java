package com.elibrary.web;

import com.elibrary.domain.MembershipType;
import com.elibrary.service.MemberManagementService;
import java.util.Arrays;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {

    private final MemberManagementService memberService;

    public MemberController(MemberManagementService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public String members(Model model) {
        model.addAttribute("members", memberService.listMembers());
        model.addAttribute("membershipTypes", Arrays.asList(MembershipType.values()));
        return "members/list";
    }

    @PostMapping("/members/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String displayName,
                         @RequestParam MembershipType membershipType,
                         @RequestParam(defaultValue = "false") boolean enabled,
                         RedirectAttributes redirectAttributes) {
        try {
            memberService.updateMember(id, displayName, membershipType, enabled);
            redirectAttributes.addFlashAttribute("success", "Member account updated.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/members";
    }
}

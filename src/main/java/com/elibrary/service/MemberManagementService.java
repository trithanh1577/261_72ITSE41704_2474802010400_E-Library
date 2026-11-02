package com.elibrary.service;

import com.elibrary.domain.MembershipType;
import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.UserAccountRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberManagementService {

    private final UserAccountRepository userRepository;

    public MemberManagementService(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserAccount> listMembers() {
        return userRepository.findAllByRoleOrderByDisplayNameAsc(Role.MEMBER);
    }

    @Transactional
    public void updateMember(Long memberId, String displayName, MembershipType membershipType, boolean enabled) {
        String safeName = displayName == null ? "" : displayName.trim();
        if (safeName.isBlank() || safeName.length() > 120) {
            throw new IllegalArgumentException("Display name is required and must be at most 120 characters.");
        }
        if (membershipType == null) {
            throw new IllegalArgumentException("Select a valid membership type.");
        }
        UserAccount member = userRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member account was not found."));
        if (member.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException("Only member accounts can be edited here.");
        }
        member.setDisplayName(safeName);
        member.setMembershipType(membershipType);
        member.setEnabled(enabled);
    }
}

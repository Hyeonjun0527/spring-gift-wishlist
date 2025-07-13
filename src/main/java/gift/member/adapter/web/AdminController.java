package gift.member.adapter.web;

import gift.common.annotation.RequireAdmin;
import gift.member.application.port.in.MemberUseCase;
import gift.member.application.port.in.dto.CreateMemberRequest;
import gift.member.application.port.in.dto.MemberResponse;
import gift.member.application.port.in.dto.UpdateMemberRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final MemberUseCase memberUseCase;

    public AdminController(MemberUseCase memberUseCase) {
        this.memberUseCase = memberUseCase;
    }

    @RequireAdmin
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> members = memberUseCase.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @RequireAdmin
    @PostMapping("/members")
    public ResponseEntity<Void> createMember(@Valid @RequestBody CreateMemberRequest request) {
        MemberResponse memberResponse = memberUseCase.createMember(request);
        return ResponseEntity.created(URI.create("/api/admin/members/" + memberResponse.id())).build();
    }

    @RequireAdmin
    @PutMapping("/members/{id}")
    public ResponseEntity<Void> updateMember(@PathVariable Long id, 
                                           @Valid @RequestBody UpdateMemberRequest request) {
        memberUseCase.updateMember(id, request);
        return ResponseEntity.noContent().build();
    }

    @RequireAdmin
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberUseCase.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
} 
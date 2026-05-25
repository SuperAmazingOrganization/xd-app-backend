package pl.vizja.xdbackend.security.token;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.vizja.xdbackend.user.UserService;
import pl.vizja.xdbackend.user.dto.UserDTO;

@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
@Tag(name = "Tokens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
class TokenController {

    private final TokenService tokenService;
    private final UserService userService;

    @Operation(summary = "Create tokens (access + refresh)")
    @PostMapping
    ResponseEntity<TokenPairDTO> createTokens(
            @RequestBody CreateTokenPairDTO createTokenPairDTO
    ) {
        return ResponseEntity.ok(tokenService.createTokenPair(createTokenPairDTO));
    }

    @Operation(summary = "Replace tokens (generate new token pair)")
    @PutMapping
    ResponseEntity<TokenPairDTO> replaceTokens(
            @RequestBody ReplaceTokenPairDTO replaceTokenPairDTO
    ) {
        return ResponseEntity.ok(tokenService.replaceTokenPair(replaceTokenPairDTO));
    }

    @Operation(summary = "Delete a refresh token (invalidate)")
    @DeleteMapping("/refresh")
    ResponseEntity<Void> deleteRefreshToken(
            @RequestBody DeleteRefreshTokenDTO deleteRefreshTokenDTO
    ) {
        tokenService.deleteRefreshToken(deleteRefreshTokenDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get current user info from token")
    @GetMapping("/me")
    ResponseEntity<UserDTO> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(userService.getUser(userId));
    }
}

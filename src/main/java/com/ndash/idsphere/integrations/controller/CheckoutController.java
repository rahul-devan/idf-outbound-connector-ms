package com.ndash.idsphere.integrations.controller;

import com.ndash.idsphere.integrations.dto.ApiResponse;
import com.ndash.idsphere.integrations.dto.CheckoutRequest;
import com.ndash.idsphere.integrations.dto.CheckoutResponse;
import com.ndash.idsphere.integrations.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<ApiResponse<CheckoutResponse>> createCheckout(
            @RequestBody CheckoutRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        CheckoutResponse response = checkoutService.addToCheckout(request, jwt.getClaim("userId"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, HttpStatus.CREATED.value()));
    }


    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<String>> deleteAllCheckouts() {

        checkoutService.deleteAllCheckouts();

        return ResponseEntity.ok(
                ApiResponse.success("All checkout records deleted", HttpStatus.OK.value())
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<CheckoutResponse>>> getPendingCheckouts() {
        List<CheckoutResponse> list = checkoutService.getPendingCheckouts();
        return ResponseEntity.ok(
                ApiResponse.success(list, HttpStatus.OK.value())
        );
    }



}

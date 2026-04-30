package com.nguyenquyen.chatapp.controller;


import com.nguyenquyen.chatapp.dto.request.ChatMessageRequest;
import com.nguyenquyen.chatapp.dto.response.ApiResponse;
import com.nguyenquyen.chatapp.dto.response.ChatMessageResponse;
import com.nguyenquyen.chatapp.dto.response.PageResponse;
import com.nguyenquyen.chatapp.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @PostMapping
    ApiResponse<ChatMessageResponse> sendChatMessage(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid ChatMessageRequest request) {
        var senderId = jwt.getSubject();
        var data = chatMessageService.sendChatMessage(senderId, request);

        return ApiResponse.<ChatMessageResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Chat message sent successfully")
                .data(data)
                .build();
    }

    @GetMapping("/conversations/{conversationId}/messages")
    ApiResponse<PageResponse<ChatMessageResponse>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        var data = chatMessageService.getMessagesByConversationId(conversationId, page, size);
        return ApiResponse.<PageResponse<ChatMessageResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Messages retrieved successfully")
                .data(data)
                .build();
    }
}

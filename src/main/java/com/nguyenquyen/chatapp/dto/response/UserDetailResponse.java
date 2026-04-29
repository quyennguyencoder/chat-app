package com.nguyenquyen.chatapp.dto.response;

import lombok.Builder;

@Builder
public record UserDetailResponse(
        String userId,
        String email,
        String username
) {
}

package com.nguyenquyen.chatapp.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MessageMediaResponse(
        String fileName,
        String fileType,
        String thumbnailUrl,
        LocalDateTime uploadedAt
) {
}

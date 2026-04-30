package com.nguyenquyen.chatapp.dto.request;

public record MessageMediaRequest(
        String fileName,
        String fileType,
        String thumbnailUrl
) {
}

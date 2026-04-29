package com.nguyenquyen.chatapp.dto.response;


import com.nguyenquyen.chatapp.common.ConversationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class CreateConversationResponse {
    private String id;
    private String name;
    private String conversationAvatar;
    private ConversationType conversationType;
    private List<ParticipantResponse> participantInfo;
    private LocalDateTime createdAt;
}
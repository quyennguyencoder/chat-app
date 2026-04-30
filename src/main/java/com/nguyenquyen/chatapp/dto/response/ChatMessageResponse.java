package com.nguyenquyen.chatapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nguyenquyen.chatapp.common.MessageType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageResponse implements Serializable {
    private String id;
    private String tempId;
    private String conversationId;
    private String conversationAvatar;
    private String senderId;
    private String senderName;
    private String content;
    private MessageType messageType;
    private List<MessageMediaResponse> messageMedia;
    private LocalDateTime createdAt;
}

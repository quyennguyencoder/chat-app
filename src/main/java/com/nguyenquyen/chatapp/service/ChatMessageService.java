package com.nguyenquyen.chatapp.service;


import com.nguyenquyen.chatapp.common.MessageMedia;
import com.nguyenquyen.chatapp.dto.request.ChatMessageRequest;
import com.nguyenquyen.chatapp.dto.response.ChatMessageResponse;
import com.nguyenquyen.chatapp.dto.response.MessageMediaResponse;
import com.nguyenquyen.chatapp.dto.response.PageResponse;
import com.nguyenquyen.chatapp.entity.ChatMessage;
import com.nguyenquyen.chatapp.entity.Conversation;
import com.nguyenquyen.chatapp.entity.User;
import com.nguyenquyen.chatapp.exception.AppException;
import com.nguyenquyen.chatapp.exception.ErrorCode;
import com.nguyenquyen.chatapp.repository.ChatMessageRepository;
import com.nguyenquyen.chatapp.repository.ConversationRepository;
import com.nguyenquyen.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;


    @Transactional(rollbackFor = Exception.class)
    public ChatMessageResponse sendChatMessage(String senderId, ChatMessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Conversation conversation = conversationRepository.findByIdAndMember(request.conversationId(), senderId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_CONVERSATION_MEMBER));

        List<MessageMedia> media = request.messageMedia() != null && !request.messageMedia().isEmpty() ?
                request.messageMedia().stream()
                        .map(messageMedia -> MessageMedia.builder()
                                .fileName(messageMedia.fileName())
                                .fileType(messageMedia.fileType())
                                .thumbnailUrl(messageMedia.thumbnailUrl())
                                .build())
                        .toList(): List.of();

        ChatMessage message = ChatMessage.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.content())
                .messageType(request.messageType())
                .mediaFiles(media)
                .build();

        chatMessageRepository.save(message);

        conversation.setLastMessageId(message.getId());
        conversation.setLastMessageContent(message.getContent());
        conversation.setLastMessageTime(message.getSentAt());
        conversationRepository.save(conversation);

        List<String> recipientIds = conversation.getParticipants()
                .stream()
                .filter(participant -> !participant.getUser().getId().equals(senderId))
                .map(participant -> participant.getUser().getId())
                .toList();

        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(message.getId())
                .tempId(request.tempId())
                .conversationId(message.getConversation().getId())
                .conversationAvatar(message.getConversation().getConversationAvatar())
                .senderId(sender.getId())
                .senderName(sender.getUsername())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .messageMedia(message.getMediaFiles().stream()
                        .map(messageMedia -> MessageMediaResponse.builder()
                                .fileName(messageMedia.getFileName())
                                .fileType(messageMedia.getFileType())
                                .thumbnailUrl(messageMedia.getThumbnailUrl())
                                .uploadedAt(messageMedia.getUploadedAt())
                                .build())
                        .toList())
                .build();

        return response;
    }

    public PageResponse<ChatMessageResponse> getMessagesByConversationId(String conversationId, int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) throw new AppException(ErrorCode.UNAUTHORIZED);

        String userId = authentication.getName();

        Conversation conversation = conversationRepository.findByIdAndMember(conversationId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_CONVERSATION_MEMBER));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        Page<ChatMessage> chatMessagePage = chatMessageRepository.findByConversationId(conversationId, pageable);

        List<ChatMessage> messages = chatMessagePage.getContent();

        List<ChatMessageResponse> responses = messages.stream()
                .map(message -> ChatMessageResponse.builder()
                        .id(message.getId())
                        .conversationId(conversation.getId())
                        .conversationAvatar(conversation.getConversationAvatar())
                        .senderId(message.getSender().getId())
                        .senderName(message.getSender().getUsername())
                        .content(message.getContent())
                        .messageType(message.getMessageType())
                        .messageMedia(message.getMediaFiles().stream()
                                .map(messageMedia -> MessageMediaResponse.builder()
                                        .fileName(messageMedia.getFileName())
                                        .fileType(messageMedia.getFileType())
                                        .thumbnailUrl(messageMedia.getThumbnailUrl())
                                        .uploadedAt(messageMedia.getUploadedAt())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return PageResponse.<ChatMessageResponse>builder()
                .currentPage(page)
                .pageSize(pageable.getPageSize())
                .totalPages(chatMessagePage.getTotalPages())
                .totalElements(chatMessagePage.getTotalElements())
                .content(responses)
                .build();
    }

}

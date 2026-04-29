package com.nguyenquyen.chatapp.repository;


import com.nguyenquyen.chatapp.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    @EntityGraph(attributePaths = {"sender"})
    Page<ChatMessage> findByConversationId(String conversationId, Pageable pageable);
}

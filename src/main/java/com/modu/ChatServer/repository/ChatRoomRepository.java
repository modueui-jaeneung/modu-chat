package com.modu.ChatServer.repository;

import com.modu.ChatServer.domain.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    List<ChatRoom> findByOwnerIdOrParticipantId(String ownerId, String ParticipantId);
}

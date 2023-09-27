package com.modu.ChatServer.dto;

import com.modu.ChatServer.domain.ChatRoom;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomDto {

    private String roomId;
    private String ownerId;
    private String participantId;
    private String title;

    public ChatRoomDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.ownerId = chatRoom.getOwnerId();
        this.participantId = chatRoom.getParticipantId();
        this.title = chatRoom.getTitle();
    }

    public ChatRoom toEntity() {
        return ChatRoom.builder()
                .roomId(roomId)
                .ownerId(ownerId)
                .participantId(participantId)
                .title(title)
                .build();
    }
}

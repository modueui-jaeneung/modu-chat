package com.modu.ChatServer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "room")
public class ChatRoom {

    @Id
    @Field("_id")
    private String roomId;
    private String title;

    @Field("owner_id")
    private String ownerId;

    @Field("participant_id")
    private String participantId;
}

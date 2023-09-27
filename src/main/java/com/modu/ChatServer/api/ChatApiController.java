package com.modu.ChatServer.api;

import com.modu.ChatServer.domain.ChatRoom;
import com.modu.ChatServer.dto.ChatMessageDto;
import com.modu.ChatServer.dto.ChatRoomDto;
import com.modu.ChatServer.dto.UserIdDto;
import com.modu.ChatServer.repository.ChatMessageRepository;
import com.modu.ChatServer.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatApiController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

//    @PostMapping("/rooms")
//    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody ChatRoomDto chatRoomDto) {
//        chatRoomRepository.save(chatRoomDto.toEntity());
//        return new ResponseEntity<ChatRoomDto>(chatRoomDto, HttpStatus.CREATED);
//    }

    // 채팅방 생성 (인가 로직 추가)
//    @PostMapping("/rooms")
//    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody ChatRoomDto chatRoomDto, @RequestBody UserIdDto userIdDto) {
//        String userId = userIdDto.getUserId();
//
//        if (!userId.equals(chatRoomDto.getParticipantId())) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//
//        chatRoomRepository.save(chatRoomDto.toEntity());
//        return new ResponseEntity<ChatRoomDto>(chatRoomDto, HttpStatus.CREATED);
//    }

    // 채팅방 생성 (쿼리 파라미터)
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody ChatRoomDto chatRoomDto, @RequestParam String userId) {

        if (!userId.equals(chatRoomDto.getParticipantId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        chatRoomRepository.save(chatRoomDto.toEntity());
        return new ResponseEntity<ChatRoomDto>(chatRoomDto, HttpStatus.CREATED);
    }

    // 모든 채팅방 데이터 조회
//    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> rooms() {
        List<ChatRoomDto> chatRoomDtoList = chatRoomRepository.findAll().stream()
                .map(ChatRoomDto::new)
                .collect(Collectors.toList());

        return new ResponseEntity<List<ChatRoomDto>>(chatRoomDtoList, HttpStatus.OK);
    }

    // 모든 채팅방 데이터 조회 (사용자ID)
    // user id가 채팅방의 Owner 이거나 Participant 인 경우, 해당 채팅방을 가져옴
    // TODO Spring security 도입 시, 로그인했을 때 사용할 수 있는 @AuthenticationPrincipal UserIdDto 로 수정
//    @GetMapping("/rooms")
//    public ResponseEntity<List<ChatRoomDto>> getRooms(@RequestBody UserIdDto userIdDto) {
//        List<ChatRoomDto> chatRoomDtoList = chatRoomRepository
//                .findByOwnerIdOrParticipantId(userIdDto.getUserId(), userIdDto.getUserId()).stream()
//                .map(ChatRoomDto::new)
//                .collect(Collectors.toList());
//
//        return new ResponseEntity<List<ChatRoomDto>>(chatRoomDtoList, HttpStatus.OK);
//    }

    // 채팅방 조회 By userId (쿼리 파라미터)
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getRooms(@RequestParam String userId) {
        List<ChatRoomDto> chatRoomDtoList = chatRoomRepository
                .findByOwnerIdOrParticipantId(userId, userId).stream()
                .map(ChatRoomDto::new)
                .collect(Collectors.toList());

        return new ResponseEntity<List<ChatRoomDto>>(chatRoomDtoList, HttpStatus.OK);
    }

    // 특정 채팅방 데이터 조회
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomDto> room(@PathVariable String roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(NullPointerException::new);
        ChatRoomDto chatRoomDto = new ChatRoomDto(room);

        return new ResponseEntity<ChatRoomDto>(chatRoomDto, HttpStatus.OK);
    }

    // 특정 채팅방 전체 메시지 조회
//    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> messages(@PathVariable String roomId) {
        List<ChatMessageDto> chatMessageDtoList = chatMessageRepository.findByRoomId(roomId).stream()
                .map(ChatMessageDto::new)
                .collect(Collectors.toList());

        return new ResponseEntity<List<ChatMessageDto>>(chatMessageDtoList, HttpStatus.OK);
    }

    // 특정 채팅방 전체 메시지 조회 (인가 로직 추가)
//    @GetMapping("/rooms/{roomId}/messages")
//    public ResponseEntity<List<ChatMessageDto>> getMessages(@PathVariable String roomId, @RequestBody UserIdDto userIdDto) {
//        // 본인이 참가한 채팅방에 대해서만 메시지를 가져온다
//        ChatRoom room = chatRoomRepository.findById(roomId)
//                .orElseThrow(NullPointerException::new);
//        String userId = userIdDto.getUserId();
//
//        // 채팅방의 owner도 아니고, participant도 아닌 경우
//        if (!userId.equals(room.getParticipantId()) && !userId.equals(room.getOwnerId())) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//
//        List<ChatMessageDto> chatMessageDtoList = chatMessageRepository.findByRoomId(roomId).stream()
//                .map(ChatMessageDto::new)
//                .collect(Collectors.toList());
//
//        return new ResponseEntity<List<ChatMessageDto>>(chatMessageDtoList, HttpStatus.OK);
//    }

    // 특정 채팅방 전체 메시지 조회 (인가 로직 추가)
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getMessages(@PathVariable String roomId, @RequestParam String userId) {
        // 본인이 참가한 채팅방에 대해서만 메시지를 가져온다
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(NullPointerException::new);

        // 채팅방의 owner도 아니고, participant도 아닌 경우
        if (!userId.equals(room.getParticipantId()) && !userId.equals(room.getOwnerId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<ChatMessageDto> chatMessageDtoList = chatMessageRepository.findByRoomId(roomId).stream()
                .map(ChatMessageDto::new)
                .collect(Collectors.toList());

        return new ResponseEntity<List<ChatMessageDto>>(chatMessageDtoList, HttpStatus.OK);
    }
}

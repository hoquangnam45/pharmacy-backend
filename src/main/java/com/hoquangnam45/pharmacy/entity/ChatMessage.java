//package com.hoquangnam45.pharmacy.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.Table;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.OffsetDateTime;
//import java.util.UUID;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@Table(name = "chat_message", schema = "public")
//public class ChatMessage {
//    @Id
//    @GeneratedValue
//    private UUID id;
//    private String content;
//    private User user;
//
//    private OffsetDateTime createdAt;
//    private OffsetDateTime updatedAt;
//    private OffsetDateTime deletedAt;
//
//    @Column(name = "chat_room_id")
//    private UUID chatRoomId;
//
//    @JoinColumn(name = "chat_room_id", referencedColumnName = "id", insertable = false, updatable = false)
//    private ChatRoom chatRoom;
//}

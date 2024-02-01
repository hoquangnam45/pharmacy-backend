//package com.hoquangnam45.pharmacy.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.JoinTable;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//@Entity
//@NoArgsConstructor
//@Getter
//@Setter
//@Table(name = "chat_room", schema = "public")
//public class ChatRoom {
//    @Id
//    @GeneratedValue
//    private UUID id;
//
//    @Column(name = "user_id")
//    private UUID userId;
//
//    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
//    private User user;
//
//    @JoinTable(
//            name = "chat_room_participant",
//            schema = "public",
//            joinColumns = @JoinColumn(name = "room_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> participants;
//
//    @OneToMany(mappedBy = "chatRoom")
//    private List<ChatMessage> messages;
//}

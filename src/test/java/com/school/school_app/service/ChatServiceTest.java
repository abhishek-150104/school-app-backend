package com.school.school_app.service;

import com.school.school_app.dto.request.CreateChannelRequest;
import com.school.school_app.dto.request.SendMessageRequest;
import com.school.school_app.dto.response.ChatChannelResponse;
import com.school.school_app.dto.response.ChatMessageResponse;
import com.school.school_app.entity.ChatChannel;
import com.school.school_app.entity.ChatMessage;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.ChatChannelRepository;
import com.school.school_app.repository.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock ChatChannelRepository channelRepo;
    @Mock ChatMessageRepository messageRepo;

    @InjectMocks ChatService chatService;

    private static final String SCHOOL_ID = "school1";

    @Test
    void createChannel_success() {
        CreateChannelRequest req = new CreateChannelRequest();
        req.setName("General");
        req.setType("GROUP");
        req.setMemberIds(new ArrayList<>(List.of("u2")));
        req.setMemberNames(new ArrayList<>(List.of("User2")));

        ChatChannel saved = ChatChannel.builder().id("c1").name("General").type("GROUP").build();
        when(channelRepo.save(any())).thenReturn(saved);

        ChatChannelResponse result = chatService.createChannel(SCHOOL_ID, req, "u1", "User1");

        assertThat(result.getId()).isEqualTo("c1");
        verify(channelRepo).save(argThat(ch -> ch.getMembers().contains("u1")));
    }

    @Test
    void createChannel_creatorAddedToMembers() {
        CreateChannelRequest req = new CreateChannelRequest();
        req.setName("Team");
        req.setType("GROUP");
        req.setMemberIds(new ArrayList<>(List.of("u2")));
        req.setMemberNames(new ArrayList<>(List.of("User2")));

        when(channelRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        chatService.createChannel(SCHOOL_ID, req, "u1", "Admin");

        verify(channelRepo).save(argThat(ch ->
                ch.getMembers().contains("u1") && ch.getMembers().contains("u2")));
    }

    @Test
    void getMyChannels_returnsList() {
        ChatChannel ch = ChatChannel.builder().id("c1").schoolId(SCHOOL_ID).build();
        when(channelRepo.findBySchoolIdAndMembersContaining(SCHOOL_ID, "u1")).thenReturn(List.of(ch));

        List<ChatChannelResponse> result = chatService.getMyChannels(SCHOOL_ID, "u1");

        assertThat(result).hasSize(1);
    }

    @Test
    void sendMessage_success() {
        SendMessageRequest req = new SendMessageRequest();
        req.setChannelId("c1");
        req.setContent("Hello!");
        req.setType("TEXT");

        ChatChannel ch = ChatChannel.builder().id("c1").schoolId(SCHOOL_ID)
                .members(new ArrayList<>(List.of("u1", "u2"))).build();
        ChatMessage saved = ChatMessage.builder().id("m1").content("Hello!").channelId("c1").build();

        when(channelRepo.findById("c1")).thenReturn(Optional.of(ch));
        when(messageRepo.save(any())).thenReturn(saved);

        ChatMessageResponse result = chatService.sendMessage(SCHOOL_ID, req, "u1", "User1", "TEACHER");

        assertThat(result.getId()).isEqualTo("m1");
        assertThat(result.getContent()).isEqualTo("Hello!");
    }

    @Test
    void sendMessage_nonMemberThrows() {
        SendMessageRequest req = new SendMessageRequest();
        req.setChannelId("c1");
        req.setContent("Hi");

        ChatChannel ch = ChatChannel.builder().id("c1").schoolId(SCHOOL_ID)
                .members(new ArrayList<>(List.of("u2"))).build();
        when(channelRepo.findById("c1")).thenReturn(Optional.of(ch));

        assertThatThrownBy(() -> chatService.sendMessage(SCHOOL_ID, req, "u1", "User1", "STUDENT"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Not a member");
    }

    @Test
    void sendMessage_channelNotFoundThrows() {
        SendMessageRequest req = new SendMessageRequest();
        req.setChannelId("bad");
        when(channelRepo.findById("bad")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.sendMessage(SCHOOL_ID, req, "u1", "User1", "ADMIN"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Channel not found");
    }

    @Test
    void getChannelMessages_success() {
        ChatChannel ch = ChatChannel.builder().id("c1").schoolId(SCHOOL_ID)
                .members(new ArrayList<>(List.of("u1"))).build();
        ChatMessage msg = ChatMessage.builder().id("m1").channelId("c1").content("Hi").build();

        when(channelRepo.findById("c1")).thenReturn(Optional.of(ch));
        when(messageRepo.findByChannelIdOrderByCreatedAtAsc("c1")).thenReturn(List.of(msg));

        List<ChatMessageResponse> result = chatService.getChannelMessages(SCHOOL_ID, "c1", "u1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Hi");
    }

    @Test
    void getChannelMessages_nonMemberThrows() {
        ChatChannel ch = ChatChannel.builder().id("c1").schoolId(SCHOOL_ID)
                .members(new ArrayList<>(List.of("u2"))).build();
        when(channelRepo.findById("c1")).thenReturn(Optional.of(ch));

        assertThatThrownBy(() -> chatService.getChannelMessages(SCHOOL_ID, "c1", "u1"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Not a member");
    }

    @Test
    void getGroupChannels_returnsList() {
        ChatChannel ch = ChatChannel.builder().id("c1").type("GROUP").schoolId(SCHOOL_ID).build();
        when(channelRepo.findBySchoolIdAndType(SCHOOL_ID, "GROUP")).thenReturn(List.of(ch));

        List<ChatChannelResponse> result = chatService.getGroupChannels(SCHOOL_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("c1");
    }
}

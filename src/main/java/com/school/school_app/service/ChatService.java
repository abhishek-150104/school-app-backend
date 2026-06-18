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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatChannelRepository channelRepo;
    private final ChatMessageRepository messageRepo;

    public ChatChannelResponse createChannel(String schoolId, CreateChannelRequest req,
                                             String createdById, String createdByName) {
        List<String> members = new ArrayList<>(req.getMemberIds());
        List<String> memberNames = new ArrayList<>(req.getMemberNames());

        if (!members.contains(createdById)) {
            members.add(createdById);
            memberNames.add(createdByName);
        }

        ChatChannel channel = ChatChannel.builder()
                .schoolId(schoolId)
                .name(req.getName())
                .type(req.getType())
                .members(members)
                .memberNames(memberNames)
                .createdById(createdById)
                .createdByName(createdByName)
                .build();

        return ChatChannelResponse.from(channelRepo.save(channel));
    }

    public List<ChatChannelResponse> getMyChannels(String schoolId, String userId) {
        return channelRepo.findBySchoolIdAndMembersContaining(schoolId, userId)
                .stream().map(ChatChannelResponse::from).toList();
    }

    public ChatMessageResponse sendMessage(String schoolId, SendMessageRequest req,
                                           String senderId, String senderName, String senderRole) {
        ChatChannel channel = channelRepo.findById(req.getChannelId())
                .filter(c -> c.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new AppException("Channel not found", HttpStatus.NOT_FOUND));

        if (!channel.getMembers().contains(senderId)) {
            throw new AppException("Not a member of this channel", HttpStatus.FORBIDDEN);
        }

        ChatMessage message = ChatMessage.builder()
                .channelId(req.getChannelId())
                .schoolId(schoolId)
                .senderId(senderId)
                .senderName(senderName)
                .senderRole(senderRole)
                .content(req.getContent())
                .type(req.getType() != null ? req.getType() : "TEXT")
                .build();

        return ChatMessageResponse.from(messageRepo.save(message));
    }

    public List<ChatMessageResponse> getChannelMessages(String schoolId, String channelId, String userId) {
        ChatChannel channel = channelRepo.findById(channelId)
                .filter(c -> c.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new AppException("Channel not found", HttpStatus.NOT_FOUND));

        if (!channel.getMembers().contains(userId)) {
            throw new AppException("Not a member of this channel", HttpStatus.FORBIDDEN);
        }

        return messageRepo.findByChannelIdOrderByCreatedAtAsc(channelId)
                .stream().map(ChatMessageResponse::from).toList();
    }

    public List<ChatChannelResponse> getGroupChannels(String schoolId) {
        return channelRepo.findBySchoolIdAndType(schoolId, "GROUP")
                .stream().map(ChatChannelResponse::from).toList();
    }
}

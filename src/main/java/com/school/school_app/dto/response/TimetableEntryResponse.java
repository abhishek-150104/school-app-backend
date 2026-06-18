package com.school.school_app.dto.response;

import com.school.school_app.entity.TimetableEntry;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimetableEntryResponse {
    private String id;
    private String sectionId;
    private String sectionName;
    private String classRoomId;
    private String classRoomName;
    private String subjectId;
    private String subjectName;
    private String teacherId;
    private String teacherName;
    private String dayOfWeek;
    private int periodNumber;
    private String startTime;
    private String endTime;

    public static TimetableEntryResponse from(TimetableEntry e) {
        return TimetableEntryResponse.builder()
                .id(e.getId())
                .sectionId(e.getSectionId()).sectionName(e.getSectionName())
                .classRoomId(e.getClassRoomId()).classRoomName(e.getClassRoomName())
                .subjectId(e.getSubjectId()).subjectName(e.getSubjectName())
                .teacherId(e.getTeacherId()).teacherName(e.getTeacherName())
                .dayOfWeek(e.getDayOfWeek())
                .periodNumber(e.getPeriodNumber())
                .startTime(e.getStartTime()).endTime(e.getEndTime())
                .build();
    }
}

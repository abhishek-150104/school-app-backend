package com.school.school_app.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CreateChannelRequest {
    private String name;
    private String type; // GROUP or DIRECT
    private List<String> memberIds;
    private List<String> memberNames;
}

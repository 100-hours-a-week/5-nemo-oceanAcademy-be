package com.nemo.oceanAcademy.domain.chat.dataAccess.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection =  "chatting_content")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    private String id;
    @JsonProperty("roomId")
    private Long roomId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("writer")
    private String writer;

    @JsonProperty("profile_image_path")
    private String profileImagePath;

    @JsonProperty("createdDate")
    private Date createdDate;

    public Chat(Long roomId, String content, String writer, String profileImagePath, Date date) {
        this.roomId = roomId;
        this.content = content;
        this.writer = writer;
        this.profileImagePath = profileImagePath;
        this.createdDate = date;
    }

}

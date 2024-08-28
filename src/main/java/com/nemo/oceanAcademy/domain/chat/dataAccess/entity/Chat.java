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
    private ObjectId id;
    @JsonProperty("roomId")
    private Long roomId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("writerId")
    private Long writerId;

    @JsonProperty("createdDate")
    private Date createdDate;

    public Chat(Long roomId, String content, Long writerId, Date date) {
        this.roomId = roomId;
        this.content = content;
        this.writerId = writerId;
        this.createdDate = date;
    }

}
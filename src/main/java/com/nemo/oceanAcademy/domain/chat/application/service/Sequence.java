package com.nemo.oceanAcademy.domain.chat.application.service;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "database_sequences")
public class Sequence {

    @Id
    private String id;
    private long seq;

}

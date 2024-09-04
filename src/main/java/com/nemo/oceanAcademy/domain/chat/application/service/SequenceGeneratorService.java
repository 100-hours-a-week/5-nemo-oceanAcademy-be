package com.nemo.oceanAcademy.domain.chat.application.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class SequenceGeneratorService {

    @Autowired
    private MongoOperations mongoOperations;

    public long generateSequence(String seqName) {
        Sequence counter = mongoOperations.findAndModify(
                Query.query(where("_id").is(seqName)),
                new Update().inc("seq", 1),
                options().returnNew(true).upsert(true),
                Sequence.class);
        return counter != null ? counter.getSeq() : 1;
    }

    public String generateChatId(Long roomId) {         // 날짜+방번호+id 시퀀스 생성

        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());  // 현재 날짜를 yyyyMMdd 형식으로 변환
        String sequenceName = date + "_" + roomId;                          // 날짜와 방 번호를 조합한 시퀀스 이름 생성
        long sequenceCount = generateSequence(sequenceName);                // 해당 날짜와 방 번호에 맞는 시퀀스 값을 증가시킴
        String formattedSeq = String.format("%016d", sequenceCount);        // 시퀀스를 16자리로 포맷 (예: 001, 002, ...)
        return date + "_" + roomId + "_" + formattedSeq;                    // 최종 ID 생성 (날짜_방번호_시퀀스)
    }
}

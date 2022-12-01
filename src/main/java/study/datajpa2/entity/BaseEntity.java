package study.datajpa2.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {

    //등록일시
    @CreatedDate
    @Column(updatable = false) //수정이 안일어나게 (다른 데이터로 바뀔수있어서 false로 둠)
    private LocalDateTime createDate;

    //수정일시
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    //등록자
    @CreatedBy
    @Column(updatable = false) //수정이 안일어나게 (다른 데이터로 바뀔수있어서 false로 둠)
    private String createdBy;

    //수정자
    @LastModifiedBy
    private String lastModifiedBy;
}

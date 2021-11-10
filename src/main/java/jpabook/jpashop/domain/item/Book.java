package jpabook.jpashop.domain.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") // DiscriminatorColumn 와 세트
@Getter @Setter
public class Book extends Item {

    private String anthor;
    private String isbn;

}

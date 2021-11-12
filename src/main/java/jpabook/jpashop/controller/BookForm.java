package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

// 웹 계층(Controller 등)에서만 사용하기 위해
@Getter @Setter
public class BookForm {

    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;
}

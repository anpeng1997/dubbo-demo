package cn.pengan.service;

import cn.pengan.pojo.Book;

import java.util.List;

public interface IBookService {
    List<Book> findAllBooks();
}

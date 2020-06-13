package cn.pengan.controller;

import cn.pengan.pojo.Book;
import cn.pengan.service.IBookService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Reference(version = "${master.sever.version}")
    private IBookService bookService;

    @GetMapping("")
    public List<Book> findAll() {
        List<Book> allBooks = bookService.findAllBooks();
        return allBooks;
    }
}

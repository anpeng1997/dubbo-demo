package cn.pengan.service.impl;

import cn.pengan.pojo.Book;
import cn.pengan.service.IBookService;
import com.alibaba.dubbo.config.annotation.Service;

import java.util.Arrays;
import java.util.List;

@Service(version = "${master.sever.version}")
public class BookServiceImpl implements IBookService {


    @Override
    public List<Book> findAllBooks() {
        return  Arrays.asList(new Book(1, "红楼梦"), new Book(2, "西游记"));
    }
}

package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * @author 言曌
 * @date 2019-05-06 14:53
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class CommentRepositoryTest {


    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void test() {
        User user = userRepository.findOne(113);
        Integer result = 0;
        try {
            result = commentRepository.deleteByUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }
}
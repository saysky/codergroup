package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.Job;
import com.liuyanzhao.forum.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired
    private JobRepository jobRepository;

    @Test
    public void test() {
        Job job = new Job();
//        job.setId(12);
        job.setName("UI设计师");
        Example<Job> example = Example.of(job);
        System.out.println(example);
        List<Job> jobList = jobRepository.findAll(example);
        System.out.println(jobList);
    }
}
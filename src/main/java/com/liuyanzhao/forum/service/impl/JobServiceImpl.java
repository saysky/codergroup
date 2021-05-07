package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Job;
import com.liuyanzhao.forum.repository.JobRepository;
import com.liuyanzhao.forum.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/4/5 上午10:04
 */

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;


    @Override
    public List<Job> listJobs() {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return jobRepository.findAll(sort);
    }

    @Override
    public void saveJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public void removeJob(Integer id) {
        jobRepository.delete(id);
    }

    @Override
    public Job findJobById(Integer id) {
        return jobRepository.findOne(id);
    }
}

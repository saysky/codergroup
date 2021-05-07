package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Job;

import java.util.List;

/**
 * 职业Service
 * @author 言曌
 * @date 2018/4/5 上午10:01
 */

public interface JobService {

    /**
     * 列出所有的职业
     * @return
     */
    List<Job> listJobs();

    /**
     * 添加/更新职业
     * @param job
     */
    void saveJob(Job job);

    /**
     * 删除职业
     * @param id
     */
    void removeJob(Integer id);

    /**
     * 根据id获得职业
     * @param id
     * @return
     */
    Job findJobById(Integer id);
}

package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/5/24 下午10:46
 */

public interface TagService {

    /**
     * 添加或更新标签
     * @param tag
     * @return
     */
    Tag saveTag(Tag tag);


    /**
     * 根据Id获得标签
     * @param id
     * @return
     */
    Tag getTagById(Integer id);


    /**
     * 删除标签
     * @CacheEvict 应用到删除数据的方法上，调用方法时会从缓存中删除对应key的数据
     * @param id
     */
    Boolean removeTag(Integer id);



    /**
     * 获得标签列表
     * @return
     */
    Page<Tag> listTags(Pageable pageable);



    /**
     *根据标签名获取标签
     * @param name
     * @return
     */
    Tag getTagByName(String name);

    /**
     * 获得标签列表，不分页
     * @return
     */
    List<Tag> listTags();



}

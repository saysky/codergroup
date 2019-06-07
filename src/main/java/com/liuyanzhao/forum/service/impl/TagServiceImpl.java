package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Tag;
import com.liuyanzhao.forum.repository.TagRepository;
import com.liuyanzhao.forum.service.TagService;
import com.liuyanzhao.forum.util.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/5/24 下午10:46
 */
@Service
@CacheConfig(cacheNames = "tags")
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private RedisOperator redis;

    @Transactional
    @Override
    //执行下面的方法，最终将结果添加(如果这个key存在，则覆盖)到Redis中
    @CachePut(value = "7d", key = "'tags:'+#p0.id", unless = "#tag eq null")
    public Tag saveTag(Tag tag) {
        redis.del("tags:list");
        return tagRepository.save(tag);
    }

    @Override
    //如果Redis数据库中没有这个key，执行下面方法，最终结果添加到Redis。如果key已存在，则直接从Redis获取，不执行下面方法
    @Cacheable(value = "7d", key = "'tags:'+#id.toString()")
    public Tag getTagById(Integer id) {
        return tagRepository.findOne(id);
    }

    @Override
//    condition为true执行@CacheEvict，将该key从Redis删除
    @CacheEvict(value = "7d", key = "'tags:'+#id.toString()", condition = "#result eq true")
    public Boolean removeTag(Integer id) {
        tagRepository.delete(id);
        return true;
    }

    @Override
    @Cacheable(value = "7d", key = "'tags:hotTagist'")
    public List<Tag> listTags() {
        return tagRepository.findAll();
    }

    @Override
    public Page<Tag> listTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }


}

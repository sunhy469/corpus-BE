package com.sunhy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sunhy.entity.Words;
import com.sunhy.mapper.WordsMapper;
import com.sunhy.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

@Service
public class WordsServiceImpl extends ServiceImpl<WordsMapper, Words> implements WordsService  {

    private final WordsMapper wordsMapper;

    public WordsServiceImpl(WordsMapper wordsMapper) {
        this.wordsMapper = wordsMapper;
    }

    @Override
    public List<Words> getList(LambdaQueryWrapper<Words> wrapper) {
        return wordsMapper.selectList(wrapper);
    }
}

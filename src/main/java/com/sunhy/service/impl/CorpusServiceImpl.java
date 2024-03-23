package com.sunhy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunhy.entity.Corpus;
import com.sunhy.mapper.CorpusMapper;
import com.sunhy.service.CorpusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorpusServiceImpl extends ServiceImpl<CorpusMapper, Corpus> implements CorpusService {

    private final CorpusMapper corpusMapper;

    @Override
    public List<String> getAllEnglish() {
        LambdaQueryWrapper<Corpus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Corpus::getTopic,"农药名称");
        List<Corpus> corpusList = this.getList(wrapper);
        List<String> strings = corpusList.stream()
                .map(Corpus::getEnglish)
                .collect(Collectors.toCollection(ArrayList::new));
        return strings;
    }

    public CorpusServiceImpl(CorpusMapper corpusMapper) {
        this.corpusMapper = corpusMapper;
    }

    @Override
    public List<Corpus> getList(LambdaQueryWrapper<Corpus> wrapper) {
        return corpusMapper.selectList(wrapper);
    }
}

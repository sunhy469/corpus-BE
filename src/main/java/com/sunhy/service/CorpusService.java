package com.sunhy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sunhy.entity.Corpus;
import com.sunhy.entity.Words;

import java.util.List;

public interface CorpusService extends IService<Corpus> {

    List<Corpus> getList(LambdaQueryWrapper<Corpus> wrapper);

    List<String> getAllEnglish();
}

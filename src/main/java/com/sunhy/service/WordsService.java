package com.sunhy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sunhy.entity.Words;

import java.util.List;

public interface WordsService extends IService<Words> {

    List<Words> getList(LambdaQueryWrapper<Words> wrapper);
}

package com.sunhy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunhy.entity.Corpus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CorpusMapper extends BaseMapper<Corpus> {

}

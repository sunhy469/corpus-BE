package com.sunhy.dto;

import com.sunhy.entity.Corpus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ReturnDto extends Corpus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer frequency;
}

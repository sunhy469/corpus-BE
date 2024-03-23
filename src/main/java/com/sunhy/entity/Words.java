package com.sunhy.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Words implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String english;

    private Integer frequency;

    private String chinese;
}

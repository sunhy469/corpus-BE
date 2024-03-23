package com.sunhy.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Corpus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String chinese;

    private String english;

    private String abbreviation;

    private String topic;

    private String classify;
}

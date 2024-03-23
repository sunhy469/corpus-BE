package com.sunhy.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

@Data
public class ReceiveDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String type;

    private String item;

    private ArrayList<String> checkedList;
}

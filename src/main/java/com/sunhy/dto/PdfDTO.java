package com.sunhy.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PdfDTO  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private byte[] pdfBytes;
}

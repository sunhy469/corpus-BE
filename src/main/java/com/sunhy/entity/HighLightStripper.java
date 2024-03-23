package com.sunhy.entity;

import lombok.Data;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationHighlight;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
public class HighLightStripper extends PDFTextStripper {

    Set<String> keyWords;

    public HighLightStripper(Collection<String> keyWords) {
        super();
        this.keyWords = Set.copyOf(keyWords);
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        if (keyWords.contains(string.toLowerCase())) {
            float posXInit = textPositions.get(0).getXDirAdj();
            float posXEnd = textPositions.get(textPositions.size() - 1).getXDirAdj()
                    + textPositions.get(textPositions.size() - 1).getWidth();
            float posYInit = textPositions.get(0).getPageHeight() - textPositions.get(0).getYDirAdj();
            float posYEnd = textPositions.get(0).getPageHeight() - textPositions.get(textPositions.size() - 1).getYDirAdj();
            float height = textPositions.get(0).getHeightDir();

            PDRectangle position = new PDRectangle();
            position.setLowerLeftX(posXInit);
            position.setLowerLeftY(posYEnd);
            position.setUpperRightX(posXEnd);
            position.setUpperRightY(posYEnd + height);

            PDAnnotationTextMarkup markup = new PDAnnotationHighlight();
            markup.setRectangle(position);

            float[] quadPoints = {posXInit, posYEnd + height + 2,
                    posXEnd, posYEnd + height + 2,
                    posXInit, posYInit - 2,
                    posXEnd, posYEnd - 2};
            markup.setQuadPoints(quadPoints);
            PDColor color = new PDColor(new float[]{1, 1 / 255F, 1}, PDDeviceRGB.INSTANCE);
            markup.setColor(color);

            List<PDAnnotation> annotationsInPage = document.getPage(this.getCurrentPageNo() - 1).getAnnotations();
            annotationsInPage.add(markup);
        }
    }

    @Override
    protected void processTextPosition(TextPosition text){
        String character = text.getUnicode();
        if (character != null && !character.trim().isEmpty())
            super.processTextPosition(text);
    }

}

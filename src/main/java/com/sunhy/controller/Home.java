package com.sunhy.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sunhy.entity.HighLightStripper;
import com.sunhy.dto.PdfDTO;
import com.sunhy.dto.ReceiveDto;
import com.sunhy.dto.ReturnDto;
import com.sunhy.entity.Corpus;
import com.sunhy.entity.Words;
import com.sunhy.service.CorpusService;
import com.sunhy.service.WordsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/home")
@Slf4j
public class Home {

    private final WordsService wordsService;

    private final CorpusService corpusService;

    // 所有英文关键词
    private final ArrayList<String> keyWords = new ArrayList<>();

    private boolean keywordsLoaded = false;

    public Home(WordsService wordsService, CorpusService corpusService) {
        this.wordsService = wordsService;
        this.corpusService = corpusService;
        // 在构造函数中加载关键字
        loadKeyWords();
    }

    @PostMapping("/search")
    public ArrayList<ReturnDto> search(@RequestBody ReceiveDto receiveDto) {
        log.info("搜索");
        String type = receiveDto.getType();
        String item = receiveDto.getItem();
        ArrayList<String> checkedList = receiveDto.getCheckedList();

        if (item.isEmpty()) {
            return null;
        }

        ArrayList<ReturnDto> list = new ArrayList<>();
        if (type.equals("与所有词匹配")) {
            LambdaQueryWrapper<Words> wordsWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<Corpus> corpusWrapper = new LambdaQueryWrapper<>();

            if (containsChinese(item)) {
//                wordsWrapper.like(Words::getChinese, "%" + item + "%");
//                corpusWrapper.like(Corpus::getChinese, "%" + item + "%");
                wordsWrapper.apply("MATCH(chinese) AGAINST({0})", item);
                corpusWrapper.apply("MATCH(chinese) AGAINST({0})", item);
            } else {
                wordsWrapper.apply("MATCH(english) AGAINST({0})", item);
                corpusWrapper.apply("MATCH(english) AGAINST({0})", item);
            }

            if(checkedList.isEmpty()){
                return null;
            } else {
                confirmChecked(corpusWrapper, checkedList);
            }

            List<Words> wordsList = wordsService.getList(wordsWrapper);
            List<Corpus> corpusList = corpusService.getList(corpusWrapper);

            for (Words aWord : wordsList) {
                ReturnDto returnDto = new ReturnDto();
                BeanUtils.copyProperties(aWord, returnDto);
                list.add(returnDto);
            }

            for (Corpus aCorpus : corpusList) {
                ReturnDto returnDto = new ReturnDto();
                BeanUtils.copyProperties(aCorpus, returnDto);
                list.add(returnDto);
            }
        } else if (type.equals("完全匹配")) {
            LambdaQueryWrapper<Words> wordsWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<Corpus> corpusWrapper = new LambdaQueryWrapper<>();

            if (containsChinese(item)) {
                wordsWrapper.eq(Words::getChinese, item);
                corpusWrapper.eq(Corpus::getChinese, item);
            } else {
                wordsWrapper.eq(Words::getEnglish, item);
                corpusWrapper.eq(Corpus::getEnglish, item);
            }

            if(checkedList.isEmpty()){
                return null;
            } else {
                confirmChecked(corpusWrapper, checkedList);
            }

            List<Words> wordsList = wordsService.getList(wordsWrapper);
            List<Corpus> corpusList = corpusService.getList(corpusWrapper);

            for (Words aWord : wordsList) {
                ReturnDto returnDto = new ReturnDto();
                BeanUtils.copyProperties(aWord, returnDto);
                list.add(returnDto);
            }

            for (Corpus aCorpus : corpusList) {
                ReturnDto returnDto = new ReturnDto();
                BeanUtils.copyProperties(aCorpus, returnDto);
                list.add(returnDto);
            }
        }
        return list;
    }

    @PostMapping("/pdf")
    public String pdf(@RequestBody PdfDTO pdfDTO) {
        long start = System.currentTimeMillis();
        // 创建临时文件路径
        String tempFileName = UUID.randomUUID() + ".pdf";
        String newFilePath = "D:\\cropus\\giahs\\giahs_app\\public\\pdf_highlighted.pdf";

        try {

            // 将字节数组写入临时文件
            Path tempFile = Paths.get(tempFileName);
            Files.write(tempFile, pdfDTO.getPdfBytes());

            PDFParser parser = new PDFParser(new RandomAccessReadBufferedFile(tempFileName));
            // 对PDF文件进行解析
            PDDocument document = parser.parse();
            //关键词必须小写
            PDFTextStripper stripper = new HighLightStripper(keyWords);
            stripper.setSortByPosition(true);
            stripper.setStartPage(0);
            stripper.setEndPage(document.getNumberOfPages());
            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            stripper.writeText(document, dummy);
            document.save(newFilePath);
            document.close();

            long end = System.currentTimeMillis();
            System.out.println("-------------完成用时:" + (end - start));

            return newFilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * 检测是否含中文字符
     *
     * @param str
     * @return
     */
    private static boolean containsChinese(String str) {
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    private static void confirmChecked(LambdaQueryWrapper<Corpus> corpusWrapper, ArrayList<String> checkedList) {
        corpusWrapper.and(wrapper1 -> {
            for (String checkedItem : checkedList) {
                wrapper1.or(w -> {
                    if (checkedItem.equals("国际机构")) {
                        w.eq(Corpus::getTopic, "农业领域常见国际机构名称及缩写");
                    } else if (checkedItem.equals("茶业")) {
                        w.eq(Corpus::getClassify, "一般性词汇");
                    } else if (checkedItem.equals("茶叶加工")) {
                        w.eq(Corpus::getClassify, "茶叶加工");
                    } else if (checkedItem.equals("农药登记")) {
                        w.eq(Corpus::getClassify, "农药登记");
                    } else if (checkedItem.equals("农药名称")) {
                        w.eq(Corpus::getTopic, "农药名称");
                    } else if (checkedItem.equals("农药剂型")) {
                        w.eq(Corpus::getClassify, "农药剂型");
                    } else if (checkedItem.equals("农药构成")) {
                        w.eq(Corpus::getClassify, "农药构成");
                    } else if (checkedItem.equals("茶树栽培")) {
                        w.eq(Corpus::getClassify, "茶树栽培");
                    } else if (checkedItem.equals("GIAHS官网词汇")) {
                        w.eq(Corpus::getTopic, "FAO GIAHS官网词汇");
                    } else if (checkedItem.equals("气候术语")) {
                        w.eq(Corpus::getTopic, "气候术语");
                    }
                });
            }
        });
    }

    private void loadKeyWords() {
        if (!keywordsLoaded) {
            List<String> allEnglish = corpusService.getAllEnglish();
            for (String word : allEnglish) {
                keyWords.add(word.toLowerCase());
            }
            keywordsLoaded = true;
        }
    }
}

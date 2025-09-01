package org.mbc.czo.function.Sample.controller;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.Sample.dto.SampleDTO;
import org.mbc.czo.function.Sample.service.SampleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/sample")
@Log4j2
@RequiredArgsConstructor // final을 붙인 필드로 생성자 만듬.

public class SampleController {
    // 컨트롤러는 url 생성과 프론트를 연결하는 부분으로 과거에 servlet-context.xml과 같은 역할을 함

    private final SampleService sampleService;

    @GetMapping("/sample0")  //  http://localhost:port/sample/sample0 -> void -> sample0.html
    public void sample0(Model model){

        log.info("SampleController.sample0메서드 실행.....");

        model.addAttribute("msg", "Attribute 입력값 출력 테스트");

    }

    @GetMapping("/sample1") //  http://localhost:port/sample/sample1  -> /resources/templates/sample/sample1.html
    public void sample1(Model model){
        // 리스트타입으로 데이터를 보내 보자.
        List<String> list = Arrays.asList("리스트1", "리스트2","리스트3","리스트4","리스트5");

        model.addAttribute("list",list);

    }

    @GetMapping("/sample2")  // 리턴타입이 void 임으로  /resources/templates/sample/sample2.html
    public String sample2(Model model){
        log.info("SampleController.sample2 메서드 실행....");

        var strList = sampleService.makeSampleList(1,10);

        var map = sampleService.makeSampleAccount();

        var sampleDTO = sampleService.makeSampleDTO();

        model.addAttribute("list",strList);
        model.addAttribute("map",map);
        model.addAttribute("sampleDTO",sampleDTO);

        return "sample/sample2";
    }

    @PostMapping(value = "/sample2")
    public String samplePost(SampleDTO sampleDto, Model model){

        log.info("SampleController.samplePost 메서드 실행....");
        log.info("SampleDTO.p1: " + sampleDto.getP1());
        log.info("SampleDTO.p2: " + sampleDto.getP2());
        log.info("SampleDTO.p3: " + sampleDto.getP3());



       return "sample/sample2";
    }

}

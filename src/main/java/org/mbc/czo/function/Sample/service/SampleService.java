package org.mbc.czo.function.Sample.service;

import org.mbc.czo.function.Sample.dto.SampleDTO;

import java.util.List;
import java.util.Map;

public interface SampleService {

    //Sample2 실행
    List<String> makeSampleList(int minVal, int maxVal);

    //Sample2 실행
    Map<String,String> makeSampleAccount();

    //Sample2 실행
    SampleDTO makeSampleDTO();

    void testSaveSampleDTO(SampleDTO sampleDTO);
}

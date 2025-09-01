package org.mbc.czo.function.Sample.service;

import lombok.RequiredArgsConstructor;
import org.mbc.czo.function.Sample.domain.Sample;
import org.mbc.czo.function.Sample.dto.SampleDTO;
import org.mbc.czo.function.Sample.repository.SampleJpaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService {

    private final SampleJpaRepository sampleJpaRepository;

    @Override
    public List<String> makeSampleList(int minVal, int maxVal) {

        List<String> strList =  IntStream.range(1,10) // 1~10까지 정수를 생성한다.
                .mapToObj(i -> "데이터"+i)
                .collect(Collectors.toList());

        return strList;  // 리스트에 정수(숫자)문자열이 생성된다.
        // [데이터1, 데이터2 ...... 데이터9]
    }

    @Override
    public Map<String, String> makeSampleAccount() {

        Map<String, String> map = new HashMap<>();
        map.put("id","user");
        map.put("pw","1234"); // key , value

        return map;
    }

    @Override
    public SampleDTO makeSampleDTO() {

        // 최종적으로 객체 3개 완성 됨.
        SampleDTO sampleDTO = SampleDTO.builder()
                .p1("값....  p1")
                .p2("값....  p2")
                .p3("값....  p3")
                .build();

        return sampleDTO;
    }

    @Override
    public void testSaveSampleDTO(SampleDTO sampleDTO) {

        ModelMapper modelMapper = new ModelMapper();
        var sampleMapper = modelMapper.map(sampleDTO, Sample.class);

        //JpaRepository 이용한 데이터 저장 샘플
        var testSave = sampleJpaRepository.save(sampleMapper);
    }
}

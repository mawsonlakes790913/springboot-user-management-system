package com.example.demo.hello;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SampleService {

    @Autowired
    private SampleRepository sampleRepository;

    public Sample getSample(String id) {
        // 検索
        Optional<Sample> optionalSample = sampleRepository.findById(id);
        // OptionalからSampleを取得
        Sample sample = optionalSample.get();
        return sample;
    }
}

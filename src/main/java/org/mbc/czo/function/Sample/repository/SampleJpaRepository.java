package org.mbc.czo.function.Sample.repository;

import org.mbc.czo.function.Sample.domain.Sample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleJpaRepository extends JpaRepository<Sample, String>{
}

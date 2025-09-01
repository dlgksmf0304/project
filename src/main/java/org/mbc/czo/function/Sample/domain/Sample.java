package org.mbc.czo.function.Sample.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sample_tbl")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Sample {

    @Id
    @Column(name = "sample_p1")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardIndex;

    @Column(name = "sample_p2",
            length = 500)
    private String title;

    @Column(name = "sample_p3",
            length = 2000)
    private String content;
}

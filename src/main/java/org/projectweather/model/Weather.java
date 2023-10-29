package org.projectweather.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Weather {

    private long regionId;
    private String regionName;
    private int temperature;
    private LocalDateTime localDateTime;

}


package org.projectweather.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class WeatherDto {

    @NotNull
    private Long regionId;
    @NotBlank(message = "Должно быть указано название региона.")
    private String regionName;
    @NotNull
    @Max(value = 100, message = "Температура должна быть не больше 100")
    @Min(value = -100, message = "Температура должна быть не меньше -100")
    private Integer temperature;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime localDateTime;


}

package org.projectweather.model;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class WeatherDto {

    @NotNull
    private Long regionID;
    @NotBlank(message = "Должно быть указано название региона.")
    private String regionName;
    @NotNull
    @Max(value = 100, message = "Температура должна быть не больше 100")
    @Min(value = -100, message = "Температура должна быть не меньше -100")
    private Integer temperature;
    @NotNull
    private ZonedDateTime zonedDateTime;


}

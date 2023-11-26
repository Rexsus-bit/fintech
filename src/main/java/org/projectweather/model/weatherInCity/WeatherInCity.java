package org.projectweather.model.weatherInCity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@EqualsAndHashCode
@Table(name = "weather_in_city")
public class WeatherInCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_in_city_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    @ManyToOne
    @JoinColumn(name = "weather_id")
    private WeatherType weatherType;
    @Column(name = "weather_datetime")
    private Instant unixDateTime;
    @Column(name = "weather_temp")
    private Double temperature;

}

package org.projectweather;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;


@SpringBootApplication
@EnableScheduling
public class ProjectweatherApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectweatherApplication.class, args);
	}

	@Profile("!Test")
	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server inMemoryH2DatabaseServer() throws SQLException {
		return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9091");
	}

}

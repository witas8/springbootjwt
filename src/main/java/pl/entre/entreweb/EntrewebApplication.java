package pl.entre.entreweb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.entre.entreweb.model.*;
import pl.entre.entreweb.service.CompanyService;
import pl.entre.entreweb.service.UserService;

import java.util.ArrayList;

@SpringBootApplication
public class EntrewebApplication {

	//http://localhost:8080/entre/login , in params: username : xxx and password : yyy
	//ex: http://localhost:8080/entre/login?username=xxx&password=yyy
	//http://localhost:8080/entre/users , Get, Authorization, Bearer Token : Bearer access_token or refresh_token from login body
	public static void main(String[] args) {
		SpringApplication.run(EntrewebApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService, CompanyService companyService){
		return args -> {

			userService.saveRole(new Role(null, RoleType.PARTNER.name()));
			companyService.saveCompany(
					new Company(null, "Surf Burger", CompanyType.PUB.name(), "Gdańsk",
							"Garncarska 5", "80-205", "mariusz@surfburger.pl", "+48 123 456 789"));
			userService.saveUser(new User(null, "Piotr", "Nowak", "pnowak", "pnowak123",
					new ArrayList<>(), "p.nowak@gmail.com", "+48 534 456 211", new ArrayList<>()));

			userService.saveUser(new User(null, "Mikołaj", "Witkowski", "mwitkowski", "mwitkowski123",
					new ArrayList<>(), "m.witkowski@gmail.com", "+48 124 221 479", new ArrayList<>()));

			userService.addRoleToUser("pnowak", RoleType.PARTNER.name());
			userService.addCompanyToUser("pnowak", "Surf Burger");
		};
	}
}

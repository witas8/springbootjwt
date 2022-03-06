package pl.entre.entreweb.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.entre.entreweb.model.Company;
import pl.entre.entreweb.model.Role;
import pl.entre.entreweb.model.User;
import pl.entre.entreweb.service.CompanyService;
import pl.entre.entreweb.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/entre")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final CompanyService companyService;

    @GetMapping("/error")
    public String error(){
        return "Error. Please contact administrator.";
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/companies")
    private ResponseEntity<List<Company>> getCompanies(){
        return ResponseEntity.ok().body(companyService.getAllCompanies());
    }

    @PostMapping("user/save")
    private ResponseEntity<User> saveUser(@RequestBody User user){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("entre/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("company/save")
    private ResponseEntity<Company> saveCompany(@RequestBody Company company){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("entre/company/save").toUriString());
        return ResponseEntity.created(uri).body(companyService.saveCompany(company));
    }

    //void
    @PostMapping("/company/addToUser")
    public ResponseEntity<?> linkCompanyWithUser(@RequestBody CompanyToUserForm form){
        //void method has to be outside from a body
        userService.addCompanyToUser(form.getUsername(), form.getCompanyName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/role/addToUser")
    public ResponseEntity<?> linkRoleWithUser(@RequestBody RoleToUserForm form){
        userService.addRoleToUser(form.getUserName(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try{
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                com.auth0.jwt.algorithms.Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);
                //use the user to create access_token. Parts of code taken from CustomAuthenticationFilter:
                String accessToken = JWT.create()
                        .withSubject(user.getName())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) //10 minutes
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);//applcation/json
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch( Exception exception){
                log.error("Error logging in: {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                //or more detailed response in a body:
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE); //applcation/json
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else{
            throw new RuntimeException("Refresh token is missing");
        }
    }
}

@Data
class CompanyToUserForm{
    private String username;
    private String companyName;
}

@Data
class RoleToUserForm{
    private String userName;
    private String roleName;
}
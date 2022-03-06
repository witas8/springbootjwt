package pl.entre.entreweb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String companyName;
    private String companyType;
    private String city;
    private String address;
    private String postcode;
    private String email;
    private String phone;

}

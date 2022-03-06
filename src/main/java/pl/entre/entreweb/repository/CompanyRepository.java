package pl.entre.entreweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.entre.entreweb.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByCompanyName(String companyName);
}

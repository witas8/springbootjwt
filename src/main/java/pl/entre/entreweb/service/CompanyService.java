package pl.entre.entreweb.service;

import pl.entre.entreweb.model.Company;

import java.util.List;

public interface CompanyService {
    Company saveCompany(Company company);
    Company getCompany(String companyName);
    List<Company> getAllCompanies();
}

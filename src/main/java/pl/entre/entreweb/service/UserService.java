package pl.entre.entreweb.service;

import pl.entre.entreweb.model.Company;
import pl.entre.entreweb.model.Role;
import pl.entre.entreweb.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    User getUser(String username);
    List<User> getAllUsers();

    void addRoleToUser(String username, String roleName);
    void addCompanyToUser(String username, String companyName);
}

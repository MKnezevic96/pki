package service;

import model.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.AdministratorRepository;

@Service
public class AdministratorService {

    @Autowired
    private AdministratorRepository administratorRepository;


    public Administrator findByUsername(String username)
    {
        return administratorRepository.findByUsername(username);
    }

    public void save(Administrator administrator)
    {
        administratorRepository.save(administrator);
    }
}

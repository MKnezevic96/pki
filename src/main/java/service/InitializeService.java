package service;

import model.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import repository.AdministratorRepository;

import javax.annotation.PostConstruct;

@Component
public class InitializeService {

    @Autowired
    private AdministratorRepository administratorRepository;

    @PostConstruct
    public void init(){
        Administrator admin = new Administrator("admin","1234");
        administratorRepository.save(admin);
    }
}

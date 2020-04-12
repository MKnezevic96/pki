package repository;

import java.util.List;

import model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AdministratorRepository extends JpaRepository<Administrator,Long>{

    public Administrator findByUsername(String username);

}

package controller;

import dto.AdministratorDTO;
import model.Administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AdministratorService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AdministratorService administratorService;

    @PostMapping(value ="/login", consumes = "application/json")
    public ResponseEntity<HttpServletResponse> login(@RequestBody AdministratorDTO dto, HttpServletResponse response)
    {
        HttpHeaders header = new HttpHeaders();

        Administrator u = administratorService.findByUsername(dto.getUsername());

        if(u == null)
        {
            header.set("responseText","User doesn't exist!");
            return new ResponseEntity<>(header, HttpStatus.NOT_FOUND);
        }

        String password = dto.getPassword();

        try {

            if(password.equals(u.getPassword()))
            {
                response.addCookie(new Cookie("username",u.getUsername()));
                return new ResponseEntity<>(HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        header.set("Response","Password incorrect!");
        return new ResponseEntity<>(header,HttpStatus.NOT_FOUND);
    }


}

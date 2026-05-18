package LogIn;

import bean.ProfileBean;
import controller.LogInController;
import model.utente.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.AperturaFileTEST;


import static org.junit.jupiter.api.Assertions.assertEquals;

 class TestRegister {

    private LogInController logInController;

    @BeforeEach

    void setUp() {

        AperturaFileTEST.open();
        logInController = new LogInController();

    }


    @Test
    void test_first_register_is_admin(){

        ProfileBean  utenteDaRegistrare = new ProfileBean();

        utenteDaRegistrare.setUsername("juan");
        utenteDaRegistrare.setPassword("123");

        logInController.insert(utenteDaRegistrare);

        Utente registrato = logInController.researchUser(utenteDaRegistrare);

        assertEquals("ADMIN", registrato.getRuolo());

    }

    @Test
    void test_second_register_is_not_admin(){

        ProfileBean  utenteDaRegistrare = new ProfileBean();

        utenteDaRegistrare.setUsername("juan");
        utenteDaRegistrare.setPassword("123");

        logInController.insert(utenteDaRegistrare);

        utenteDaRegistrare.setUsername("joker");
        utenteDaRegistrare.setPassword("123");

        logInController.insert(utenteDaRegistrare);

        Utente registrato = logInController.researchUser(utenteDaRegistrare);

        assertEquals("USER", registrato.getRuolo());

    }













 }
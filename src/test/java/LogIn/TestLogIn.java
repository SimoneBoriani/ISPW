//package LogIn;
//
//import bean.ProfileBean;
//import controller.LogInController;
//import exceptions.IncorrectCredentialExeption;
//import model.utente.Utente;
//import org.junit.jupiter.api.*;
//import utils.AperturaFileTEST;
//import view.factory.ControllerFactory;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//
//class TestLogIn {
//
//    private LogInController logInController;
//
//    @BeforeEach
//    void setUp() {
//        AperturaFileTEST.open();
//        logInController = ControllerFactory.getGraphicalSingletonFactory().createLoginController();
//    }
//
//    @Test
//    @Order(1)
//    void test_first_registered_is_admin() {
//
//        ProfileBean utenteDaRegistrare = new ProfileBean();
//
//        utenteDaRegistrare.setUsername("juan");
//        utenteDaRegistrare.setPassword("123");
//
//        logInController.insert(utenteDaRegistrare);
//
//        Utente registrato = logInController.researchUser(utenteDaRegistrare);
//
//        assertEquals("ADMIN", registrato.getRuolo());
//
//    }
//
//    @Test
//    void test_next_registered_is_user() {
//
//        ProfileBean utenteDaRegistrare = new ProfileBean();
//        utenteDaRegistrare.setUsername("joker");
//        utenteDaRegistrare.setPassword("123");
//        logInController.insert(utenteDaRegistrare);
//
//        Utente registrato = logInController.researchUser(utenteDaRegistrare);
//
//        assertEquals("USER", registrato.getRuolo());
//    }
//
//    @Test
//    void register_username_null() {
//
//        ProfileBean loginBean = new ProfileBean();
//        loginBean.setUsername(null);
//        loginBean.setPassword("123");
//
//        Exception ex = assertThrows(IncorrectCredentialExeption.class, () -> {
//            logInController.insert(loginBean);
//        });
//
//        assertEquals("L'username non può essere nullo o vuoto.", ex.getMessage());
//    }
//
//    @Test
//    void register_password_null() {
//        ProfileBean daRegistrare = new ProfileBean();
//        daRegistrare.setUsername("mario");
//        daRegistrare.setPassword(null);
//
//        Exception ex = assertThrows(IncorrectCredentialExeption.class, () -> {
//            logInController.insert(daRegistrare);
//        });
//
//        assertEquals("La password non può essere nulla o vuota.", ex.getMessage());
//    }
//
//    @Test
//    void test_username_gia_presente() {
//
//        ProfileBean primoUtente = new ProfileBean();
//        primoUtente.setUsername("ben");
//        primoUtente.setPassword("123");
//        logInController.insert(primoUtente);
//
//        ProfileBean utenteDoppione = new ProfileBean();
//        utenteDoppione.setUsername("ben");
//        utenteDoppione.setPassword("abc");
//
//        Exception ex = assertThrows(IncorrectCredentialExeption.class, () -> {
//            logInController.insert(utenteDoppione);
//        });
//
//        assertEquals("Username già registrato.", ex.getMessage());
//    }
//
//    @Test
//    void incorrect_username_login() {
//        ProfileBean logging = new ProfileBean();
//        logging.setUsername("wrong");
//        logging.setPassword("123");
//
//        Exception ex = assertThrows(IncorrectCredentialExeption.class, () -> {
//            logInController.authenticate(logging);
//        });
//
//        assertEquals("Username o Password errati.", ex.getMessage());
//    }
//
//    @Test
//    void incorrect_password_login() {
//
//        ProfileBean mario = new ProfileBean();
//        mario.setUsername("mario");
//        mario.setPassword("password_giusta");
//        logInController.insert(mario);
//
//        ProfileBean logging = new ProfileBean();
//        logging.setUsername("mario");
//        logging.setPassword("wrong");
//
//        Exception ex = assertThrows(IncorrectCredentialExeption.class, () -> {
//            logInController.authenticate(logging);
//        });
//
//        assertEquals("Username o Password errati.", ex.getMessage());
//    }
//}
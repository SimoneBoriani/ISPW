package model.daofactory;

import exceptions.GenericSystemException;

import model.acquistoauto.dao.DaoAcquistoAuto;
import model.macchina.dao.DaoMacchina;
import model.utente.dao.DaoUtente;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public abstract class DaoFactory {


   public abstract DaoMacchina createMacchinaDao();
   public abstract DaoUtente createUtenteDao();
   public abstract DaoAcquistoAuto createAcquistoAutoDao();

   private static DaoFactory instance = null;

   protected DaoFactory(){

   }

   public static DaoFactory getDaoSingletonFactory() throws GenericSystemException {
        Properties prop = new Properties();

        try(FileInputStream config = new FileInputStream("src/main/resources/config.properties")){
            prop.load(config);
            String persistence = prop.getProperty("persistence");



            if(instance == null){
               if(persistence.equals("DEMO")){
                    instance = new DemoDaoFactory();
                }

                if(persistence.equals("JDBC")){
                    instance = new DbmsDaoFactory();
                }

        /*        if(persistence.equals("FILE")){
                    instance = new FileDaoFactory();
                }*/
            }

        } catch (FileNotFoundException e) {
            throw new GenericSystemException(e.getMessage());
        } catch (IOException e) {
            throw new GenericSystemException(e.getMessage());
        }
        return instance;
    }
}


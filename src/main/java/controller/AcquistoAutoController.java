package controller;

import bean.AcquistoAutoBean;
import model.daofactory.DaoFactory;

public class AcquistoAutoController {

    public void buyRequest(AcquistoAutoBean bean){
        if(findCar(bean)){

        }
    }

    public boolean findCar(AcquistoAutoBean bean){
        return DaoFactory.getDaoSingletonFactory().createAcquistoAutoDao().isCarFound(bean.getMacchina());
    }

    public boolean checkData(AcquistoAutoBean bean){

        return true;

    }
}

package controller;

import model.daofactory.DaoFactory;
import model.noleggioauto.NoleggioAuto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class VisualizzaStoricoController {

    public List<NoleggioAuto> findRented(){
        return DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().getRented();
    }

    public Map<LocalDate, Double> getProfittiPerData() {
        return DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().getProfittiPerData();
    }


}

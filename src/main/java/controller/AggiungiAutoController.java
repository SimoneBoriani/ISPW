package controller;

import bean.AggiungiAutoBean;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.io.IOException;

public class AggiungiAutoController {

    @FXML
    private TextField car_year;
    @FXML
    private TextField car_name;
    @FXML
    private TextField car_price;
    @FXML
    private TextField car_type;
    @FXML
    private TextField car_alimentation;
    @FXML
    private TextField car_brand;
    @FXML
    private TextField car_seat;
    @FXML
    private TextField car_owners;
    @FXML
    private TextField car_km;

    @FXML
    private void insert(ActionEvent event)throws IOException {

        String car_year_str = car_year.getText();
        String car_name_str = car_name.getText();
        String car_price_str = car_price.getText();
        String car_alimentation_str = car_alimentation.getText();
        String car_brand_str = car_brand.getText();
        String car_seat_str = car_seat.getText();
        String car_owners_str = car_owners.getText();
        String car_km_str = car_km.getText();

        AggiungiAutoBean  aggiungiAutoBean = new AggiungiAutoBean();

        aggiungiAutoBean.setCar_alimentation(car_alimentation_str);
        aggiungiAutoBean.setCar_brand(car_brand_str);
        aggiungiAutoBean.setCar_name(car_name_str);
        aggiungiAutoBean.setCar_seat((Integer.parseInt(car_seat_str)));
        aggiungiAutoBean.setCar_km((Integer.parseInt(car_km_str)));
        aggiungiAutoBean.setCar_price((Integer.parseInt(car_price_str)));
        //aggiungiAutoBean.setCar_type();
        aggiungiAutoBean.setCar_year((Integer.parseInt(car_year_str)));
        aggiungiAutoBean.setCar_owners((Integer.parseInt(car_owners_str)));

        try{
            aggiungiAutoBean.sendinfo();
        }catch(Exception e){
            e.printStackTrace();
        }

        car_brand.clear();
        car_seat.clear();
        car_owners.clear();
        car_km.clear();
        car_price.clear();
        car_alimentation.clear();
        car_year.clear();
        car_name.clear();

    }

}

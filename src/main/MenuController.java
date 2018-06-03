package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private ListView<String> patientsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HttpRequests httpRequests = new HttpRequests();
        ArrayList<Patient> patients = null;
        try {
            patients = httpRequests.getPatients();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert patients != null;
        for(Patient patient : patients){
            patientsList.getItems().add(patient.getName()[0].getFamily() + " " + patient.getName()[0].getGiven()[0]);
        }
    }
}

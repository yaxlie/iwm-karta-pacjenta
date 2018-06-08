package main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import jdk.nashorn.internal.runtime.Debug;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MenuController implements Initializable {
    private interface selectionChangeListener{
        public void onChange();
        public void onPatientChange();
        public void onCategoryChange();
        public void onObservationChange();
    }
    private abstract class Selected implements selectionChangeListener{
        Patient patient = null;
        String category = null;
        Observation observation = null;

        public Patient getPatient() {
            return patient;
        }

        public void setPatient(Patient patient) {
            this.patient = patient;
            onChange();
            onPatientChange();
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
            onChange();
            onCategoryChange();
        }

        public Observation getObservation() {
            return observation;
        }

        public void setObservation(Observation observation) {
            this.observation = observation;
            onObservationChange();
        }
    }

    Logger logger = Logger.getLogger("Menu");

    Selected selected = new Selected() {
        @Override
        public void onChange() {
            if(selected.getCategory()!=null && selected.getPatient() != null){
                observationsList.getItems().clear();
                textArea.setText("");
                List<Observation> patientObservations = observations.get(selected.getPatient().getId()).get(selected.getCategory());
                logger.info("Obserwacje dla: " + selected.getPatient().getId() + " " + selected.getCategory());
                if(patientObservations != null) {
                    for (int i = 0; i < patientObservations.size(); i++) {
                        observationsList.getItems().add(Integer.toString(i));
                    }
                }
            }
        }
        @Override
        public void onPatientChange() {
            //dodaj rodzaje obserwacji do fx listy
            categoriesList.getItems().clear();
            assert observations != null;
            for (String key : observations.get(selected.getPatient().getId()).keySet()) {
                //todo zamiast tego wyrzucic nulle z kontenera wczesniej
                if(key != null)
                    categoriesList.getItems().add(key);
            }
        }

        @Override
        public void onCategoryChange() {

        }

        @Override
        public void onObservationChange() {
            if(selected.getObservation().getValueQuantity()!= null) {
                String text = selected.getObservation().getValueQuantity().getValue() != null ?
                        selected.getObservation().getValueQuantity().getValue() : "";
                String unit = selected.getObservation().getValueQuantity().getUnit() != null ?
                        selected.getObservation().getValueQuantity().getUnit() : "";
                textArea.setText(text + " [" + unit + "]");
            }
            else if(selected.getObservation().getValueString() != null){
                textArea.setText(selected.getObservation().getValueString());
            }
        }
    };

    HashMap<String, Patient> patients = null;

    //name -> id
    HashMap<String, String> patientsIds = new HashMap<String, String>();

    //pacjent -> kategoria -> pomiar
    HashMap<String, HashMap<String, List<Observation>>> observations = new HashMap<String, HashMap<String, List<Observation>>>();
    HttpRequests httpRequests = new HttpRequests();

    @FXML
    private ListView<String> patientsList;
    @FXML
    private ListView<String> categoriesList;
    @FXML
    private ListView<String> observationsList;
    @FXML
    private TextArea textArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        observationsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selected.setObservation(observations.get(selected.getPatient()
                        .getId()).get(selected.getCategory()).get(Integer.parseInt(newValue)));
            }
        });

        patientsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selected.setPatient(patients.get(patientsIds.get(newValue)));
            }
        });

        categoriesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selected.setCategory(newValue);
            }
        });

        try {
            logger.info("Wczytywanie listy pacjentów...");
            patients = httpRequests.getPatients();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            logger.info("Wczytywanie listy Obserwacji...");
            for(Patient p : patients.values()){
                ArrayList<Observation> patientObservations = httpRequests.getObservation(p.getId());
                for(Observation o : patientObservations){
                    addObservation(o,p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //dodaj pacjentow do fx listy
        assert patients != null;
        for(Patient patient : patients.values()){
            String name = patient.getName()[0].getFamily() + " " + patient.getName()[0].getGiven();
            patientsList.getItems().add(name);
            patientsIds.put(name, patient.getId());
        }
    }

    private void addObservation(Observation o, Patient p){
        String oCategory = o.getCode().getText();

        //jesli pacjent w danej kategorii jest nowy, to stworz nowa podmape
        if(observations.get(p.getId()) == null){
            observations.put(p.getId(), new HashMap<String, List<Observation>>());
        }
        //jeśli kateogria obserwacji jest nowa, to stworz nowa mape
        if(observations.get(p.getId()).get(oCategory) == null){
            observations.get(p.getId()).put(oCategory, new ArrayList<Observation>());
        }
        //w koncu dodaj pomiar
        observations.get(p.getId()).get(oCategory).add(o);
    }
}

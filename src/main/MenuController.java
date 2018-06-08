package main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
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
        public void onCategoryChange(String category);
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
            onCategoryChange(category);
        }

        public Observation getObservation() {
            return observation;
        }

        public void setObservation(Observation observation) {
            this.observation = observation;
            onObservationChange();
        }
    }

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
    private TextArea patientInfo;
    @FXML
    private TextArea textArea;
    @FXML
    private LineChart lineChart;

    Logger logger = Logger.getLogger("Menu");

    Selected selected = new Selected() {
        @Override
        public void onChange() {
            if(selected.getCategory()!=null && selected.getPatient() != null){
                lineChart.getData().clear();
                textArea.setText("");
                List<Observation> patientObservations = observations.get(selected.getPatient().getId()).get(selected.getCategory());
                logger.info("Obserwacje dla: " + selected.getPatient().getId() + " " + selected.getCategory());
                if(patientObservations != null) {
                    String seriesText = patientObservations.get(0).getValueQuantity() != null?
                            selected.getCategory() + " [" + patientObservations.get(0).getValueQuantity().getUnit() + "]": "";
                    XYChart.Series series = new XYChart.Series();
                    series.setName(seriesText);
                    for (int i = patientObservations.size()-1; i >= 0; i--) {
                        Observation o = patientObservations.get(i);
                        if(o.getValueQuantity()!=null)
                            series.getData().add(new XYChart.Data(o.getIssued().split("T")[0],Double.parseDouble(o.getValueQuantity().getValue())));
                        if(o.getValueString()!=null){
                            textArea.setText(textArea.getText() + " (" +o.getIssued().split("T")[0] +") " + o.getValueString() + "\n");
                        }
                    }
                    lineChart.getData().add(series);
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
            Patient p = selected.getPatient();
            patientInfo.setText(p.getName()[0].getGiven() + " " + p.getName()[0].getFamily()  + " ("+ p.getGender() + ") \n"
                    + p.getBirthDate() + "\n"
                    + p.getAddress()[0].getPostalCode() + " " + p.getAddress()[0].getCity()+ " ("
                    + p.getAddress()[0].getCountry() +", "   + p.getAddress()[0].getState() + ") \n"
                    + p.getTelecom()[0].getValue());
        }

        @Override
        public void onCategoryChange(String category) {
            lineChart.setTitle(category);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {


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

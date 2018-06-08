package main;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class HttpRequests {
    public static String URL = "http://localhost:8080/baseDstu3";
    Logger logger = Logger.getLogger("Http");

    public HashMap<String, Patient> getPatients() throws Exception {
        HashMap<String, Patient> patients = new HashMap<String, Patient>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(URL + "/Patient");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            logger.info("Wczytywanie pacjentów: " + response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            String responseString = new BasicResponseHandler().handleResponse(response);
//            System.out.println(responseString);

            JSONObject jsonObj = new JSONObject(responseString);
            JSONArray jsonArray = jsonObj.getJSONArray("entry");

            Gson gson = new Gson();
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i).getJSONObject("resource");
                String jsonString = json.toString();
                Patient patient = gson.fromJson(jsonString, Patient.class);
                patients.put(patient.getId(), patient);
            }

            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return patients;
    }

    public ArrayList<Observation> getObservation(String patient_id) throws Exception {
        ArrayList<Observation> observations = new ArrayList<Observation>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(URL + "/Observation?patient="+ patient_id);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            logger.info("Wczytywanie Obserwacji: " + response.getStatusLine().toString() + " (" + patient_id + ")");
            HttpEntity entity = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            String responseString = new BasicResponseHandler().handleResponse(response);
//            System.out.println(responseString);

            JSONObject jsonObj = new JSONObject(responseString);
            JSONArray jsonArray = jsonObj.getJSONArray("entry");

            Gson gson = new Gson();
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i).getJSONObject("resource");
                String jsonString = json.toString();
                Observation observation = gson.fromJson(jsonString, Observation.class);
                observations.add(observation);
            }

            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return observations;
    }
}

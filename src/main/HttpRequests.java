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

public class HttpRequests {
    public static String URL = "http://localhost:8080/baseDstu3";

    public ArrayList<Patient> getPatients() throws Exception {
        ArrayList<Patient> patients = new ArrayList<Patient>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(URL + "/Patient");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        try {
            System.out.println(response.getStatusLine());
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
                patients.add(patient);
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
            System.out.println(response.getStatusLine());
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

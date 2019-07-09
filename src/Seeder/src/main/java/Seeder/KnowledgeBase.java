package Seeder;

import Seeder.Responses.*;
import Seeder.Utils.SheetReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class KnowledgeBase {

    private KbPropertyValues _kbProperties;
    private String _kbName;

    public String getKnowledgeBaseId() { return _kbProperties.getPropertyValue("KnowledgeBaseId"); }

    public KnowledgeBase(String kbName) {
        _kbProperties = new KbPropertyValues(kbName + ".properties");
        _kbName = kbName;
        if (_kbProperties.getPropertyValue("KnowledgeBaseId") == null) {
            try {
                create();
            } catch (Exception e ) {
                System.out.println("Not able to create kb: " + e);
            }
        }
    }

    public void create() throws HttpException, UnsupportedEncodingException {
        if(_kbName == null) {
            throw new UnsupportedEncodingException("kbName should not be null.");
        }
        if(_kbName.length() == 0) {
            throw new UnsupportedEncodingException("kbName should not be empty.");
        }

        HttpClient httpClient = HttpClientBuilder.create().build();

        String endpoint = _kbProperties.getPropertyValue("BaseEndpoint");
        endpoint += "/knowledgebases/create";

        HttpPost httpPostRequest = new HttpPost(endpoint);

        //headers
        httpPostRequest.setHeader("Ocp-Apim-Subscription-Key", _kbProperties.getPropertyValue("SubscriptionKey"));
        httpPostRequest.setHeader("Content-Type", "application/json");

        //body
        String json = String.format("{'name':'%s'}", _kbName);
        StringEntity entity = new StringEntity(json);
        httpPostRequest.setEntity(entity);

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPostRequest);
        }
        catch (ClientProtocolException e) {
            System.out.println("ClientProtocolException " + e);
        }
        catch (IOException e) {
            System.out.println("IOException " + e);
        }

        if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != 202 ) {
            throw new HttpException();
        }
        else {
            _kbProperties.setPropertyValues("KnowledgeBaseId", getInfo(_kbName).id);
            System.out.println("Success on creating kb named " + _kbName);
        }
    }

    public InfoBody getInfo(String kbName) {
        InfoBody info = null;
        HttpClient httpClient = HttpClientBuilder.create().build();
        String endpoint = _kbProperties.getPropertyValue("BaseEndpoint");
        endpoint += "/knowledgebases";
        HttpGet httpGetRequest = new HttpGet(endpoint);

        //headers
        httpGetRequest.setHeader("Ocp-Apim-Subscription-Key", _kbProperties.getPropertyValue("SubscriptionKey"));
        httpGetRequest.setHeader("Content-Type", "application/json");

        HttpResponse httpResponse = null;

        try {
            httpResponse = httpClient.execute(httpGetRequest);

            HttpEntity entity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            responseString = responseString.substring(responseString.indexOf(":") + 1, responseString.length() - 1);

            ObjectMapper mapper = new ObjectMapper();
            InfoBody[] arrayOfKb = mapper.readValue(responseString, InfoBody[].class);

            for (InfoBody body: arrayOfKb) {
                if (body.name.equals(kbName)) {
                    info = body;
                }
            }
        }
        catch (ClientProtocolException e) {
            System.out.println("ClientProtocolException " + e);
        }
        catch (IOException e) {
            System.out.println("IOException " + e);
        }
        return info;
    }

    public void addQnaPairs(String fileName) throws Exception {
        List<Pair> listedQnA = SheetReader.parseFileToList(fileName);

        HttpClient httpClient = HttpClientBuilder.create().build();
        String endpoint = _kbProperties.getPropertyValue("BaseEndpoint");
        endpoint += "/knowledgebases/" + _kbProperties.getPropertyValue("KnowledgeBaseId");

        HttpPatch httpPatchRequest = new HttpPatch(endpoint);

        //headers
        httpPatchRequest.setHeader("Ocp-Apim-Subscription-Key", _kbProperties.getPropertyValue("SubscriptionKey"));
        httpPatchRequest.setHeader("Content-Type", "application/json");

        //body
        StringEntity entity = createBodyForQnaAddRequest(listedQnA);
        httpPatchRequest.setEntity(entity);

        HttpResponse httpResponse = httpClient.execute(httpPatchRequest);

        if (httpResponse.getStatusLine().getStatusCode() != 202 ) {
            throw new HttpException();
        }
        else {
            System.out.println("Success on uploading the questions");
        }

    }

    private static StringEntity createBodyForQnaAddRequest(List<Pair> qnaList) throws Exception {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < qnaList.size(); i++ ) {
            sb.append(String.format("{'answer' : '%s',", qnaList.get(i).getAnswer()));
            sb.append(String.format("'questions': ['%s']}", qnaList.get(i).getQuestion()));

            if (i != qnaList.size() - 1) {
                sb.append(",");
            }
        }

        String json = String.format("{'add': { 'qnaList':[%s]}}", sb.toString());
        return new StringEntity(json);
    }

    public void publish() throws HttpException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        String endpoint = _kbProperties.getPropertyValue("BaseEndpoint");
        endpoint += "/knowledgebases/" + _kbProperties.getPropertyValue("KnowledgeBaseId");
        HttpPost httpPostRequest = new HttpPost(endpoint);

        //headers
        httpPostRequest.setHeader("Ocp-Apim-Subscription-Key", _kbProperties.getPropertyValue("SubscriptionKey"));
        httpPostRequest.setHeader("Content-Type", "application/json");

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPostRequest);
        }
        catch (ClientProtocolException e) {
            System.out.println("ClientProtocolException " + e);
        }
        catch (IOException e) {
            System.out.println("IOException " + e);
        }
        if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != 204 ) {
            throw new HttpException();
        }
        else {
            _kbProperties.setPropertyValues("ServiceHost", getInfo(_kbName).hostName + "/qnamaker/knowledgebases");
            _kbProperties.setPropertyValues("EndpointKey", getEndpointKeys().primaryEndpointKey);
            System.out.println("Success on publishing.");
        }
    }

    public AnswerBody[] getAnswer(String message, int top, int scoreThreshold) throws UnsupportedEncodingException {
        if(message == null || message.length() == 0) {
            throw new UnsupportedEncodingException("Null or empty input message was provided.");
        }
        if (top < 1) {
            throw new IllegalArgumentException("Top value should be positive.");
        }
        if (scoreThreshold < 0 || scoreThreshold > 100) {
            throw new IllegalArgumentException("ScoreThreshold should be between 0 and 100.");
        }
        HttpClient httpClient = HttpClientBuilder.create().build();
        String endpoint = _kbProperties.getPropertyValue("ServiceHost");
        endpoint += String.format("/%s/generateAnswer", _kbProperties.getPropertyValue("KnowledgeBaseId"));

        HttpPost httpPostRequest = new HttpPost(endpoint);

        // Headers
        httpPostRequest.setHeader("Content-Type", "application/json");
        httpPostRequest.setHeader("Authorization",
                "EndpointKey " + _kbProperties.getPropertyValue("EndpointKey"));

        // Body
        String json = String.format("{ 'question' : '%s', 'top' : %d, 'scoreThreshold' : %d}",
                message, top, scoreThreshold);
        httpPostRequest.setEntity(new StringEntity(json));

        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPostRequest);
            HttpEntity entity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            responseString = responseString.substring(responseString.indexOf(":") + 1, responseString.length() - 1);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseString, AnswerBody[].class);
        }
        catch (ClientProtocolException e) {
            System.out.println("ClientProtocolException " + e);
        }
        catch (IOException e) {
            System.out.println("IOException " + e);
        }
        return null;
    }

    public KeysBody getEndpointKeys() {
        String endpoint = _kbProperties.getPropertyValue("BaseEndpoint");
        endpoint += "/endpointkeys";
        HttpClient httpClient = HttpClientBuilder.create().build();;
        HttpGet httpGetRequest = new HttpGet(endpoint);
        //headers
        httpGetRequest.setHeader("Ocp-Apim-Subscription-Key", _kbProperties.getPropertyValue("SubscriptionKey"));

        HttpResponse httpResponse;
        KeysBody keys = null;

        try {
            httpResponse = httpClient.execute(httpGetRequest);
            HttpEntity entity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            keys = mapper.readValue(responseString, KeysBody.class);
        }
        catch (ClientProtocolException e) {
            System.out.println("ClientProtocolException " + e);
        }
        catch (IOException e) {
            System.out.println("IOException " + e);
        }
        return keys;
    }

    public void export(String path) { _kbProperties.storePropertyValues(path); }
}

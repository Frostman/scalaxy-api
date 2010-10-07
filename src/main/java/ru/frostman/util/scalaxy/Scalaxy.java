package ru.frostman.util.scalaxy;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.util.scalaxy.helpers.RequestMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.frostman.util.scalaxy.ScalaxyAction.*;
import static ru.frostman.util.scalaxy.helpers.RequestMethod.GET;
import static ru.frostman.util.scalaxy.helpers.RequestMethod.POST;

/**
 * Main class of Scalaxy API for Java implementation.
 * It provides all methods to work with API and
 * to authenticate in system.
 *
 * @author slukjanov aka Frostman
 */
public class Scalaxy {
    private static final Logger log = LoggerFactory.getLogger(Scalaxy.class);

    private final HttpClient client;

    public Scalaxy(String login, String password) {
        client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        client.getState().setCredentials(
                new AuthScope("www.scalaxy.ru", 443, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(login, password)
        );
    }

    public ScalaxyResponse getProjectsAsResponse() throws ScalaxyException {
        return execute(GET_PROJECTS_LIST, null, null);
    }

    public ScalaxyResponse getProjectAsResponse(String id) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", id);

        return execute(GET_PROJECT, null, urlParams);
    }

    private ScalaxyResponse execute(ScalaxyAction action, Map<String, String> params, Map<String, String> urlParams) throws ScalaxyException {
        HttpMethodBase request;
        String url = action.getUrlFormatter().format(urlParams);
        RequestMethod method = action.getMethod();

        if (method == GET) {
            request = new GetMethod(url);
        } else if (method == POST) {
            PostMethod tmp = new PostMethod(url);
            if (params != null) {
                tmp.setRequestBody(mapToNameValuePairs(params));
            }

            request = tmp;
        } else {
            throw new ScalaxyException("Unsupported request method");
        }

        request.setDoAuthentication(true);

        try {
            ScalaxyStatus status = ScalaxyStatus.getByHttpStatus(client.executeMethod(request));
            JSONParser parser = new JSONParser();
            JSONArray array = null;
            if (status.equals(action.getSuccessStatus())) {                
                array = (JSONArray) parser.parse(request.getResponseBodyAsString());
            } else {
                try {
                    array = (JSONArray) parser.parse(request.getResponseBodyAsString());
                } catch (ParseException e) {
                    // no operation
                }
            }
            return new ScalaxyResponse(array, status, status.equals(action.getSuccessStatus()));
        } catch (HttpException e) {
            throw new ScalaxyException(e);
        } catch (ParseException e) {
            throw new ScalaxyException(e);
        } catch (IOException e) {
            throw new ScalaxyException(e);
        } finally {
            request.releaseConnection();
        }
    }

    private NameValuePair[] mapToNameValuePairs(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        NameValuePair[] data = new NameValuePair[entrySet.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : entrySet) {
            data[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        return data;
    }

    private static String responseToString(ScalaxyResponse response) {
        StringBuilder result = new StringBuilder();
        if (response.isSuccess()) {
            result.append("[SUCCESS] ").append(response.getData().toJSONString());
        } else {
            result.append("[FAIL] ").append(response.getStatus()).append(":").append(response.getStatus().getDescription());
        }

        return result.toString();
    }
}

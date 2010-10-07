package ru.frostman.util.scalaxy;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
import static ru.frostman.util.scalaxy.helpers.RequestMethod.*;

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

    public ScalaxyResponse createProjectAsResponse(String name) throws ScalaxyException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);

        return execute(CREATE_PROJECT, params, null);
    }

    public ScalaxyResponse getProjectAsResponse(String projectId) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", projectId);

        return execute(GET_PROJECT, null, urlParams);
    }

    public ScalaxyResponse deleteProjectAsResponse(String projectId) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", projectId);

        return execute(DELETE_PROJECT, null, urlParams);
    }

    public ScalaxyResponse getVirtualMachinesListAsResponse(String projectId) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", projectId);

        return execute(GET_VMS_LIST, null, urlParams);
    }

    public ScalaxyResponse getVirtualMachineAsResponse(String projectId, String instanceId) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", projectId);
        urlParams.put("instance_id", instanceId);

        return execute(GET_VM, null, urlParams);
    }

    public ScalaxyResponse startVirtualMachineAsResponse(String projectId, String instanceId) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", projectId);
        urlParams.put("instance_id", instanceId);

        return execute(START_VM, null, urlParams);
    }

    public ScalaxyResponse restartVirtualMachineAsResponse(String projectId, String instanceId) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", projectId);
        urlParams.put("instance_id", instanceId);

        return execute(RESTART_VM, null, urlParams);
    }

    public ScalaxyResponse stopVirtualMachineAsResponse(String projectId, String instanceId) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", projectId);
        urlParams.put("instance_id", instanceId);

        return execute(STOP_VM, null, urlParams);
    }

    public ScalaxyResponse resizeVirtualMachineAsResponse(String projectId, String instanceId, String newSize) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", projectId);
        urlParams.put("instance_id", instanceId);

        Map<String, String> params = new HashMap<String, String>();
        params.put("slots", newSize);

        return execute(RESIZE_VM, params, urlParams);
    }

     public ScalaxyResponse renameVirtualMachineAsResponse(String projectId, String instanceId, String newName) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", projectId);
        urlParams.put("instance_id", instanceId);

        Map<String, String> params = new HashMap<String, String>();
        params.put("name", newName);

        return execute(RENAME_VM, params, urlParams);
    }

    public ScalaxyResponse deleteVirtualMachineAsResponse(String projectId, String instanceId) throws ScalaxyException {
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("project_id", projectId);
        urlParams.put("instance_id", instanceId);

        return execute(DELETE_VM, null, urlParams);
    }

    private ScalaxyResponse execute(ScalaxyAction action, Map<String, String> params, Map<String, String> urlParams) throws ScalaxyException {
        HttpMethodBase request;
        String url = action.getUrlFormatter().format(urlParams);
        RequestMethod method = action.getMethod();

        if (method == GET) {
            request = new GetMethod(url);
        } else if (method == POST) {
            PostMethod tmp = new PostMethod(url);
            tmp.setRequestHeader("Content-Type", "application/json");
            if (params != null) {
                tmp.setRequestBody(new NameValuePair[]{
                        new NameValuePair("JSON", mapToNameValuePairs(params).toJSONString())
                });
            }

            request = tmp;
        } else if (method == DELETE) {
            request = new DeleteMethod(url);
        } else if (method == PUT) {
            PutMethod tmp = new PutMethod(url);
            tmp.setRequestHeader("Content-Type", "application/json");
            if (params != null) {
                //todo find better way to set body                
                tmp.setRequestBody(mapToNameValuePairs(params).toJSONString());
            }

            request = tmp;
        } else {
            throw new ScalaxyException("Unsupported request method");
        }

        request.setDoAuthentication(true);

        try {
            ScalaxyStatus status = ScalaxyStatus.getByHttpStatus(client.executeMethod(request));

            JSONArray array = null;
            if (status.equals(action.getSuccessStatus())) {
                array = parseJSON(request.getResponseBodyAsString());
            } else {
                try {
                    array = parseJSON(request.getResponseBodyAsString());
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

    private JSONObject mapToNameValuePairs(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        JSONObject json = new JSONObject();
        Set<Map.Entry<String, String>> entrySet = params.entrySet();

        for (Map.Entry<String, String> entry : entrySet) {
            json.put(entry.getKey(), entry.getValue());
        }

        return json;
    }

    private static JSONArray parseJSON(String str) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(str);
        if (obj instanceof JSONObject) {
            JSONArray array = new JSONArray();
            array.add(obj);

            return array;
        } else {
            return (JSONArray) obj;
        }
    }
}

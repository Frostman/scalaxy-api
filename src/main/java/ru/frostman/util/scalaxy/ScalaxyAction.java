package ru.frostman.util.scalaxy;

import ru.frostman.util.scalaxy.helpers.RequestMethod;
import ru.frostman.util.scalaxy.helpers.URLFormatter;

import static ru.frostman.util.scalaxy.helpers.RequestMethod.*;
import static ru.frostman.util.scalaxy.ScalaxyStatus.*;

/**
 * @author slukjanov aka Frostman
 */
public enum ScalaxyAction {
    // Working with projects
    GET_PROJECTS_LIST("https://www.scalaxy.ru/api/projects.json", GET, SC_200),
    CREATE_PROJECT("https://www.scalaxy.ru/api/projects.json", POST, SC_201),
    GET_PROJECT("https://www.scalaxy.ru/api/projects/{project_id}.json", POST, SC_200),
    DELETE_PROJECT("https://www.scalaxy.ru/api/projects/{project_id}.json", DELETE, SC_204),

    // Working with ip addresses
    CREATE_IP("https://www.scalaxy.ru/api/projects/18/accounts/allocate_public_ip.json", PUT, SC_200),
    GET_IPS_LIST("https://www.scalaxy.ru/api/ip_addresses.json", GET, SC_200),

    // Working with virtual machines
    CREATE_VM("https://www.scalaxy.ru/api/projects/{project_id}/instances.json", POST, SC_201),
    GET_VMS_LIST("https://www.scalaxy.ru/api/projects/{project_id}/instances.json", GET, SC_200),
    GET_VM("https://www.scalaxy.ru/api/projects/{project_id}/instances/{instance_id}.json", GET, SC_200),
    START_VM("https://www.scalaxy.ru/api/projects/{project_id}/instances/{instance_id}/run.json", PUT, SC_200),
    RESTART_VM("https://www.scalaxy.ru/api/projects/{project_id}/instances/{instance_id}/reboot.json", PUT, SC_200),
    STOP_VM("https://www.scalaxy.ru/api/projects/{project_id}/instances/{instance_id}/terminate.json", PUT, SC_200),
    RESIZE_VM("https://www.scalaxy.ru/api/projects/{project_id}/instances/{instance_id}/resize.json", PUT, SC_200),
    RENAME_VM("https://www.scalaxy.ru/api/projects/{project_id}/instances/{instance_id}/rename.json", PUT, SC_200),
    DELETE_VM("https://www.scalaxy.ru/api/projects/{project_id}/instances/{instance_id}.json", DELETE, SC_204);

    private URLFormatter url;
    private RequestMethod method;
    private ScalaxyStatus successStatus;

    private ScalaxyAction(String url, RequestMethod method, ScalaxyStatus successStatus) {
        this.url = new URLFormatter(url);
        this.method = method;        
        this.successStatus = successStatus;
    }

    public URLFormatter getUrlFormatter() {
        return url;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public ScalaxyStatus getSuccessStatus() {
        return successStatus;
    }
}

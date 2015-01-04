package me.loki2302;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) throws MikrotikApiException, InterruptedException, JsonProcessingException {
        ApiConnection connection = ApiConnection.connect("192.168.10.1");
        try {
            connection.login("admin", "");
            dumpInterfaces(connection);
            pingAllMyISPs(connection);
        } finally {
            connection.disconnect();
        }
    }

    private static void pingAllMyISPs(ApiConnection connection) throws JsonProcessingException, MikrotikApiException, InterruptedException {
        for(String interfaceName : new String[] { "ether2", "ether3", "pppoe-rostelekom" }) {
            System.out.printf("PING via %s\n", interfaceName);

            String command = String.format("/ping address=8.8.8.8 count=3 interface=%s", interfaceName);
            List<Map<String, String>> results = connection.execute(command);
            printResults(results);

            System.out.println();
        }
    }

    private static void dumpInterfaces(ApiConnection connection) throws JsonProcessingException, MikrotikApiException {
        List<Map<String, String>> results = connection.execute("/interface/print");
        printResults(results);
    }

    private static void printResults(List<Map<String, String>> results) throws JsonProcessingException {
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(results));
    }
}

package com.github.adminpanelfinder;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println(Colors.GREEN + "AdminPanelFinder v1.0 \n" +
                "Created by kysplease\n" + Colors.RESET);

        System.out.println(Colors.GREEN + "Enter the site you'd like to test: " + Colors.RESET);
        Scanner scanner = new Scanner(System.in);

        Path directoryList = FileSystems.getDefault().getPath("list.txt");
        Path validLinks = FileSystems.getDefault().getPath("validLinks.txt");

        Scanner reader = new Scanner(directoryList);
        BufferedWriter writer = Files.newBufferedWriter(validLinks);

        String site = scanner.nextLine();

        if (!site.endsWith("/")) {
            site += "/";
        }

        if(!site.startsWith("http://")) {
            site = "http://" + site;
        }

        int failCount = 0;
        int successCount = 0;
        int possibleSuccessCount = 0;

        try {
            while (reader.hasNext()) {
                String siteWithDirectory = site + reader.nextLine();

                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet request = new HttpGet(siteWithDirectory);
                CloseableHttpResponse response = httpClient.execute(request);

                if (response.getStatusLine().getStatusCode() == 404) {
                    System.out.println(Colors.RED +
                            siteWithDirectory + " returns 404." + Colors.RESET);
                    failCount++;
                } else if (response.getStatusLine().getStatusCode() == 200) {
                    System.out.println(Colors.GREEN +
                            siteWithDirectory + " returns 200!" + Colors.RESET);
                    writer.write(siteWithDirectory + " (Response Code 200)\n");
                    successCount++;
                } else {
                    System.out.println(Colors.YELLOW +
                            siteWithDirectory + "returns " + response.getStatusLine().getStatusCode() + "!" +
                            Colors.RESET);
                    writer.write(siteWithDirectory + " (Response Code " +
                            response.getStatusLine().getStatusCode() + ")");
                    possibleSuccessCount++;
                }
                response.close();
            }
        } catch (NoHttpResponseException e) {
            System.out.println(Colors.RED +
            "NoHttpResponseException: Link is either invalid or the site is down." +
            Colors.RESET);

            System.exit(1);
        } catch (HttpHostConnectException e) {
            System.out.println(Colors.RED +
                    "HttpHostConnectException: Link is either invalid or the site is down." +
                    Colors.RESET);

            System.exit(1);
        } catch (ClientProtocolException e) {
            System.out.println(Colors.RED +
                    "ClientProtocolException: Invalid host name." +
                    Colors.RESET);

            System.exit(1);
        }

        System.out.println(Colors.RED +
        "\nAmount Failed: " + failCount + Colors.RESET);

        System.out.println(Colors.YELLOW +
        "Amount Possibly Successful: " + possibleSuccessCount + Colors.RESET);

        System.out.println(Colors.GREEN +
        "Amount Successful: " + successCount + Colors.RESET);

        System.out.println(Colors.GREEN +
        "\nAll valid links have been added to 'validLinks.txt', along with their response codes." +
        Colors.RESET);

        reader.close();
        writer.close();

    }

    private class Colors {
        private static final String RED = "\033[91m";
        private static final String GREEN = "\033[92m";
        private static final String YELLOW = "\033[93m";
        private static final String RESET = "\033[0m";
    }
}
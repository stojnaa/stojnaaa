/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static boolean loggedIn = false;
    private static String loggedUserId = null;
    private static String loggedUsername = null;
    private static String loggedPassword = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        while (true) {
            int code = printMenu();
            if (!loggedIn && code != 0 && code != 1) {
                System.out.println("You must be logged in (option 1).");
                continue;
            }
            if (loggedIn && code == 1) {
                System.out.println("You already logged in " + loggedUsername + " (id=" + loggedUserId + ").");
                continue;
            }
            if (!loggedIn && code == 24) {
                System.out.println("You did not logged in.");
                continue;
            }
            if (code == 24) {
                loggedIn = false;
                loggedUserId = null;
                loggedUsername = null;
                loggedPassword = null;
                System.out.println("Logout OK.");
                continue;
            }
            if (code == 0) {
                break;
            }
            if (code == -1) {
                continue;
            }
            String url = getURL(code);
            HashMap<String, String> map = getMap(code);
            String reqMet = getReq(code);
            String result = send(url, reqMet, map);
            if (code == 1 && result != null) {
                String r = extractOkLine(result);
                if (r.startsWith("OK|")) {
                    String[] parts = r.split("\\|");
                    if (parts.length >= 3) {
                        loggedIn = true;
                        loggedUserId = parts[1];
                        loggedUsername = parts[2];
                        loggedPassword = parts[3];
                        System.out.println("You logged in like " + loggedUsername + " (id=" + loggedUserId + ").");
                    } else {
                        System.out.println("Login response bad format: " + r);
                    }
                } else {
                    System.out.println("Login failed: " + r);
                }
            }
            print(code, result);
        }
        scanner.close();

    }

    private static String extractOkLine(String result) {
        if (result == null) {
            return null;
        }
        for (String line : result.split("\\R")) { // \R = bilo koji newline
            line = line.trim();
            if (line.startsWith("OK|") || line.startsWith("ERR") || line.startsWith("Login FAIL")) {
                return line;
            }
        }
        return result.trim();
    }

    private static int printMenu() {
        System.out.println("MENU:");
        System.out.println("1.  Login");
        System.out.println("2.  Create city");
        System.out.println("3.  Create user");
        System.out.println("4.  Add money to user");
        System.out.println("5.  Update user's address and city");
        System.out.println("6.  Create category");
        System.out.println("7.  Create item");
        System.out.println("8.  Update item price");
        System.out.println("9.  Set item discount");
        System.out.println("10. Add item to cart (quantity)");
        System.out.println("11. Remove item from cart (quantity)");
        System.out.println("12. Add item to wishlist");
        System.out.println("13. Remove item from wishlist");
        System.out.println("14. Pay (create transaction + order + order items, clear cart)");
        System.out.println("15. Get all cities");
        System.out.println("16. Get all users");
        System.out.println("17. Get all categories");
        System.out.println("18. Get items by seller");
        System.out.println("19. Get cart content");
        System.out.println("20. Get wishlist content");
        System.out.println("21. Get user's orders");
        System.out.println("22. Get all orders");
        System.out.println("23. Get all transactions");
        System.out.println("24. Logout");
        System.out.println("0.  Exit");
        int code;
        try {
            code = Integer.parseInt(scanner.nextLine());
            if (code < 0 || code > 24) {
                System.out.println("Enter again.");
                code = -1;
            }
            System.out.println("CODE = " + code);
        } catch (NumberFormatException ex) {
            System.out.println("Enter again.");
            code = -1;
        }
        return code;
    }

    private static void print(int code, String result) {
        System.out.println(result);
    }

    private static String getURL(int code) {
        String url = "http://localhost:8080/centralServer/resources/";

        switch (code) {
            case 1:
                return url + "ps1/users/login";
            case 2:
                return url + "ps1/cities";
            case 3:
                return url + "ps1/users";
            case 4:
                return url + "ps1/users/" + loggedUserId + "/money";

            case 5:
                return url + "ps1/users/" + loggedUserId;
            case 6:
                return url + "ps2/categories";
            case 7:
                return url + "ps2/items";
            case 8:
                return url + "ps2/items/price";
            case 9:
                return url + "ps2/items/discount";
            case 10:
                return url + "ps2/cart/add/" + loggedUserId;
            case 11:
                return url + "ps2/cart/remove/" + loggedUserId;
            case 12:
                return url + "ps2/wishlist/add/" + loggedUserId;
            case 13:
                return url + "ps2/wishlist/remove/" + loggedUserId;
            case 14:
                return url + "ps3/pay/" + loggedUserId;
            case 15:
                return url + "ps1/cities";
            case 16:
                return url + "ps1/users";
            case 17:
                return url + "ps2/categories";
            case 18:
                return url + "ps2/items/seller/" + loggedUserId;
            case 19:
                return url + "ps2/cart/" + loggedUserId;
            case 20:
                return url + "ps2/wishlist/" + loggedUserId;
            case 21:
                return url + "ps3/orders/" + loggedUserId;
            case 22:
                return url + "ps3/orders/all";
            case 23:
                return url + "ps3/transactions";

        }

        return url;
    }

    private static HashMap<String, String> getMap(int code) {
        HashMap<String, String> map = new HashMap<>();

        switch (code) {
            case 1: {
                System.out.print("username: ");
                map.put("username", scanner.nextLine());
                System.out.print("password: ");
                map.put("password", scanner.nextLine());
                break;
            }
            case 2: {
                System.out.print("city name: ");
                map.put("name", scanner.nextLine());
                break;
            }
            case 3: {
                System.out.print("username: ");
                map.put("username", scanner.nextLine());
                System.out.print("password: ");
                map.put("password", scanner.nextLine());
                System.out.print("firstName: ");
                map.put("firstName", scanner.nextLine());
                System.out.print("lastName: ");
                map.put("lastName", scanner.nextLine());
                System.out.print("address: ");
                map.put("address", scanner.nextLine());
                System.out.print("balance: ");
                map.put("balance", scanner.nextLine());
                System.out.print("cityId: ");
                map.put("cityId", scanner.nextLine());
                break;
            }
            case 4: {
                System.out.print("amount: ");
                map.put("amount", scanner.nextLine());
                break;
            }
            case 5: {
                System.out.print("address: ");
                map.put("address", scanner.nextLine());
                System.out.print("cityId: ");
                map.put("cityId", scanner.nextLine());
                break;
            }
            case 6: {
                System.out.print("name: ");
                map.put("name", scanner.nextLine());
                System.out.print("parentId (prazno ako nema): ");
                map.put("parentId", scanner.nextLine());
                break;
            }
            case 7: {
                System.out.print("name: ");
                map.put("name", scanner.nextLine());
                System.out.print("description: ");
                map.put("description", scanner.nextLine());
                System.out.print("price: ");
                map.put("price", scanner.nextLine());
                System.out.print("discount_pct: ");
                map.put("discount_pct", scanner.nextLine());
                System.out.print("categoryId: ");
                map.put("categoryId", scanner.nextLine());
                System.out.print("sellerId: ");
                map.put("sellerId", loggedUserId);
                break;
            }
            case 8: {
                map.put("userId", loggedUserId);
                System.out.print("itemId: ");
                map.put("itemId", scanner.nextLine());
                System.out.print("price: ");
                map.put("price", scanner.nextLine());
                break;
            }
            case 9: {
                map.put("userId", loggedUserId);
                System.out.print("itemId: ");
                map.put("itemId", scanner.nextLine());
                System.out.print("discount_pct: ");
                map.put("discount_pct", scanner.nextLine());
                break;
            }
            case 10: {
                System.out.print("itemId: ");
                map.put("itemId", scanner.nextLine());
                System.out.print("quantity: ");
                map.put("quantity", scanner.nextLine());
                break;
            }
            case 11: {
                System.out.print("itemId: ");
                map.put("itemId", scanner.nextLine());
                System.out.print("quantity: ");
                map.put("quantity", scanner.nextLine());
                break;
            }
            case 12: {
                System.out.print("itemId: ");
                map.put("itemId", scanner.nextLine());
                break;
            }
            case 13: {
                System.out.print("itemId: ");
                map.put("itemId", scanner.nextLine());
                break;
            }

        }

        return map;
    }

    private static String send(String url, String requestMethod, HashMap<String, String> map) {
        try {
            String queryParams = "";
            for (String key : map.keySet()) {
                if (!queryParams.isEmpty()) {
                    queryParams += "&";
                }
                queryParams += key + "=" + URLEncoder.encode(map.get(key), "UTF-8");
            }

            if (requestMethod.equals("GET") || requestMethod.equals("DELETE")) {
                if (!queryParams.isEmpty()) {
                    url += "?" + queryParams;
                }
            }

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod(requestMethod);
            if (loggedIn) {
                String token = Base64.getEncoder().encodeToString(
                        (loggedUsername + ":" + loggedPassword).getBytes(StandardCharsets.UTF_8)
                );
                con.setRequestProperty("Authorization", "Basic " + token);
            }

            if (requestMethod.equals("POST") || requestMethod.equals("PUT")) {
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                DataOutputStream os = new DataOutputStream(con.getOutputStream());
                os.writeBytes(queryParams);
                os.flush();
                os.close();
            }

            int responseCode = con.getResponseCode();
            BufferedReader in;
            if (responseCode >= 400) {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            in.close();

            return "HTTP " + responseCode + "\n" + response.toString();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private static String getReq(int code) {
        switch (code) {
            case 1:
                return "POST";
            case 2:
                return "POST";
            case 3:
                return "POST";
            case 4:
                return "POST";
            case 5:
                return "PUT";
            case 6:
                return "POST";
            case 7:
                return "POST";
            case 8:
                return "PUT";
            case 9:
                return "PUT";
            case 10:
                return "POST";
            case 11:
                return "POST";
            case 12:
                return "POST";
            case 13:
                return "POST";
            case 14:
                return "POST";
            default:
                return "GET";
        }
    }

}

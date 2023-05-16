import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class server {

    private static final int PORT = 12345;
    private static ArrayList<String> database;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started");

            // Load database
            database = loadDatabase();

            while (true) {
                Socket socket = serverSocket.accept();

                // Get search keyword from client
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String keyword = input.readLine();

                // Search database for the keyword
                ArrayList<String> searchResults = searchDatabase(keyword);

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String searchTime = formatter.format(date);
                out.println("Arama tarihi: " + searchTime);

                // Send search results to client
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                int i = 1;
                for (String result : searchResults) {
                    output.println(i + ". " + result);
                    i++;
                }

                // Get promo code from client
                String promoCode = input.readLine();

                // Check if promo code is valid
                if (!promoCode.matches("[A-Z]{4}\\d{4}[A-Z]{4}")) {
                    output.println("Hatalı promosyon kodu!");
                } else {
                    // Get year from promo code
                    int year = Integer.parseInt(promoCode.substring(0, 4));

                    // Calculate promo discount
                    double promoDiscount = getPromoDiscount(year,
                            Double.parseDouble(searchResults.get(0).split(",")[1].trim()), promoCode);

                    // Send promo discounted price to client
                    double discountedPrice = Double.parseDouble(searchResults.get(0).split(",")[1].trim())
                            - promoDiscount;
                    output.println("Promosyonlu fiyat: " + discountedPrice);
                }

                // Close resources
                input.close();
                output.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    private static ArrayList<String> loadDatabase() throws IOException {
        ArrayList<String> database = new ArrayList<String>();
        File file = new File("urunler.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            database.add(line);
        }
        reader.close();
        return database;
    }

    private static ArrayList<String> searchDatabase(String keyword) {
        ArrayList<String> results = new ArrayList<String>();
        for (String item : database) {
            if (item.toLowerCase().contains(keyword.toLowerCase())) {
                results.add(item);
            }
        }
        return results;
    }

    private static double getPromoDiscount(int year, double price, String promoCode) {
        double E = Double.parseDouble(promoCode.substring(6, 7));
        double F = Double.parseDouble(promoCode.substring(7, 8));
        double G = Double.parseDouble(promoCode.substring(8, 9));
        double H = Double.parseDouble(promoCode.substring(9));
        double x = year - Integer.parseInt(promoCode.substring(0, 4));
        double promo = (E * Math.pow(x, 3) + F * Math.pow(x, 2) + G * x + H) / Math.pow(x, 4);
        double discountedPrice = (1 - promo) * price;
        return discountedPrice;
    }

    private static boolean containsProduct(String productName) {
        for (String item : database) {
            if (item.toLowerCase().contains(productName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

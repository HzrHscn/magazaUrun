import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String keyword;

        while (true) {
            System.out.println("Ürün adı girin (Çıkmak için 'exit' yazın): ");
            keyword = input.readLine();

            if (keyword.equalsIgnoreCase("exit")) {
                break;
            }

            String[] keywords = keyword.split("\\s+");
            if (keywords.length > 2) {
                System.out.println("En az bir kelime, en fazla iki kelime giriniz!");
                continue;
            }

            Socket socket = new Socket(HOST, PORT);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(keyword);

            BufferedReader serverOutput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String result;
            while (true) {
                result = serverOutput.readLine();
                System.out.println(result);

                System.out.println("Promosyon kodunu girin: ");
                String promoCode = input.readLine();

                if (!promoCode.contains("1317")) {
                    System.out.println("Hata: Promosyon kodu geçersiz.");
                    continue;
                }

                String yearStr = promoCode.substring(0, 4);
                int year = Integer.parseInt(yearStr);
                int E = promoCode.charAt(8);
                int F = promoCode.charAt(9);
                int G = promoCode.charAt(10);
                int H = promoCode.charAt(11);
                int x = 2023 - year;

                double promosyon = (double) (((E * Math.pow(x, 3)) + (F * Math.pow(x, 2)) + (G * x) + H))
                        / Math.pow(x, 4);

                System.out.println("Promosyon kullanılsın mı? (y/n)");
                String answer = input.readLine();

                if (answer.equalsIgnoreCase("y")) {
                    double fiyat = 1500;
                    double promosyonluFiyat = (1 - promosyon) * fiyat;
                    double kazanc = promosyonluFiyat - fiyat;
                    System.out.println("Promosyonlu Fiyat: " + promosyonluFiyat + " /  Toplam Kazancınız: "
                            + kazanc);

                } else {
                    System.out.println("Fiyat: " + 1500);
                }

                // İkinci ürün adı girişinin doğru yere taşınması
                System.out.println("Ürün adı girin (Çıkmak için 'exit' yazın): ");
                keyword = input.readLine();

                if (keyword.equalsIgnoreCase("exit")) {
                    System.out.println("Program sonlandırılıyor...");
                    socket.close();
                    return;
                }

                keywords = keyword.split("\\s+");
                if (keywords.length > 2) {
                    System.out.println("En az bir kelime, en fazla iki kelime giriniz!");
                    continue;
                }

                output.println(keyword);
                socket.close();
            }

            /*
             * if (!productList.containsProduct(keywords[0])) {
             * System.out.println("Hata: " + keywords[0] + " ürün listesinde bulunamadı.");
             * continue;
             * }
             */

        }
    }
}

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class DictionaryClient {
    private static final String SERVER = "127.0.0.1";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER, PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("🔗 Đã kết nối tới server.");
            System.out.println("Nhập từ tiếng Anh cần tra (gõ 'exit' để thoát):");

            while (true) {
                System.out.print("> ");
                String word = scanner.nextLine();
                out.println(word);
                if (word.equalsIgnoreCase("exit")) break;

                String response = in.readLine();
                if (response == null) break;
                System.out.println(response);

                if (response.contains("muốn thêm")) {
                    System.out.print("> ");
                    String answer = scanner.nextLine();
                    out.println(answer);

                    if (answer.equalsIgnoreCase("y")) {
                        String prompt = in.readLine();
                        System.out.println(prompt);
                        System.out.print("> ");
                        String newMeaning = scanner.nextLine();
                        out.println(newMeaning);
                        System.out.println(in.readLine());
                    } else {
                        System.out.println(in.readLine());
                    }
                }
            }
            System.out.println("👋 Đã ngắt kết nối.");
        } catch (IOException e) {
            System.out.println("❌ Không thể kết nối: " + e.getMessage());
        }
    }
}

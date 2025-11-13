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
            // Chọn chế độ tra cứu
            System.out.println("Chọn chế độ tra cứu: 1) Anh->Viet  2) Viet->Anh");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();
            String mode = "EN2V";
            if (choice.equals("2") || choice.equalsIgnoreCase("V")) mode = "V2E";
            out.println(mode);

            // Đọc và in dòng xác nhận chế độ từ server
            String modeReply = in.readLine();
            if (modeReply != null) System.out.println(modeReply);

            System.out.println("🔗 Đã kết nối tới server.");
            System.out.println("Nhập từ cần tra (gõ 'exit' để thoát):");

            while (true) {
                System.out.print("> ");
                String word = scanner.nextLine();
                out.println(word);
                if (word.equalsIgnoreCase("exit")) break;

                String response = in.readLine();
                if (response == null) break;
                System.out.println(response);

                // kiểm tra nếu server hỏi có muốn thêm (dùng kiểm tra chuỗi chung hơn)
                String lower = response.toLowerCase();
                if (lower.contains("muon them") || lower.contains("(y/n)") || lower.contains("ban co muon")) {
                    System.out.print("> ");
                    String answer = scanner.nextLine();
                    out.println(answer);

                    // nếu muốn thêm, server sẽ tiếp tục yêu cầu nhập; client đọc và gửi
                    if (answer.equalsIgnoreCase("y")) {
                        String prompt = in.readLine();
                        if (prompt != null) System.out.println(prompt);
                        System.out.print("> ");
                        String newMeaning = scanner.nextLine();
                        out.println(newMeaning);
                        String confirmation = in.readLine();
                        if (confirmation != null) System.out.println(confirmation);
                    } else {
                        String skipMsg = in.readLine();
                        if (skipMsg != null) System.out.println(skipMsg);
                    }
                }
            }
            System.out.println("👋 Đã ngắt kết nối.");
        } catch (IOException e) {
            System.out.println("❌ Không thể kết nối: " + e.getMessage());
        }
    }
}

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class DictionaryServer {
    private static final int Port = 8080;
    private static Properties dictionary = new Properties();

    private static void handleClient(Socket clientSocket) {
        try(
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            // Đọc chế độ từ client: "EN2V" hoặc "V2E"
            String modeLine = in.readLine();
            boolean viToEn = modeLine != null && (modeLine.equalsIgnoreCase("V2E") || modeLine.equalsIgnoreCase("VI2EN") || modeLine.equalsIgnoreCase("vi->en"));
            out.println("Chế độ: " + (viToEn ? "Tiếng Việt -> Tiếng Anh" : "Tiếng Anh -> Tiếng Việt"));

            String word;

            while((word = in.readLine()) != null) {
                word = word.trim();
                System.out.println("Tra tu: " + word + " (mode: " + (viToEn ? "V->E" : "E->V") + ")");

                if(word.equalsIgnoreCase("exit")) {
                    out.println("Ket thuc phien lam viec!!!");
                    break;
                }

                if (viToEn) {
                    // Tìm key (tiếng Anh) theo value (tiếng Việt)
                    String foundEnglish = null;
                    for (String eng : dictionary.stringPropertyNames()) {
                        String viet = dictionary.getProperty(eng);
                        if (viet != null && viet.trim().equalsIgnoreCase(word.trim())) {
                            foundEnglish = eng;
                            break;
                        }
                    }

                    if (foundEnglish != null) {
                        out.println("Nghia tieng Anh cua \"" + word + "\": " + foundEnglish);
                    } else {
                        out.println("Khong tim thay tu " + word + " ban co muon them tu nay khong? (y/N): ");
                        String answer = in.readLine();

                        if (answer != null && answer.equalsIgnoreCase("y")) {
                            out.println("Nhap tu tieng anh cho tu " + word + ": ");
                            String newEnglish = in.readLine();

                            if (newEnglish != null && !newEnglish.isBlank()) {
                                dictionary.setProperty(newEnglish.trim().toLowerCase(), word.trim());
                                saveDictionary();

                                out.println("✅ Đã thêm \"" + word + "\" = \"" + newEnglish + "\" vào từ điển!");
                            } else {
                                out.println("Khong the them tu do nghia trong");
                            }
                        } else {
                            out.println("Bo qua viec them tu");
                        }
                    }
                } else {
                    // EN -> VI (hành vi trước đó)
                    String key = word.toLowerCase();
                    String meaning = dictionary.getProperty(key);

                    if(meaning != null) {
                        out.println("Nghia cua tu " + word + ": " + meaning);
                    } else {
                        out.println("Khong tim thay tu " + word + " ban co muon them tu nay khong? (y/N): ");
                        String answer = in.readLine();

                        if (answer != null && answer.equalsIgnoreCase("y")) {
                            out.println("Nhap nghia tieng viet cho tu " + word + ": ");
                            String newMeaning = in.readLine();

                            if (newMeaning != null && !newMeaning.isBlank()) {
                                dictionary.setProperty(key, newMeaning.trim());
                                saveDictionary();

                                out.println("✅ Đã thêm \"" + word + "\" = \"" + newMeaning + "\" vào từ điển!");
                            } else {
                                out.println("Khong the them tu do nghia trong");
                            }
                        } else {
                            out.println("Bo qua viec them tu");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Loi khi xu ly client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client da ngat ket noi");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadDictionary() {
        try(Reader reader = new FileReader("dictionary.properties")) {
            dictionary.load(reader);
            System.out.println("📖 Đã tải từ điển: " + dictionary.size() + " từ.");
        } catch (IOException e) {
            System.out.println("⚠️ Không thể đọc file dictionary.properties.");
        }
    }

    private static void saveDictionary() {
        try (FileWriter writer = new FileWriter("dictionary.properties")) {
            dictionary.store(writer, "Cap nhat tu dien Anh-Viet");
        } catch (IOException e) {
            System.out.println("Loi khi luu tu dien: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        loadDictionary();

        try(ServerSocket serverSocket = new ServerSocket(Port)) {
            System.out.println("Server dang chay tai cong " + Port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client da ket noi " + clientSocket.getInetAddress());
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

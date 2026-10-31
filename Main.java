import java.io.*;

public class Main {

    // копирует один текстовый файл в другой
    static void copy(String from, String to) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(from));
             BufferedWriter out = new BufferedWriter(new FileWriter(to))) {
            String s = in.readLine();
            while (s != null) {
                out.write(s);
                out.newLine();
                s = in.readLine();
            }
        }
    }

    // то же копирование, но ошибка не выбрасывается, а выводится (для потоков)
    static void safeCopy(String from, String to) {
        try {
            copy(from, to);
        } catch (IOException e) {
            System.out.println("Ошибка копирования: " + e.getMessage());
        }
    }

    // создаем тестовый файл с нужным количеством строк
    static void createTestFile(String name, int count) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(name))) {
            for (int i = 0; i < count; i++) {
                pw.println("Строка номер " + i + ". Тестовые данные для проверки копирования.");
            }
        }
    }

    static void oneByOne(String a, String aCopy, String b, String bCopy) throws IOException {
        long t = System.currentTimeMillis();
        copy(a, aCopy);
        copy(b, bCopy);
        long time = System.currentTimeMillis() - t;
        System.out.println("Последовательное копирование: " + time + " мс");
    }

    static void together(String a, String aCopy, String b, String bCopy) throws InterruptedException {
        long t = System.currentTimeMillis();

        Thread first = new Thread(() -> safeCopy(a, aCopy));
        Thread second = new Thread(() -> safeCopy(b, bCopy));
        first.start();
        second.start();

        // ждем пока оба потока закончат
        first.join();
        second.join();

        long time = System.currentTimeMillis() - t;
        System.out.println("Параллельное копирование: " + time + " мс");
    }

    public static void main(String[] args) throws Exception {
        createTestFile("file1.txt", 200000);
        createTestFile("file2.txt", 200000);

        oneByOne("file1.txt", "copy1.txt", "file2.txt", "copy2.txt");
        together("file1.txt", "copy1.txt", "file2.txt", "copy2.txt");
    }
}

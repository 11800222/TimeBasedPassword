import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TimeBasedPassword {

   static String openDate = "2022-12-22";

    static String password;

    static BufferedWriter beginFile, endFile;

    static {
        try {
            beginFile = new BufferedWriter(new FileWriter("beginFile.password"));
            endFile = new BufferedWriter(new FileWriter("endFile.password"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String s[]) {
        password = String.valueOf(Math.random());
        outPut(password, beginFile);
         loops(() -> {
            if (isOpen()) {
                outPut(password, endFile);
            }else { outPut(openDate, endFile);}
        });
    }

    static void loops(Runnable runnable) {
        new Thread(() -> {
            while (true) {
                runnable.run();
                try {
                    Thread.sleep(1000 * 60 * 60);
                } catch (InterruptedException e) {
                    outPut(password, endFile);
                }
            }
        }).start();

    }

    static void outPut(String s, BufferedWriter writer) {
        try {
            writer.append(s);
            writer.append("\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean isOpen() {
        try {
            return getDate().compareTo(openDate) >= 0;
        } catch (Exception e) {
            outPut(Arrays.toString(e.getStackTrace()), endFile);
            return false;
        }
    }

    static String getDate() {
        try {
            URL url = new URL("http://worldtimeapi.org/api/timezone/Asia/Hong_Kong");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                byte[] buffer = new byte[256];
                int len = 0;
                while ((len = inputStream.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                String response = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                outPut(response, endFile);
                Pattern r = Pattern.compile("(\\d{1,4}[-|\\/|年|\\.|\\s]?(0[1-9]|1[1-2])[-|\\/|月|\\.|\\s]?(0[1-9]|[1-2]\\d|3[0-1]))", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
                Matcher m = r.matcher(response);
                if (m.find()) {
                    return m.group();
                } else {
                    throw new RuntimeException();
                }
            } finally {
                inputStream.close();
                baos.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

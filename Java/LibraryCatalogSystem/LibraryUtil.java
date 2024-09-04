import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LibraryUtil {
    private static final String FILE_NAME = "library_data.json";
    private static Gson gson = new Gson();

    public static List<Book> loadBooks() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type bookListType = new TypeToken<ArrayList<Book>>() {}.getType();
                return gson.fromJson(reader, bookListType);
            } catch (IOException e) {
                System.out.println("Error loading data: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    public static void saveBooks(List<Book> books) {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(books, writer);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}

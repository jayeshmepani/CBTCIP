import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AccountDatabase {
    private static final String FILE_NAME = "accounts.json";
    private static final Gson gson = new Gson();

    // Save accounts to a JSON file
    public static void saveAccounts(Map<String, Account> accounts) {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(accounts, writer);
            System.out.println("Accounts saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    // Load accounts from a JSON file
    public static Map<String, Account> loadAccounts() {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Type accountMapType = new TypeToken<Map<String, Account>>() {}.getType();
            return gson.fromJson(reader, accountMapType);
        } catch (IOException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
            return new HashMap<>();  // Return empty map if loading fails
        }
    }
}

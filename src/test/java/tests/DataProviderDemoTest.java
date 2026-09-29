package tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class DataProviderDemoTest {
    @DataProvider(name = "basicData")
    public Object[][] basicData() {
        return new Object[][]{
                {"user!@yahoo.com", "password123"},
                {"user!@gmail.com", "password456"},
        };
    }

    @Test(dataProvider = "basicData")
    public void testLogin(String username, String password) {
        System.out.println("Testing login with username: " + username +
                " and password: " + password);
    }

    @DataProvider(name = "hashMapData")
    public Object[][] hashMapData() {
        HashMap<String, String> user1 = new HashMap<>();
        user1.put("email", "user!@yahoo.com");
        user1.put("password", "password1");

        HashMap<String, String> user2 = new HashMap<>();
        user2.put("email", "user!@gmail.com");
        user2.put("password", "password2");

        return new Object[][]{{user1}, {user2}};
    }

    @Test(dataProvider = "hashMapData")
    public void testLoginWithHashMap(HashMap<String, String> userData) {
        System.out.println("Testing login with email: " + userData.get("email") +
                " and password: " + userData.get("password"));
    }

    @DataProvider(name="jsonData")
    public Object[][] jsonData() throws IOException {
        String jsonContent = new String(Files.readAllBytes(
                Paths.get(System.getProperty("user.dir")+"/src/test/resources/testData_TC1.json")
        ));

        Type type = new TypeToken<List<HashMap<String, String>>>() {}.getType();
        List<HashMap<String, String>> list = new Gson().fromJson(jsonContent, type);

        Object[][] table = new Object[list.size()][1];
        for(int i = 0; i < list.size(); i++) {
            table[i][0] = list.get(i);
        }
        return table;
    }
    @Test(dataProvider = "jsonData")
    public void testLoginWithJsonData(HashMap<String, String> userData) {
        System.out.println("Testing login with email: " + userData.get("email") +
                " and password: " + userData.get("password"));
    }
}

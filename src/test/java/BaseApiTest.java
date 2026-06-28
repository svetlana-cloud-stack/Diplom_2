import io.restassured.RestAssured;
import org.junit.BeforeClass;

import static data.UserData.BASE_URI;

public class BaseApiTest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = BASE_URI;
    }
}

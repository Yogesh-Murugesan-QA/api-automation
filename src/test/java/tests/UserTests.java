package tests;
import api.UserApi;
import org.testng.annotations.Test;
import pojo.User;
import data.*;
import base.BaseTest;

import static org.hamcrest.Matchers.*;
import java.util.HashMap;
import java.util.Map;

public class UserTests extends BaseTest {
    @Test(groups = {"smoke", "regression"})
    public void createUser(){
        User user =new User("Yogi","SDET");
        UserApi.createUser(user)
                .then().statusCode(201)
                .body("name",equalTo(user.getName()));
    }
    @Test(groups = {"regression"}, dataProvider ="invalidCreateUser",dataProviderClass = UserTestData.class)
    public void createUserValidation(Object name, Object job,
                                     int expectedStatus, String description){
        Map<String, Object> body = new HashMap<>();
        if (name != null) body.put("name", name);
        if (job != null) body.put("job", job);
        UserApi.createUser(body)
                .then()
                .log().ifValidationFails()
                .statusCode(expectedStatus);
    }
    @Test(groups = {"regression"})
    public void getUsers(){
        int page= 1;
        UserApi.getUsers(page)
                .then().statusCode(200)
                .body("page",equalTo(page))
                .body("data",hasSize(greaterThan(0)));
    }

    @Test(groups = {"smoke"})
    public void updateUser(){
        int id=1;
        User user =new User("Yogi","SDET/QA");
        UserApi.updateUser(id,user)
                .then().statusCode(200)
                .body("job",equalTo(user.getJob()))
                .body("updatedAt",notNullValue());
    }
    @Test(groups = {"smoke", "regression"})
    public void deleteUser(){
        int id= 1;
        UserApi.deleteUser(id).then().statusCode(204).log().body();
    }

}

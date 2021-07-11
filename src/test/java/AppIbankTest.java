import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;

public class AppIbankTest {

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private static Registration userActive = DataGenerator.generateUserActive();
    private static Registration userBlocked = DataGenerator.generateUserBlocked();

    @BeforeAll
    static void setUpAll() {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(userActive)// передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(userBlocked)// передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    @BeforeEach
    void goToURL() {
        open("http://localhost:9999");
    }

    @Test
    void shouldLoginSuccess() {
        $("[data-test-id=login] input").setValue(userActive.getLogin());
        $("[data-test-id=password] input").setValue(userActive.getPassword());
        $("[data-test-id=action-login]").click();
        $("h2").should(visible, Duration.ofSeconds(3)).shouldHave(text("Личный кабинет"));
    }

    @Test
    void shouldFailIfWrongUsername() {
        $("[data-test-id=login] input").setValue("WrongUsername");
        $("[data-test-id=password] input").setValue(userActive.getPassword());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").should(visible, Duration.ofSeconds(3)).shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    void shouldFailIfWrongPassword() {
        $("[data-test-id=login] input").setValue(userActive.getLogin());
        $("[data-test-id=password] input").setValue("WrongPassword");
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").should(visible, Duration.ofSeconds(3)).shouldHave(text("Неверно указан логин или пароль"));
    }

    @Test
    void shouldFailIfUserBlocked() {
        $("[data-test-id=login] input").setValue(userBlocked.getLogin());
        $("[data-test-id=password] input").setValue(userBlocked.getPassword());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=error-notification]").should(visible, Duration.ofSeconds(3)).shouldHave(text("Пользователь заблокирован"));
    }

    @Test
    void shouldFailIfUsernameEmpty() {
        $("[data-test-id=password] input").setValue(userActive.getPassword());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=login] .input__sub").shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldFailIfPasswordEmpty() {
        $("[data-test-id=login] input").setValue(userActive.getLogin());
        $("[data-test-id=action-login]").click();
        $("[data-test-id=password] .input__sub").shouldHave(text("Поле обязательно для заполнения"));
    }
}

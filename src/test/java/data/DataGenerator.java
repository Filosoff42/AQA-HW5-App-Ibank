package data;

import com.github.javafaker.Faker;

import java.util.Locale;


public class DataGenerator {

    private DataGenerator() {
    }

    public static Registration generateUserActive() {
        Faker faker = new Faker(new Locale("en"));
        return new Registration(
                faker.name().username(),
                faker.internet().password(8, 16),
                "active"
        );
    }

    public static Registration generateUserBlocked() {
        Faker faker = new Faker(new Locale("en"));
        return new Registration(
                faker.name().username(),
                faker.internet().password(8, 16),
                "blocked"
        );
    }
}

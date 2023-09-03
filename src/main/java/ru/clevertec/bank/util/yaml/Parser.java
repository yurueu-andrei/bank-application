package ru.clevertec.bank.util.yaml;

import lombok.Getter;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

@Getter
public class Parser {

    private final Properties yaml;

    @SneakyThrows
    public Parser() {
        Yaml yaml = new Yaml(new Constructor(Properties.class, new LoaderOptions()));
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("application.yml");
        this.yaml = yaml.load(inputStream);
        if (inputStream != null) {
            inputStream.close();
        }
    }
}

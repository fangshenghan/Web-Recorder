package cn.sharkmc.demo;

import cn.sharkmc.demo.WebRecorder.Main;
import cn.sharkmc.demo.WebRecorder.Window;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@RestController
public class DemoApplication {

    public static String tempFolder = "";

    public static void main(String[] args) throws Exception {
        Path tempDir = Files.createTempDirectory("WebRecorderTemp");
        tempFolder = tempDir.toAbsolutePath().toString();

        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        FileService fileService = context.getBean(FileService.class);
        fileService.copyResourceFile("/static/chromedriver.exe", tempFolder + "\\chromedriver.exe");

        System.setProperty("java.awt.headless", "false");
        Window.initializeWindow();
    }

}

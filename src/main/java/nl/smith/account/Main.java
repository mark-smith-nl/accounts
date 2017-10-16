package nl.smith.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nl.smith.account.service.ImportService;

@SpringBootApplication
public abstract class Main implements CommandLineRunner {

	private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

	@Autowired
	private ImportService importService;

	public static void main(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(Main.class);
		springApplication.setBannerMode(Banner.Mode.OFF);
		springApplication.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		importService.importFromFile("/home/mark/Downloads/TXT170929134202.TAB");
	}

}

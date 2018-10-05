package nl.smith.account;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import nl.smith.account.development.validation.PojoService;
import nl.smith.account.domain.Mutation;
import nl.smith.account.domain.MutationFile;
import nl.smith.account.enums.persisted.AccountNumber;
import nl.smith.account.enums.persisted.Currency;
import nl.smith.account.service.ImportService;
import nl.smith.account.service.MutationFileService;
import nl.smith.account.service.MutationService;

@SpringBootApplication
public abstract class Application {

	private final MutationService mutationService;

	private final MutationFileService mutationFileService;

	private final ImportService importService;
	private final PojoService pojoService;

	public Application(MutationService mutationService, MutationFileService mutationFileService, ImportService importService, PojoService pojoService) {
		super();
		this.mutationService = mutationService;
		this.mutationFileService = mutationFileService;
		this.importService = importService;
		this.pojoService = pojoService;
	}

	public static void main(String[] args) throws Exception {
		SpringApplication springApplication = new SpringApplication(Application.class);

		ConfigurableApplicationContext context = springApplication.run();

		Application application = context.getBean(Application.class);

		// application.insertSimpleMutations();

		// application.insertMutation();
		// application.insertMutations();

		// application.getMutations();
		// application.addFile();
		// application.getMutationFileByAbsoluteFilePath();
	}

	private void removeMutations() {
		mutationService.removeMutations();
	}

	private void insertMutation() {
		// @formatter:off
				mutationService.persist(Mutation.MutationBuilder
						.create(AccountNumber.R449937763, Currency.EUR)
						.setBalanceBefore(1250)
						.setBalanceAfter(1300)
						.setAmount(50)
						.setInterestAndTransactionDate(LocalDate.now())
						.setDescription("50 erbij 1250 ==> 1300")
						.setRemark("Met een opmerking...")
						.getMutation());
		// @formatter:on
	}

	private void insertSimpleMutations() {
		try {
			importService.importFromFile(Paths.get("/home/mark/tmp/account/test.pdf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void insertMutations() {
		// @formatter:off
				mutationService.persist(Mutation.MutationBuilder
						.create(AccountNumber.R449937763, Currency.EUR)
						.setBalanceBefore(1250)
						.setBalanceAfter(1300)
						.setAmount(50)
						.setInterestAndTransactionDate(LocalDate.now())
						.setDescription("50 erbij 1250 ==> 1300")
						.setRemark("Met een opmerking...1")
						.add()
						.setAmount(200)
						.setInterestAndTransactionDate(LocalDate.now())
						.setDescription("200 erbij 1300 ==> 1500")
						.setRemark("Met een opmerking...2")
						.add()
						.setAmount(200)
						.setInterestAndTransactionDate(LocalDate.now())
						.setDescription("200 erbij 1300 ==> 1500")
						.setRemark("Met een opmerking...3")
						.getMutations());
				// @formatter:on
	}

	private void getMutations() {
		mutationService.getMutations().forEach(System.out::println);
	}

	private void addFile() {
		mutationFileService.persist("/home/mark/tmp/account", "test.txt");
	}

	private void getMutationFileByAbsoluteFilePath() {
		// mutationFileService.getMutationFileByAbsoluteFilePath("/home/mark/tmp/account", "test.txt").ifPresent(this::showMutationFile);
		mutationFileService.getMutationFileById(6).ifPresent(this::showMutationFile);
	}

	private void showMutationFile(MutationFile mutationFile) {
		System.out.println(mutationFile.getAbsoluteFilePath());
		System.out.println(new String(mutationFile.getFileBytes(), StandardCharsets.UTF_8));

	}
}

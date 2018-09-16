package nl.smith.account.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import nl.smith.account.AbstractTest;

@Transactional
public class ImportServiceTest extends AbstractTest {

	@Autowired
	private ImportService importService;

	@Autowired
	private MutationService mutationService;

	@Test
	public void importFromTABFile() throws IOException {
		assertThat(importService.importFromFile(Paths.get("/home/mark/temp/account/test.TAB")), is(787));
		assertThat(mutationService.getMutations().size(), is(787));
	}

	@Test
	public void importFromPdfFile() throws IOException {
		assertThat(importService.importFromFile(Paths.get("/home/mark/temp/account/test.pdf")), is(787));

	}

}

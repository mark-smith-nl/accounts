package nl.smith.account.development;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import nl.smith.account.domain.RawMutation;
import nl.smith.account.domain.RawMutation.RawMutationBuilder;
import nl.smith.account.domain.RawMutation.RawMutationBuilder.StepFinal;
import nl.smith.account.enums.persisted.AccountNumber;

public abstract class Main {

	private static final String PATTERN_PAGE_BODY_START = "Datum Omschrijving Bedrag af Bedrag bij\n";

	private static final Pattern PATTERN_START_MUTATION_RECORD = Pattern.compile("\\d{2}\\-\\d{2}\\-\\d{4}");

	private static final Pattern PATTERN_END_MUTATION_RECORD = Pattern.compile("\\d{1,}\\,\\d{2}$");

	private static Stack<RawMutation> processPage(String page, int pageNumber, AccountNumber accountNumber) {
		String body = page.split(PATTERN_PAGE_BODY_START)[1];
		String[] lines = body.split("\n");

		boolean startRecord = false;
		boolean insertRecord = false;

		String transactionDate = null;
		String description = null;
		String amount = null;

		StepFinal stepFinal = null;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (!startRecord) {
				Matcher matcherStart = PATTERN_START_MUTATION_RECORD.matcher(line);
				if (matcherStart.find()) {
					transactionDate = matcherStart.group();
					Matcher matcherEnd = PATTERN_END_MUTATION_RECORD.matcher(line);
					if (matcherEnd.find()) {
						amount = matcherEnd.group();
						description = line.substring(matcherStart.end(), matcherEnd.start()).trim();
						insertRecord = true;
					} else {
						startRecord = true;
						description = line.substring(matcherStart.end()).trim();
					}
				}
			} else {
				Matcher matcherEnd = PATTERN_END_MUTATION_RECORD.matcher(line);
				if (matcherEnd.find()) {
					amount = matcherEnd.group();
					startRecord = false;
					insertRecord = true;
				} else {
					description += " " + line.trim();
				}
			}

			if (insertRecord == true) {
				if (stepFinal == null) {
					stepFinal = RawMutationBuilder.create(accountNumber, pageNumber).setAmount(amount).setTransactionDate(transactionDate).setDescription(description);
				} else {
					stepFinal.add().setAmount(amount).setTransactionDate(transactionDate).setDescription(description);
				}
				insertRecord = false;
			}

		}

		return stepFinal.getRawMutations();
	}

	public static void main(String[] args) throws IOException {
		String text = null;
		PDDocument document = PDDocument.load(new File("/home/mark/tmp/account/test.pdf"));
		if (!document.isEncrypted()) {
			PDFTextStripper stripper = new PDFTextStripper();
			text = stripper.getText(document);
		}
		document.close();

		String[] pages = text.split("Bij- en afschrijvingen\n");

		Map<String, String> metaInfo = getMetaInfo(pages[1]);
		AccountNumber accountNumber = AccountNumber.valueOf(metaInfo.get("accountNumber"));
		for (int i = 1; i < pages.length - 1; i++) {
			processPage(pages[i], i, accountNumber).forEach(System.out::println);
		}
	}

	private static Map<String, String> getMetaInfo(String page) {
		Map<String, String> metaInfo = new HashMap<>();

		String header = page.split(PATTERN_PAGE_BODY_START)[0];

		Matcher matcher = Pattern.compile("Priverekening (.*)").matcher(header);
		if (matcher.find()) {
			metaInfo.put("accountNumber", "R" + matcher.group(1).replaceAll("\\.", ""));
		} else {
			throw new IllegalArgumentException();
		}

		matcher = Pattern.compile("Periode (\\d{2}-\\d{2}-\\d{4}) t/m (\\d{2}-\\d{2}-\\d{4})").matcher(header);
		if (matcher.find()) {
			metaInfo.put("periodFrom", matcher.group(1));
			metaInfo.put("periodTo", matcher.group(2));
		} else {
			throw new IllegalArgumentException();
		}

		matcher = Pattern.compile("(\\d+) (\\d+)").matcher(header);
		if (matcher.find()) {
			metaInfo.put("numberCredits", matcher.group(1));
			metaInfo.put("numberDebets", matcher.group(2));
		} else {
			throw new IllegalArgumentException();
		}

		matcher = Pattern.compile("Pagina 1 van (\\d+)").matcher(header);
		if (matcher.find()) {
			metaInfo.put("numberOfPages", matcher.group(1));
		} else {
			throw new IllegalArgumentException();
		}

		return metaInfo;
	}

}

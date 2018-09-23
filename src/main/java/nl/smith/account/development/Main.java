package nl.smith.account.development;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import nl.smith.account.domain.SimpleMutation;
import nl.smith.account.domain.SimpleMutation.SimpleMutationBuilder;
import nl.smith.account.domain.SimpleMutation.SimpleMutationBuilder.StepFinal;
import nl.smith.account.enums.persisted.AccountNumber;

public abstract class Main {

	private static final String PDF_PAGE_BEGIN = "Bij- en afschrijvingen\n";

	private static final String PDF_PAGE_BODY_START = "Datum Omschrijving Bedrag af Bedrag bij\n";

	private static final Pattern PDF_META_INFO_PATTERN_ACCOUNT_NUMBER = Pattern.compile("Priverekening (.*)");

	private static final Pattern PDF_META_INFO_PATTERN_FROM_TO = Pattern.compile("Periode (\\d{2}-\\d{2}-\\d{4}) t/m (\\d{2}-\\d{2}-\\d{4})");

	private static final Pattern PDF_META_INFO_PATTERN_NUMBER_OFF_CREDITS_DEBETS = Pattern.compile("(\\d+) (\\d+)");

	private static final Pattern PDF_META_INFO_PATTERN_NUMBER_OFF_PAGES = Pattern.compile("Pagina 1 van (\\d+)");

	private static final Pattern PDF_PATTERN_START_MUTATION_RECORD = Pattern.compile("\\d{2}\\-\\d{2}\\-\\d{4}");

	private static final Pattern PDF_PATTERN_END_MUTATION_RECORD = Pattern.compile("\\d{1,}\\,\\d{2}$");

	public static void main(String[] args) throws IOException {
		String text = null;

		PDDocument document = PDDocument.load(new File("/home/mark/tmp/account/test.pdf"));

		if (!document.isEncrypted()) {
			PDFTextStripper stripper = new PDFTextStripper();
			text = stripper.getText(document);
		}
		document.close();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<String> pages = new ArrayList(List.of(text.split(PDF_PAGE_BEGIN)));
		pages.remove(0);
		int numberOfExtractedPages = pages.size();
		Map<String, Object> metaInfo = getMetaInfo(pages.get(0));

		if (numberOfExtractedPages != (int) metaInfo.get("numberOfPages")) {
			throw new IllegalArgumentException(
					String.format("\nWrong number of pages in PDF.\nExpected:\t%d.\nExtracted:\t%d.", metaInfo.get("numberOfPages"), numberOfExtractedPages));
		}

		List<SimpleMutation> simpleMutations = new ArrayList<>();
		int extractedNumberOfMutations = 0;
		AccountNumber accountNumber = AccountNumber.valueOf((String) metaInfo.get("accountNumber"));
		for (int i = 0; i < numberOfExtractedPages; i++) {
			Stack<SimpleMutation> simpleMutationsFromPdfPage = processPdfPage(pages.get(i), i + 1, accountNumber);
			simpleMutations.addAll(simpleMutationsFromPdfPage);
			extractedNumberOfMutations += simpleMutationsFromPdfPage.size();
		}

		simpleMutations.forEach(System.out::println);

		int numberOfExpectedSimpleMutations = (int) metaInfo.get("numberCredits") + (int) metaInfo.get("numberDebets");
		if (numberOfExpectedSimpleMutations != extractedNumberOfMutations) {
			throw new IllegalArgumentException(String.format("\nWrong number of simple (raw) mutations in PDF.\nExpected:\t%d.\nExtracted:\t%d.", numberOfExpectedSimpleMutations,
					extractedNumberOfMutations));
		}

		simpleMutations.forEach(System.out::println);

	}

	/** Method to retrieve the following meta data from the first PDF page:
	 * <ul>
	 * <li>accountNumber</li>
	 * <li>periodFrom</li>
	 * <li>periodTo</li>
	 * <li>numberCredits</li>
	 * <li>numberDebets</li>
	 * <li>numberOfPages</li>
	 * </ul>
	 * Method throws an IllegalArgumentException if one if these values can not be obtained.
	 */
	private static Map<String, Object> getMetaInfo(String firstPage) {
		Map<String, Object> metaInfo = new HashMap<>();

		String header = firstPage.split(PDF_PAGE_BODY_START)[0];

		Matcher matcher = PDF_META_INFO_PATTERN_ACCOUNT_NUMBER.matcher(header);
		if (matcher.find()) {
			metaInfo.put("accountNumber", "R" + matcher.group(1).replaceAll("\\.", ""));
		} else {
			throw new IllegalArgumentException();
		}

		matcher = PDF_META_INFO_PATTERN_FROM_TO.matcher(header);
		if (matcher.find()) {
			metaInfo.put("periodFrom", matcher.group(1));
			metaInfo.put("periodTo", matcher.group(2));
		} else {
			throw new IllegalArgumentException();
		}

		matcher = PDF_META_INFO_PATTERN_NUMBER_OFF_CREDITS_DEBETS.matcher(header);
		if (matcher.find()) {
			metaInfo.put("numberCredits", Integer.valueOf(matcher.group(1)));
			metaInfo.put("numberDebets", Integer.valueOf(matcher.group(2)));
		} else {
			throw new IllegalArgumentException();
		}

		matcher = PDF_META_INFO_PATTERN_NUMBER_OFF_PAGES.matcher(header);
		if (matcher.find()) {
			metaInfo.put("numberOfPages", Integer.valueOf(matcher.group(1)));
		} else {
			throw new IllegalArgumentException();
		}

		return metaInfo;
	}

	private static Stack<SimpleMutation> processPdfPage(String page, int pageNumber, AccountNumber accountNumber) {
		String[] pageElements = page.split(PDF_PAGE_BODY_START);
		if (pageElements.length < 2) {
			return new Stack<SimpleMutation>();
		}

		String body = page.split(PDF_PAGE_BODY_START)[1];
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
				Matcher matcherStart = PDF_PATTERN_START_MUTATION_RECORD.matcher(line);
				if (matcherStart.find()) {
					transactionDate = matcherStart.group();
					Matcher matcherEnd = PDF_PATTERN_END_MUTATION_RECORD.matcher(line);
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
				Matcher matcherEnd = PDF_PATTERN_END_MUTATION_RECORD.matcher(line);
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
					stepFinal = SimpleMutationBuilder.create(accountNumber, pageNumber).setAmount(amount).setTransactionDate(transactionDate).setDescription(description);
				} else {
					stepFinal.add().setAmount(amount).setTransactionDate(transactionDate).setDescription(description);
				}
				insertRecord = false;
			}

		}

		return stepFinal.getSimpleMutations();
	}
}

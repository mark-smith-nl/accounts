package nl.smith.account.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.smith.account.domain.Mutation;
import nl.smith.account.domain.SimpleMutation;
import nl.smith.account.domain.SimpleMutation.SimpleMutationBuilder;
import nl.smith.account.domain.SimpleMutation.SimpleMutationBuilder.StepFinal;
import nl.smith.account.enums.persisted.AccountNumber;

@Transactional
@Service
public class ImportService {

	private final static Logger LOGGER = LoggerFactory.getLogger(ImportService.class);

	private static final String PDF_PAGE_BEGIN = "Bij- en afschrijvingen\n";

	private static final String PDF_PAGE_BODY_START = "Datum Omschrijving Bedrag af Bedrag bij\n";

	private static final Pattern PDF_META_INFO_PATTERN_ACCOUNT_NUMBER = Pattern.compile("Priverekening (.*)");

	private static final Pattern PDF_META_INFO_PATTERN_FROM_TO = Pattern.compile("Periode (\\d{2}-\\d{2}-\\d{4}) t/m (\\d{2}-\\d{2}-\\d{4})");

	private static final Pattern PDF_META_INFO_PATTERN_NUMBER_OFF_CREDITS_DEBETS = Pattern.compile("(\\d+) (\\d+)");

	private static final Pattern PDF_META_INFO_PATTERN_NUMBER_OFF_PAGES = Pattern.compile("Pagina 1 van (\\d+)");

	private static final Pattern PDF_PATTERN_START_MUTATION_RECORD = Pattern.compile("\\d{2}\\-\\d{2}\\-\\d{4}");

	private static final Pattern PDF_PATTERN_END_MUTATION_RECORD = Pattern.compile("\\d{1,}\\,\\d{2}$");

	private final MutationService mutationService;

	private final List<Column> columns;

	private final Pattern pattern;

	public ImportService(MutationService mutationService) {
		this.mutationService = mutationService;

		columns = buildColumns();
		pattern = Pattern.compile(Column.getRegex(columns));
	}

	public void cleanDatabase() {
		mutationService.removeMutations();
	}

	public int importFromFile(Path input) throws IOException {
		LOGGER.info("Reading file {}", input.toString());

		if (input.toString().toUpperCase().endsWith("TAB")) {
			return importFromTABFile(input);
		} else if (input.toString().toUpperCase().endsWith("PDF")) {
			return importFromPdfFile(input);
		}

		throw new IllegalArgumentException(String.format("Unkwown filetype for file %s.", input.toString()));
	}

	private int importFromTABFile(Path input) throws IOException {
		List<String> records = Files.readAllLines(input);
		Stack<Mutation> mutations = new Stack<>();
		if (!records.isEmpty()) {
			LOGGER.info("Read {} mutation lines.", records.size());
			try {
				records.forEach(record -> getMutationFromStringAndAdd(mutations, record));
			} catch (IllegalArgumentException e) {
				LOGGER.warn(e.getMessage());
				return 0;
			}
		}

		LOGGER.info("All {} mutations are valid", mutations.size());

		mutationService.persist(mutations);

		return mutations.size();
	}

	private int importFromPdfFile(Path input) throws InvalidPasswordException, IOException {
		String text = null;

		PDDocument document = PDDocument.load(input.toFile());

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

		int extractedNumberOfMutations = 0;
		AccountNumber accountNumber = AccountNumber.valueOf((String) metaInfo.get("accountNumber"));
		for (int i = 0; i < numberOfExtractedPages; i++) {
			Stack<SimpleMutation> simpleMutationsFromPdfPage = processPdfPage(pages.get(i), i + 1, accountNumber);
			extractedNumberOfMutations += simpleMutationsFromPdfPage.size();
			mutationService.persistSimpleMutations(simpleMutationsFromPdfPage);
		}

		int numberOfExpectedSimpleMutations = (int) metaInfo.get("numberCredits") + (int) metaInfo.get("numberDebets");
		if (numberOfExpectedSimpleMutations != extractedNumberOfMutations) {
			throw new IllegalArgumentException(String.format("\nWrong number of simple (raw) mutations in PDF.\nExpected:\t%d.\nExtracted:\t%d.", numberOfExpectedSimpleMutations,
					extractedNumberOfMutations));
		}

		return extractedNumberOfMutations;
	}

	private void getMutationFromStringAndAdd(Stack<Mutation> mutations, String record) {
		Matcher matcher = pattern.matcher(record);

		if (matcher.matches()) {
			// @formatter:off
			mutations.add(Mutation.MutationBuilder.create("R" + matcher.group((int) columns.get(0).groupPosition), matcher.group((int) columns.get(1).groupPosition))
            .setBalanceBefore(matcher.group((int) columns.get(3).groupPosition))
            .setBalanceAfter(matcher.group((int) columns.get(4).groupPosition))
            .setAmount(matcher.group((int) columns.get(6).groupPosition))
            .setInterestDate(matcher.group((int) columns.get(5).groupPosition))
            .setTransactionDate(matcher.group((int) columns.get(2).groupPosition))
            .setDescription(matcher.group((int) columns.get(7).groupPosition))
            .getMutation());     
            // @formatter:on
		} else {
			throw new IllegalArgumentException(String.format("\nCould not parse line: %s\nIt does not comply to the regular expression '%s'.'", record, pattern.pattern()));
		}
	}

	private static List<Column> buildColumns() {
		List<Column> columns = new ArrayList<>();
		// @formatter:off
        columns.add(new Column("Rekeningnummer"  , 1, COLUMNTYPE.ACCOUNTNUMBER));
        columns.add(new Column("Muntsoort"       , 2, COLUMNTYPE.CURRENCY));
        columns.add(new Column("Transactiedatum" , 3, COLUMNTYPE.DATE));
        columns.add(new Column("Beginsaldo"      , 4, COLUMNTYPE.AMOUNT));
        columns.add(new Column("Eindsaldo"       , 5, COLUMNTYPE.AMOUNT));
        columns.add(new Column("Rentedatum"      , 6, COLUMNTYPE.DATE));
        columns.add(new Column("Transactiebedrag", 7, COLUMNTYPE.AMOUNT));
        columns.add(new Column("Omschrijving"    , 8, COLUMNTYPE.OMSCHRIJVING));
        // @formatter:on

		Collections.sort(columns, (c1, c2) -> c1.position.compareTo(c2.position));

		return columns;
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

	/** Processes a PDF page.
	 * Extracts raw mutation records. The type of transaction is unknown.
	 * @param page
	 * @param pageNumber
	 * @param accountNumber
	 * @return
	 */
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

	private enum COLUMNTYPE {
		ACCOUNTNUMBER("\\d{9}"),
		CURRENCY("[A-Z]{3}"),
		DATE("(2\\d{3})(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])"),
		AMOUNT("[\\+\\-]?\\d+,\\d{2}"),
		OMSCHRIJVING(".*");

		String regex;

		COLUMNTYPE(String regex) {
			this.regex = regex;
		}

	}

	private static class Column {
		private static final String REGEX_FIELDSEPERATOR = "\t";

		private final String name;

		private final Integer position;

		private COLUMNTYPE columnType;

		private long groupPosition;

		private Column(String name, Integer position, COLUMNTYPE columnType) {
			super();
			this.name = name;
			this.position = position;
			this.columnType = columnType;
		}

		private static String getRegex(List<Column> columns) {
			List<String> elements = new ArrayList<>();
			columns.forEach(column -> {
				LOGGER.info("Processing structure column '{}'.", column.name);
				long numberOfGroups = String.join("", elements).chars().filter(ch -> ch == '(').count();
				column.groupPosition = numberOfGroups + 1;
				elements.add("(" + column.columnType.regex + ")");

			});

			return String.join(REGEX_FIELDSEPERATOR, elements);
		}

	}
}

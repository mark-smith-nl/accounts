package nl.smith.account.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.smith.account.domain.Mutation;
import nl.smith.account.persistence.AccountMapper;

@Service
public class ImportService {

    @Autowired
    private AccountMapper accountMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(ImportService.class);

    public void importFromFile(String filePath) throws IOException {
        List<Column> columns = buildColumns();

        Pattern pattern = Pattern.compile(Column.getRegex(columns));
        Path path = Paths.get(filePath);
        List<String> records = Files.readAllLines(path);
        records.forEach(record -> process(pattern, columns, record));
    }

    private void process(Pattern pattern, List<Column> columns, String record) {
        Matcher matcher = pattern.matcher(record);
        if (matcher.matches()) {
         // @formatter:off
            Mutation mutation = Mutation.MutationBuilder.create()
            .setAccountNumber(matcher.group((int) columns.get(0).groupPosition))
            .setCurrency(matcher.group((int) columns.get(1).groupPosition))
            .setTransactionDate(matcher.group((int) columns.get(2).groupPosition))
            .setBalanceBefore(matcher.group((int) columns.get(3).groupPosition))
            .setBalanceAfter(matcher.group((int) columns.get(4).groupPosition))
            .setInterestDate(matcher.group((int) columns.get(5).groupPosition))
            .setAmount(matcher.group((int) columns.get(6).groupPosition))
            .setDescription(matcher.group((int) columns.get(7).groupPosition))
            .get();
             // @formatter:on

            accountMapper.persist(mutation);

        } else {
            LOGGER.info("Could not process: {}", record);
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
        private static final String REGEX_FIELDSEPERATOR = "\\t";

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
                long numberOfGroups = String.join("", elements).chars().filter(ch -> ch == '(').count();
                column.groupPosition = numberOfGroups + 1;
                elements.add("(" + column.columnType.regex + ")");

            });
            return String.join(REGEX_FIELDSEPERATOR, elements);

        }

    }
}

/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.convert.provider;

import walkingkooka.Binary;
import walkingkooka.Either;
import walkingkooka.collect.list.BooleanList;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.list.StringList;
import walkingkooka.collect.list.TsvStringList;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.CsvStringSet;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.TsvStringSet;
import walkingkooka.color.Color;
import walkingkooka.color.HslColor;
import walkingkooka.color.HsvColor;
import walkingkooka.color.RgbColor;
import walkingkooka.convert.Converter;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyValue;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.LocalDateList;
import walkingkooka.datetime.LocalDateTimeList;
import walkingkooka.datetime.LocalTimeList;
import walkingkooka.environment.AuditInfo;
import walkingkooka.locale.LocaleLanguageTag;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.NumberList;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasHostAddress;
import walkingkooka.net.MailToUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.MediaType;
import walkingkooka.predicate.Predicates;
import walkingkooka.props.Properties;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.convert.FakeSpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.form.SpreadsheetForms;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.spreadsheet.value.SpreadsheetValueType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.StorageValueInfoList;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonArray;
import walkingkooka.tree.json.JsonBoolean;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNumber;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.text.Border;
import walkingkooka.tree.text.BoxEdge;
import walkingkooka.tree.text.Flag;
import walkingkooka.tree.text.Hyperlink;
import walkingkooka.tree.text.Image;
import walkingkooka.tree.text.Margin;
import walkingkooka.tree.text.Padding;
import walkingkooka.tree.text.Styleable;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.ValidationCheckbox;
import walkingkooka.validation.ValidationChoice;
import walkingkooka.validation.ValidationChoiceList;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.ValidationErrorList;
import walkingkooka.validation.ValueType;
import walkingkooka.validation.form.FormName;
import walkingkooka.validation.provider.ValidatorSelector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Helper that may be used to validate a submitted {@link Converter} is able to convert required types.
 * Note if a {@link UnsupportedOperationException} is thrown, it will be rethrown and the converter test not marked as a
 * FAIL. This is useful particularly in tests which may be using a {@link FakeSpreadsheetConverterContext} with some
 * unimplemented methods.
 */
final class MissingConverterVerifier {

    private final static List<Class<?>> NUMBER_TYPES = Lists.of(
        ExpressionNumber.class,
        Byte.class,
        Short.class,
        Integer.class,
        Long.class,
        Float.class,
        Double.class,
        BigInteger.class,
        BigDecimal.class
    );

    static {
        final List<Class<?>> types = Lists.array();
        types.addAll(NUMBER_TYPES);
        types.remove(Byte.class);
        types.remove(Short.class);

        NUMBER_TYPES_WITHOUT_BYTE_SHORT = types;
    }

    private static final List<Class<?>> NUMBER_TYPES_WITHOUT_BYTE_SHORT;

    private final static LocalDate DATE = LocalDate.of(
        1999,
        12,
        31
    );

    static {
        final List<Class<?>> types = Lists.array();
        types.add(Boolean.class);
        types.add(LocalDateTime.class);
        types.addAll(NUMBER_TYPES);
        types.remove(Byte.class);
        types.remove(Short.class);
        types.add(String.class);

        DATE_TO_TYPES = types;
    }

    private static final List<Class<?>> DATE_TO_TYPES;

    // time component must be zero otherwise DateTime -> integer types like BigInteger will fail
    private final static LocalDateTime DATE_TIME = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58,
        0 // formatting patterns often dont include seconds.
    );

    static {
        final List<Class<?>> types = Lists.array();
        types.add(Boolean.class);
        types.add(LocalDate.class);
        types.add(LocalTime.class);
        types.addAll(NUMBER_TYPES);
        types.remove(Byte.class);
        types.remove(Short.class);
        types.add(String.class);

        DATE_TIME_TO_TYPES = types;
    }

    private static final List<Class<?>> DATE_TIME_TO_TYPES;


    private final static LocalTime TIME = DATE_TIME.toLocalTime();

    static {
        final List<Class<?>> types = Lists.array();
        types.add(Boolean.class);
        types.add(LocalDateTime.class);
        types.addAll(NUMBER_TYPES);
        types.remove(Byte.class);
        types.remove(Short.class);
        types.remove(BigInteger.class); // time -> BigInteger doesnt work
        types.add(String.class);

        TIME_TO_TYPES = types;
    }

    private static final List<Class<?>> TIME_TO_TYPES;

    // value constants..................................................................................................

    private final static BooleanList BOOLEAN_LIST = BooleanList.EMPTY.setElements(
        Lists.of(
            true,
            false
        )
    );

    private final static CsvStringList CSV_STRING_LIST = CsvStringList.EMPTY.setElements(
        Lists.of(
            "Apple 1",
            "Banana 2"
        )
    );

    private final static CsvStringSet CSV_STRING_SET = CsvStringSet.EMPTY.setElements(
        Sets.of(
            "Apple 1",
            "Banana 2"
        )
    );

    private final static LocalDateList LOCAL_DATE_LIST = LocalDateList.EMPTY.setElements(
        Lists.of(
            DATE,
            null
        )
    );

    private final static LocalDateTimeList LOCAL_DATE_TIME_LIST = LocalDateTimeList.EMPTY.setElements(
        Lists.of(
            DATE_TIME,
            null
        )
    );

    private final static LocalTimeList LOCAL_TIME_LIST = LocalTimeList.EMPTY.setElements(
        Lists.of(
            TIME,
            null
        )
    );

    private final static StringList STRING_LIST = StringList.EMPTY.setElements(
        Lists.of(
            "Apple 1",
            "Banana 2"
        )
    );

    private final static TsvStringList TSV_STRING_LIST = TsvStringList.EMPTY.setElements(
        Lists.of(
            "Apple 1",
            "Banana 2"
        )
    );

    private final static TsvStringSet TSV_STRING_SET = TsvStringSet.EMPTY.setElements(
        Sets.of(
            "Apple 1",
            "Banana 2"
        )
    );

    private final static Binary BINARY = Binary.with(
        "Hello World Binary".getBytes(StandardCharsets.UTF_8)
    );

    private final static Binary UNKNOWN_BINARY_FILE = Binary.with(
        new byte[] {
            0,
            1,
            2
        }
    );

    private final static String EXPRESSION_TEXT = "1+2";

    private final static JsonObject JSON_OBJECT = JsonNode.object()
        .set(
            JsonPropertyName.with("hello"),
            "world"
        );

    private final static Properties PROPERTIES = Properties.parse("hello=world");
    
    private final static SpreadsheetCellReference CELL = SpreadsheetSelection.A1;
    private final static SpreadsheetCellRangeReference CELL_RANGE = SpreadsheetSelection.parseCellRange("B2:C3");
    private final static SpreadsheetColumnReference COLUMN = SpreadsheetSelection.parseColumn("A");
    private final static SpreadsheetColumnRangeReference COLUMN_RANGE = SpreadsheetSelection.parseColumnRange("B:C");
    private final static SpreadsheetRowReference ROW = SpreadsheetSelection.parseRow("1");
    private final static SpreadsheetRowRangeReference ROW_RANGE = SpreadsheetSelection.parseRowRange("2:3");

    private final static SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName("Label123");
    private final static SpreadsheetError ERROR = SpreadsheetError.referenceNotFound(CELL);

    private final static SpreadsheetFormatterSelector FORMATTER_SELECTOR = SpreadsheetFormatterSelector.parse("test-formatter");
    private final static SpreadsheetParserSelector PARSER_SELECTOR = SpreadsheetParserSelector.parse("test-parser");
    private final static ValidatorSelector VALIDATOR_SELECTOR = ValidatorSelector.parse("test-validator");

    private final static AbsoluteUrl ABSOLUTE_URL = Url.parseAbsolute("https://example.com/123");
    private final static EmailAddress EMAIL_ADDRESS = EmailAddress.parse("user@example.com");
    private final static MailToUrl MAIL_TO_URL = Url.parseMailTo("mailto:user@example.com");
    private final static MediaType MEDIA_TYPE = MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8);
    private final static RelativeUrl RELATIVE_URL = Url.parseRelative("/path1/path2?k1=v1#fragment111");

    private final static TextStyle STYLE = TextStyle.EMPTY.set(
        TextStylePropertyName.COLOR,
        Color.BLACK
    );
    private final static Border BORDER = Border.parse("BLACK SOLID 1.0px");

    private final static Margin MARGIN = TextStyle.EMPTY.setMargin(
        Optional.of(
            Margin.parse("1px")
        )
    ).margin(BoxEdge.ALL);

    private final static Padding PADDING = TextStyle.EMPTY.setPadding(
        Optional.of(
            Padding.parse("1px")
        )
    ).padding(BoxEdge.ALL);

    private final static  SpreadsheetText SPREADSHEET_TEXT = SpreadsheetText.with("Text123");

    private final static Hyperlink HYPERLINK = Hyperlink.hyperlink(ABSOLUTE_URL);
    private final static Image IMAGE = Image.image(ABSOLUTE_URL);


    private final static LocaleLanguageTag LANGUAGE_TAG = LocaleLanguageTag.parse("en-AU");

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadata.EMPTY.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SpreadsheetId.with(1)
    ).set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
        SpreadsheetName.with("Spreadsheet111")
    );

    private final static StoragePath STORAGE_PATH = StoragePath.parse("/path1/file2.txt");

    private static final StoragePath STORAGE_PATH_BINARY = StoragePath.parse("/file.bin");

    private static final StoragePath STORAGE_PATH_CSV = StoragePath.parse("/file.csv");

    private static final StoragePath STORAGE_PATH_EXPRESSION = StoragePath.parse("/formula.expression.txt");

    private static final StoragePath STORAGE_PATH_TXT = StoragePath.parse("/file1.file2.txt");

    private static final StoragePath STORAGE_PATH_JSON = StoragePath.parse("/file1/file2.json");

    private static final StoragePath STORAGE_PATH_PROPERTIES = StoragePath.parse("/file1/file2.properties");

    private static final StoragePath STORAGE_PATH_TSV = StoragePath.parse("/file.tsv");

    private static final StorageValue STORAGE_VALUE_BINARY = StorageValue.with(STORAGE_PATH_BINARY)
        .setValue(
            Optional.of(BINARY)
        );

    private static final StorageBinary STORAGE_BINARY_BINARY = StorageBinary.with(
        STORAGE_PATH_BINARY,
        BINARY
    );

    private final static AuditInfo AUDIT_INFO = AuditInfo.create(
        EMAIL_ADDRESS,
        DATE_TIME
    );

    private final static Predicate<Object> IS_ABSOLUTE_URL = v -> v instanceof AbsoluteUrl;

    private final static Predicate<Object> IS_BINARY = v -> v instanceof Binary;

    private final static Predicate<Object> IS_BOOLEAN_LIST = v -> v instanceof BooleanList;

    private final static Predicate<Object> IS_CELL_REFERENCE = v -> v instanceof SpreadsheetCellReference;

    private final static Predicate<Object> IS_CELL_RANGE_REFERENCE = v -> v instanceof SpreadsheetCellRangeReference;

    private final static Predicate<Object> IS_COLOR = v -> v instanceof Color;

    private final static Predicate<Object> IS_COLUMN_REFERENCE = v -> v instanceof SpreadsheetColumnReference;

    private final static Predicate<Object> IS_COLUMN_RANGE_REFERENCE = v -> v instanceof SpreadsheetColumnRangeReference;

    private final static Predicate<Object> IS_CSV_STRING_LIST = v -> v instanceof CsvStringList;

    private final static Predicate<Object> IS_CSV_STRING_SET = v -> v instanceof CsvStringSet;

    private final static Predicate<Object> IS_CURRENCY = v -> v instanceof Currency;

    private final static Predicate<Object> IS_CURRENCY_CODE = v -> v instanceof CurrencyCode;

    private final static Predicate<Object> IS_CURRENCY_VALUE = v -> v instanceof CurrencyValue;

    private final static Predicate<Object> IS_DATE = v -> v instanceof LocalDate;

    private final static Predicate<Object> IS_DATE_TIME = v -> v instanceof LocalDateTime;

    private final static Predicate<Object> IS_DATE_TIME_SYMBOLS = v -> v instanceof DateTimeSymbols;

    private final static Predicate<Object> IS_DECIMAL_NUMBER_SYMBOLS = v -> v instanceof DecimalNumberSymbols;

    private final static Predicate<Object> IS_EMAIL_ADDRESS = v -> v instanceof EmailAddress;

    private final static Predicate<Object> IS_ERROR = v -> v instanceof SpreadsheetError;

    private final static Predicate<Object> IS_EXPRESSION = v -> v instanceof Expression;

    private final static Predicate<Object> IS_FORM_NAME = v -> v instanceof FormName;

    private final static Predicate<Object> IS_HYPERLINK = v -> v instanceof Hyperlink;

    private final static Predicate<Object> IS_IMAGE = v -> v instanceof Image;

    private final static Predicate<Object> IS_INDENTATION = v -> v instanceof Indentation;

    private final static Predicate<Object> IS_JSON = v -> v instanceof JsonNode;
    ;
    private final static Predicate<Object> IS_LOCAL_DATE_LIST = v -> v instanceof LocalDateList;

    private final static Predicate<Object> IS_LOCAL_DATE_TIME_LIST = v -> v instanceof LocalDateTimeList;

    private final static Predicate<Object> IS_LOCAL_TIME_LIST = v -> v instanceof LocalTimeList;

    private final static Predicate<Object> IS_LINE_ENDING = v -> v instanceof LineEnding;

    private final static Predicate<Object> IS_MAIL_TO_URL = v -> v instanceof MailToUrl;

    private final static Predicate<Object> IS_MEDIA_TYPE = v -> v instanceof MediaType;

    private final static Predicate<Object> IS_NOT_NULL = v -> null != v;

    private final static Predicate<Object> IS_NULL = v -> null == v;

    private final static Predicate<Object> IS_NUMBER = v -> v instanceof Number;

    private final static Predicate<Object> IS_NUMBER_LIST = v -> v instanceof NumberList;

    private final static Predicate<Object> IS_ROW_RANGE_REFERENCE = v -> v instanceof SpreadsheetRowRangeReference;

    private final static Predicate<Object> IS_SPREADSHEET_COLOR_NAME = v -> v instanceof SpreadsheetColorName;

    private final static Predicate<Object> IS_STORAGE_PATH = v -> v instanceof StoragePath;

    private final static Predicate<Object> IS_STORAGE_VALUE = v -> v instanceof StorageValue;

    private final static Predicate<Object> IS_STRING = v -> v instanceof String;

    private final static Predicate<Object> IS_STRING_LIST = v -> v instanceof StringList;

    private final static Predicate<Object> IS_STYLE = v -> v instanceof TextStyle;

    private final static Predicate<Object> IS_TEXT_NODE = v -> v instanceof TextNode;

    private final static Predicate<Object> IS_TIME = v -> v instanceof LocalTime;

    private final static Predicate<Object> IS_TSV_STRING_LIST = v -> v instanceof TsvStringList;

    private final static Predicate<Object> IS_TSV_STRING_SET = v -> v instanceof TsvStringSet;

    private final static Predicate<Object> IS_URL = v -> v instanceof Url;

    private final static Predicate<Object> IS_VALIDATION_CHECKBOX = v -> v instanceof ValidationCheckbox;

    private final static Predicate<Object> IS_VALIDATION_CHOICE = v -> v instanceof ValidationChoice;

    private final static Predicate<Object> IS_VALIDATION_CHOICE_LIST = v -> v instanceof ValidationChoiceList;

    private final static Predicate<Object> IS_VALIDATION_ERROR = v -> v instanceof ValidationError;

    private final static Predicate<Object> IS_VALIDATION_ERROR_LIST = v -> v instanceof ValidationErrorList;
        
    /**
     * Note no tests actually involve converting {@link CharSequence} to something else, because marshalling
     * does not support the {@link CharSequence} interface types like {@link StringBuilder} etc.
     */
    static Set<MissingConverter> verify(final Converter<SpreadsheetConverterContext> converter,
                                        final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName,
                                        final SpreadsheetConverterContext context) {
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(propertyName, "propertyName");
        Objects.requireNonNull(context, "context");

        final boolean formatting = SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER.equals(propertyName);
        final boolean formula = SpreadsheetMetadataPropertyName.FORMULA_CONVERTER.equals(propertyName);
        final boolean query = SpreadsheetMetadataPropertyName.QUERY_CONVERTER.equals(propertyName);
        final boolean scripting = SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER.equals(propertyName);
        final boolean sort = SpreadsheetMetadataPropertyName.SORT_CONVERTER.equals(propertyName);
        final boolean validation = SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER.equals(propertyName);

        final MissingConverterVerifier verifier = new MissingConverterVerifier(
            converter,
            context
        );

        final Charset charset = context.charset();

        final ExpressionNumberKind kind = context.expressionNumberKind();

        final Locale locale = context.localeForLanguageTagOrFail(LANGUAGE_TAG);

        final SpreadsheetCell spreadsheetCell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1+2+3")
        ).setDateTimeSymbols(
            Optional.of(
                context.dateTimeSymbols()
            )
        ).setDecimalNumberSymbols(
            Optional.of(
                context.decimalNumberSymbols()
            )
        ).setFormatter(
            Optional.of(FORMATTER_SELECTOR)
        ).setParser(
            Optional.of(PARSER_SELECTOR)
        ).setValidator(
            Optional.of(VALIDATOR_SELECTOR)
        ).setLocale(
            Optional.of(locale)
        ).setStyle(STYLE);

        // basic........................................................................................................
        verifier.addIfConversionFail(
             null,
            Object.class,
            SpreadsheetConvertersConverterProvider.BASIC
        );

        verifier.addIfConversionFail(
                1,
            Object.class,
            SpreadsheetConvertersConverterProvider.BASIC
        );

        verifier.addIfConversionFail(
            spreadsheetCell,
            Object.class,
            SpreadsheetConvertersConverterProvider.BASIC
        );

        // boolean......................................................................................................
        verifier.addIfConversionFail(
            Lists.of(
                0,
                kind.zero(),
                SpreadsheetStrings.BOOLEAN_FALSE
            ),
            Boolean.class,
            SpreadsheetConvertersConverterProvider.BOOLEAN,
            false
        );

        verifier.addIfConversionFail(
            Lists.of(
                1,
                kind.one(),
                SpreadsheetStrings.BOOLEAN_TRUE
            ),
            Boolean.class,
            SpreadsheetConvertersConverterProvider.BOOLEAN,
            true
        );

        verifier.addIfConversionFail(
            Lists.of(
                SpreadsheetStrings.BOOLEAN_TRUE,
                SpreadsheetStrings.BOOLEAN_FALSE
            ),
            String.class,
            SpreadsheetConvertersConverterProvider.BOOLEAN,
            IS_STRING
        );

        // collection-to-list...........................................................................................
        {
            verifier.addIfConversionFail(
                (Object)
                    new ArrayList<>(BOOLEAN_LIST),
                BooleanList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST, // COLLECTION_TO_LIST
                BOOLEAN_LIST
            );

            verifier.addIfConversionFail(
                (Object)
                    new ArrayList<>(CSV_STRING_LIST),
                CsvStringList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST, // COLLECTION_TO_LIST
                CSV_STRING_LIST
            );

            verifier.addIfConversionFail(
                (Object)
                    new ArrayList<>(LOCAL_DATE_LIST),
                LocalDateList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST, // COLLECTION_TO_LIST
                LOCAL_DATE_LIST
            );

            verifier.addIfConversionFail(
                (Object)
                    new ArrayList<>(LOCAL_DATE_TIME_LIST),
                LocalDateTimeList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST, // COLLECTION_TO_LIST
                LOCAL_DATE_TIME_LIST
            );

            verifier.addIfConversionFail(
                (Object)
                    new ArrayList<>(LOCAL_TIME_LIST),
                LocalTimeList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST, // COLLECTION_TO_LIST
                LOCAL_TIME_LIST
            );

            {
                final List<Number> numberList = Lists.of(
                    kind.create(1),
                    kind.create(22),
                    kind.create(333.5),
                    null
                );
                verifier.addIfConversionFail(
                    numberList,
                    NumberList.class,
                    SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST, // COLLECTION_TO_LIST
                    IS_NUMBER_LIST
                );
            }

            verifier.addIfConversionFail(
                (Object)
                    new ArrayList<>(STRING_LIST),
                StringList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST, // COLLECTION_TO_LIST
                STRING_LIST
            );
        }

        // collection-to................................................................................................
        {
            verifier.addIfConversionFail(
                (Object)
                    Lists.of(
                    false
                    ),
                Boolean.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO, // COLLECTION_TO
                false
            );

            verifier.addIfConversionFail(
                (Object)
                    Lists.of(
                    true
                    ),
                Boolean.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO, // COLLECTION_TO
                true
            );

            verifier.addIfConversionFail(
                (Object)
                    Lists.of(
                    123
                    ),
                NUMBER_TYPES,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO, // COLLECTION_TO
                IS_NUMBER
            );

            verifier.addIfConversionFail(
                (Object)
                    Lists.of(DATE),
                LocalDate.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO, // COLLECTION_TO
                DATE
            );

            verifier.addIfConversionFail(
                (Object)
                    Lists.of(DATE_TIME),
                LocalDateTime.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO, // COLLECTION_TO
                DATE_TIME
            );

            verifier.addIfConversionFail(
                (Object)
                    Lists.of(
                    "Hello"
                    ),
                String.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO, // COLLECTION_TO
                "[Hello]"
            );

            verifier.addIfConversionFail(
                (Object)
                    Lists.of(TIME),
                LocalTime.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO, // COLLECTION_TO
                TIME
            );
        }

        // color........................................................................................................
        {
            // color-to-color...........................................................................................
            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    Color.BLACK,
                    Lists.of(
                        HslColor.class,
                        HsvColor.class,
                        RgbColor.class
                    ),
                    SpreadsheetConvertersConverterProvider.COLOR, // COLOR_TO_COLOR
                    IS_COLOR
                );
            }

            // color-to-number..........................................................................................
            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    Color.BLACK,
                    NUMBER_TYPES_WITHOUT_BYTE_SHORT,
                    SpreadsheetConvertersConverterProvider.COLOR, // COLOR_TO_NUMBER
                    IS_NUMBER
                );

                final RgbColor rgb = Color.parseRgb("#12345678");

                verifier.addIfConversionFail(
                    rgb.alpha(),
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.COLOR, // COLOR_TO_NUMBER
                    IS_NUMBER
                );

                verifier.addIfConversionFail(
                    rgb.red(),
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.COLOR, // COLOR_TO_NUMBER
                    IS_NUMBER
                );

                verifier.addIfConversionFail(
                    rgb.green(),
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.COLOR, // COLOR_TO_NUMBER
                    IS_NUMBER
                );

                verifier.addIfConversionFail(
                    rgb.blue(),
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.COLOR, // COLOR_TO_NUMBER
                    IS_NUMBER
                );
            }

            // text-to-color............................................................................................
            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    "#123456",
                    Color.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_COLOR,
                    IS_COLOR
                );
            }

            // text-to-spreadsheet-color-name...........................................................................
            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    SpreadsheetColorName.BLACK.value(),
                    SpreadsheetColorName.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_COLOR_NAME,
                    IS_SPREADSHEET_COLOR_NAME
                );
            }
        }

        // currency.....................................................................................................
        {
            if (formatting || formula || scripting) {
                final CurrencyCode currencyCode = context.currencyCode();

                verifier.addIfConversionFail(
                    currencyCode.value(),
                    Currency.class,
                    SpreadsheetConvertersConverterProvider.CURRENCY, // text-to-currency
                    IS_CURRENCY
                );

                verifier.addIfConversionFail(
                    currencyCode.value(),
                    CurrencyCode.class,
                    SpreadsheetConvertersConverterProvider.CURRENCY, // text-to-currency-code
                    IS_CURRENCY_CODE
                );

                verifier.addIfConversionFail(
                    CurrencyValue.with(
                        1,
                        currencyCode
                    ),
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.CURRENCY, // currency-value-to-number
                    IS_NUMBER
                );

                verifier.addIfConversionFail(
                    CurrencyValue.with(
                        2L,
                        currencyCode
                    ),
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.CURRENCY, // currency-value-to-number
                    IS_NUMBER
                );

                verifier.addIfConversionFail(
                    CurrencyValue.with(
                        3.0f,
                        currencyCode
                    ),
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.CURRENCY, // currency-value-to-number
                    IS_NUMBER
                );

                verifier.addIfConversionFail(
                    CurrencyValue.with(
                        4.0f,
                        currencyCode
                    ),
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.CURRENCY, // currency-value-to-number
                    IS_NUMBER
                );

                verifier.addIfConversionFail(
                    Lists.of(
                        "AUD 1.5",
                        "1.5 AUD",
                        "1.5"
                    ),
                    CurrencyValue.class,
                    SpreadsheetConvertersConverterProvider.CURRENCY, // text-to-currency-value
                    IS_CURRENCY_VALUE
                );

                verifier.addIfConversionFail(
                    currencyCode,
                    Currency.class,
                    SpreadsheetConvertersConverterProvider.CURRENCY, // currency-code-to-currency
                    IS_CURRENCY
                );

                verifier.addIfConversionFail(
                    Lists.of(
                        1,
                        1.5,
                        ExpressionNumberKind.DEFAULT.create(2.75)
                    ),
                    CurrencyValue.class,
                    SpreadsheetConvertersConverterProvider.CURRENCY, // number-to-currency-value
                    IS_CURRENCY_VALUE
                );
            }
        }

        // date-time....................................................................................................
        {
            // date -> dateTime & number & text & time
            verifier.addIfConversionFail(
                DATE, // date
                DATE_TO_TYPES,
                SpreadsheetConvertersConverterProvider.DATE_TIME,
                IS_NOT_NULL
            );

            // datetime ->
            verifier.addIfConversionFail(
                LocalDateTime.of(
                    DATE,
                    LocalTime.MIDNIGHT
                ), // date
                DATE_TIME_TO_TYPES,
                SpreadsheetConvertersConverterProvider.DATE_TIME,
                IS_NOT_NULL
            );

            // time ->
            verifier.addIfConversionFail(
                TIME,
                TIME_TO_TYPES,
                SpreadsheetConvertersConverterProvider.DATE_TIME,
                IS_NOT_NULL
            );

            // text -> number
            verifier.addIfConversionFail(
                "123",
                NUMBER_TYPES,
                SpreadsheetConvertersConverterProvider.DATE_TIME,
                IS_NUMBER
            );

            // text -> date
            verifier.addIfConversionFail(
                "1999/12/31",
                LocalDate.class,
                SpreadsheetConvertersConverterProvider.DATE_TIME,
                IS_DATE
            );

            // text -> dateTime
            verifier.addIfConversionFail(
                "1999/12/31 12:58",
                LocalDateTime.class,
                SpreadsheetConvertersConverterProvider.DATE_TIME,
                DATE_TIME
            );

            // text -> time
            verifier.addIfConversionFail(
                "12:58:59", // "12:58" fails
                LocalTime.class,
                SpreadsheetConvertersConverterProvider.DATE_TIME,
                IS_TIME
            );
        }

        // date-time-symbols............................................................................................
        {
            final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(locale)
            );

            verifier.addIfConversionFail(
                locale,
                DateTimeSymbols.class,
                SpreadsheetConvertersConverterProvider.DATE_TIME_SYMBOLS, // DATE_TIME_SYMBOLS
                IS_DATE_TIME_SYMBOLS
            );

            verifier.addIfConversionFail(
                spreadsheetCell,
                DateTimeSymbols.class,
                SpreadsheetConvertersConverterProvider.DATE_TIME_SYMBOLS, // DATE_TIME_SYMBOLS
                IS_DATE_TIME_SYMBOLS
            );

            verifier.addIfConversionFail(
                dateTimeSymbols.properties(),
                DateTimeSymbols.class,
                SpreadsheetConvertersConverterProvider.DATE_TIME_SYMBOLS, // DATE_TIME_SYMBOLS
                IS_DATE_TIME_SYMBOLS
            );
        }

        // decimal-number-symbols............................................................................................
        {
            final DecimalNumberSymbols decimalNumberSymbols = DecimalNumberSymbols.fromDecimalFormatSymbols(
                '+',
                new DecimalFormatSymbols(
                    locale
                )
            );

            verifier.addIfConversionFail(
                spreadsheetCell,
                DecimalNumberSymbols.class,
                SpreadsheetConvertersConverterProvider.DECIMAL_NUMBER_SYMBOLS, // DECIMAL_NUMBER_SYMBOLS
                spreadsheetCell.decimalNumberSymbols()
                    .get()
            );

            verifier.addIfConversionFail(
                locale,
                DecimalNumberSymbols.class,
                SpreadsheetConvertersConverterProvider.DECIMAL_NUMBER_SYMBOLS, // DECIMAL_NUMBER_SYMBOLS
                IS_DECIMAL_NUMBER_SYMBOLS
            );

            verifier.addIfConversionFail(
                decimalNumberSymbols.properties(),
                DecimalNumberSymbols.class,
                SpreadsheetConvertersConverterProvider.DECIMAL_NUMBER_SYMBOLS, // DECIMAL_NUMBER_SYMBOLS
                IS_DECIMAL_NUMBER_SYMBOLS
            );
        }

        // environment..................................................................................................
        {
            if (formula || scripting) {
                verifier.addIfConversionFail(
                    ERROR,
                    SpreadsheetError.class,
                    SpreadsheetConvertersConverterProvider.ENVIRONMENT // TEXT_TO_ENVIRONMENT_VALUE_NAME
                );
            }
        }

        // error-throwing...............................................................................................
        if (formula) {
            verifier.addIfConversionFail(
                ERROR,
                ExpressionNumber.class,
                SpreadsheetConvertersConverterProvider.ERROR_THROWING,
                context.expressionNumberKind()
                    .zero()
            );
        }

        // expression...................................................................................................
        {
            if (query || formatting || formula || scripting || validation) {
                verifier.addIfConversionFail(
                    "1+sum(2)",
                    Expression.class,
                    SpreadsheetConvertersConverterProvider.EXPRESSION,
                    IS_EXPRESSION
                );
            }
        }

        // json.........................................................................................................
        {
            // to-json..................................................................................................
            if (formula || scripting | validation) {
                verifier.addIfConversionFail(
                    "Hello",
                    Lists.of(
                        JsonNode.class,
                        JsonArray.class,
                        JsonBoolean.class,
                        JsonNumber.class,
                        JsonString.class,
                        JsonObject.class
                    ),
                    SpreadsheetConvertersConverterProvider.JSON, // TO_JSON,
                    IS_JSON
                );
            }

            if (formula || scripting) {
                verifier.addIfConversionFail(
                    context.marshall(spreadsheetCell)
                        .toString(),
                    SpreadsheetCell.class,
                    SpreadsheetConvertersConverterProvider.JSON, // textToObject
                    spreadsheetCell
                );
            }
        }

        // locale.......................................................................................................
        {
            if (formatting || formula || query || scripting) {
                verifier.addIfConversionFail(
                    Lists.of(
                        locale.toLanguageTag(),
                        locale
                    ),
                    DateTimeSymbols.class,
                    SpreadsheetConvertersConverterProvider.LOCALE, // DATE_TIME_SYMBOLS
                    IS_DATE_TIME_SYMBOLS
                );
            }

            if (formatting || formula || query || scripting) {
                verifier.addIfConversionFail(
                    Lists.of(
                        locale.toLanguageTag(),
                        locale
                    ),
                    DecimalNumberSymbols.class,
                    SpreadsheetConvertersConverterProvider.LOCALE, // DECIMAL_NUMBER_SYMBOLS
                    IS_DECIMAL_NUMBER_SYMBOLS
                );
            }

            // text-to-locale...............................................................................................
            if (formula || query || scripting) {
                verifier.addIfConversionFail(
                    locale.toLanguageTag(),
                    Locale.class,
                    SpreadsheetConvertersConverterProvider.LOCALE, // TEXT_TO_LOCALE
                    locale
                );
            }

            if (formula || query || scripting) {
                verifier.addIfConversionFail(
                    Lists.of(
                        locale,
                        spreadsheetCell
                    ),
                    Locale.class,
                    SpreadsheetConvertersConverterProvider.LOCALE, // TEXT_TO_LOCALE
                    locale
                );
            }

            // text-to-locale-language-tag..............................................................................
            if (formula || query || scripting) {
                final String languageTag = locale.toLanguageTag();

                verifier.addIfConversionFail(
                    languageTag,
                    LocaleLanguageTag.class,
                    SpreadsheetConvertersConverterProvider.LOCALE, // text-to-locale-language-tag
                    LocaleLanguageTag.parse(languageTag)
                );
            }

            if (formula || query || scripting) {
                verifier.addIfConversionFail(
                    Lists.of(
                        locale,
                        spreadsheetCell
                    ),
                    LocaleLanguageTag.class,
                    SpreadsheetConvertersConverterProvider.LOCALE, // text-to-locale-language-tag
                    LocaleLanguageTag.fromLocale(locale)
                );
            }
        }

        // net..........................................................................................................
        {
            if (formula | query | scripting) {
                verifier.addIfConversionFail(
                    Lists.of(
                        ABSOLUTE_URL.text(),
                        MAIL_TO_URL.text(),
                        RELATIVE_URL.text()
                    ),
                    Url.class,
                    SpreadsheetConvertersConverterProvider.NET,
                    IS_URL
                );

                verifier.addIfConversionFail(
                    EMAIL_ADDRESS.text(),
                    EmailAddress.class,
                    SpreadsheetConvertersConverterProvider.NET,
                    IS_EMAIL_ADDRESS
                );

                verifier.addIfConversionFail(
                    MEDIA_TYPE.toString(),
                    MediaType.class,
                    SpreadsheetConvertersConverterProvider.NET,
                    IS_MEDIA_TYPE
                );

                verifier.addIfConversionFail(
                    ABSOLUTE_URL.text(),
                    HasHostAddress.class,
                    SpreadsheetConvertersConverterProvider.NET,
                    IS_ABSOLUTE_URL
                );

                verifier.addIfConversionFail(
                    EMAIL_ADDRESS.text(),
                    HasHostAddress.class,
                    SpreadsheetConvertersConverterProvider.NET,
                    IS_EMAIL_ADDRESS
                );

                verifier.addIfConversionFail(
                    MAIL_TO_URL.text(),
                    HasHostAddress.class,
                    SpreadsheetConvertersConverterProvider.NET,
                    IS_MAIL_TO_URL
                );
            }
        }

        // number.......................................................................................................
        {
            // null-to-number...........................................................................................
            if (formula || formatting || scripting) {
                verifier.addIfConversionFail(
                    (Object) null, // dont want List overload
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.NUMBER,
                    IS_NUMBER
                );
            }

            // number-to-number.........................................................................................
            verifier.addIfConversionFail(
                1,
                NUMBER_TYPES,
                SpreadsheetConvertersConverterProvider.NUMBER,
                IS_NUMBER
            );
        }

        // optional-to..................................................................................................
        {
            verifier.addIfConversionFail(
                Optional.empty(),
                Boolean.class,
                SpreadsheetConvertersConverterProvider.OPTIONAL_TO, // OPTIONAL_TO
                (Object)null
            );

            verifier.addIfConversionFail(
                Optional.of(false),
                Boolean.class,
                SpreadsheetConvertersConverterProvider.OPTIONAL_TO, // OPTIONAL_TO
                false
            );

            verifier.addIfConversionFail(
                Optional.of(true),
                Boolean.class,
                SpreadsheetConvertersConverterProvider.OPTIONAL_TO, // OPTIONAL_TO
                true
            );

            verifier.addIfConversionFail(
                Optional.of(
                    123
                ),
                NUMBER_TYPES,
                SpreadsheetConvertersConverterProvider.OPTIONAL_TO, // OPTIONAL_TO
                IS_NUMBER
            );

            verifier.addIfConversionFail(
                Optional.of(DATE),
                LocalDate.class,
                SpreadsheetConvertersConverterProvider.OPTIONAL_TO, // OPTIONAL_TO
                DATE
            );

            verifier.addIfConversionFail(
                Optional.of(DATE_TIME),
                LocalDateTime.class,
                SpreadsheetConvertersConverterProvider.OPTIONAL_TO,// OPTIONAL_TO
                DATE_TIME
            );

            verifier.addIfConversionFail(
                Optional.of(DATE_TIME),
                LocalDateTimeList.class,
                SpreadsheetConvertersConverterProvider.OPTIONAL_TO, // OPTIONAL_TO
                LocalDateTimeList.EMPTY.concat(DATE_TIME)
            );

            verifier.addIfConversionFail(
                Optional.of(
                    "Hello"
                ),
                String.class,
                SpreadsheetConvertersConverterProvider.OPTIONAL_TO, // OPTIONAL_TO
                "Optional[Hello]"
            );

            verifier.addIfConversionFail(
                Optional.of(TIME),
                LocalTime.class,
                SpreadsheetConvertersConverterProvider.OPTIONAL_TO, // OPTIONAL_TO
                TIME
            );
        }
        
        // plugins......................................................................................................
        {
            if (formula || scripting || validation) {
                verifier.addIfConversionFail(
                    FORMATTER_SELECTOR.text(),
                    SpreadsheetFormatterSelector.class,
                    SpreadsheetConvertersConverterProvider.PLUGINS, // SPREADSHEET_VALUE
                    FORMATTER_SELECTOR
                );
            }

            // text-to-validation-selector..............................................................................
            if (formula || scripting || validation) {
                verifier.addIfConversionFail(
                    VALIDATOR_SELECTOR.text(),
                    ValidatorSelector.class,
                    SpreadsheetConvertersConverterProvider.PLUGINS, // TEXT_TO_VALIDATOR_SELECTOR
                    VALIDATOR_SELECTOR
                );
            }
        }

        // properties...................................................................................................
        if (query || formatting || formula || scripting || validation) {
            verifier.addIfConversionFail(
                METADATA,
                Properties.class,
                SpreadsheetConvertersConverterProvider.PROPERTIES,
                METADATA.properties()
            );
        }

        // spreadsheetMetadata..........................................................................................
        {
            // spreadsheet-id-to-spreadsheet-metadata...................................................................
            if (formula || scripting) {
                final SpreadsheetMetadata metadata = context.spreadsheetMetadata();

                verifier.addIfConversionFail(
                    metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
                        .toString(),
                    SpreadsheetMetadata.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA, //
                    metadata
                );

                verifier.addIfConversionFail(
                    metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID),
                    SpreadsheetMetadata.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA, //
                    metadata
                );
            }

            // text-to-spreadsheet-id...................................................................................
            final SpreadsheetId spreadsheetId = SpreadsheetId.with(0x123);

            // will be enabled by terminal
            if (formula || scripting) {
                verifier.addIfConversionFail(
                    spreadsheetId.toString(),
                    SpreadsheetId.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA, // TEXT_TO_SPREADSHEET_ID
                    spreadsheetId
                );
            }

            // text-to-spreadsheet-metadata.................................................................................
            final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                spreadsheetId
            );

            if (formula || scripting) {
                verifier.addIfConversionFail(
                    context.marshall(metadata)
                        .toString(),
                    SpreadsheetMetadata.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA, // TEXT_TO_SPREADSHEET_METADATA
                    metadata
                );
            }

            // text-to-spreadsheet-name.................................................................................
            final SpreadsheetName spreadsheetName = SpreadsheetName.with("SpreadsheetName123");

            if (formula || scripting) {
                verifier.addIfConversionFail(
                    spreadsheetName.text(),
                    SpreadsheetName.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA, // TEXT_TO_SPREADSHEET_NAME
                    spreadsheetName
                );
            }

            // text-to-spreadsheet-property-name........................................................................
            final SpreadsheetMetadataPropertyName<SpreadsheetName> spreadsheetMetadataPropertyName = SpreadsheetMetadataPropertyName.SPREADSHEET_NAME;

            if (formula || scripting) {
                verifier.addIfConversionFail(
                    spreadsheetMetadataPropertyName.value(),
                    SpreadsheetMetadataPropertyName.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA, // TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME
                    spreadsheetMetadataPropertyName
                );
            }

            // properties-to-spreadsheet-metadata.......................................................................

            if (formula || scripting) {
                verifier.addIfConversionFail(
                    metadata.properties(),
                    SpreadsheetMetadata.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA, // text-to-spreadsheet-metadata
                    metadata
                );
            }
        }

        // spreadsheetValue.............................................................................................
        {
            // error-to-error...........................................................................................
            if (formula) {
                verifier.addIfConversionFail(
                    ERROR,
                    SpreadsheetError.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // ERROR_TO_ERROR
                );
            }

            // error-to-number..........................................................................................
            if (formula) {
                verifier.addIfConversionFail(
                    ERROR,
                    ExpressionNumber.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // ERROR_TO_NUMBER
                    IS_NUMBER
                );
            }

            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    null, // dont want List overload
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // NULL_TO_NUMBER
                    IS_NUMBER
                );
            }

            // has-spreadsheet-formatter-selector.......................................................................
            if (formula || scripting || validation) {
                verifier.addIfConversionFail(
                    spreadsheetCell,
                    SpreadsheetFormatterSelector.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE,
                    spreadsheetCell.formatterSelector()
                        .orElse(null)
                );
            }

            // has-spreadsheet-parser-selector..........................................................................
            if (formula || scripting || validation) {
                verifier.addIfConversionFail(
                    spreadsheetCell,
                    SpreadsheetParserSelector.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE,
                    spreadsheetCell.parserSelector()
                        .orElse(null)
                );
            }

            // has-validation-selector..................................................................................
            if (formula || scripting || validation) {
                verifier.addIfConversionFail(
                    spreadsheetCell,
                    ValidatorSelector.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE,
                    spreadsheetCell.validator()
                        .orElse(null)
                );
            }

            // spreadsheet-selection-to-spreadsheet-selection...............................................................
            if (formula) {
                verifier.addIfConversionFail(
                    Lists.of(
                        CELL,
                        CELL_RANGE
                    ),
                    SpreadsheetCellReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                    IS_CELL_REFERENCE
                );

                verifier.addIfConversionFail(
                    Lists.of(
                        CELL,
                        CELL_RANGE,
                        COLUMN,
                        COLUMN_RANGE,
                        ROW,
                        ROW_RANGE
                    ),
                    SpreadsheetCellRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                    IS_CELL_RANGE_REFERENCE
                );

                verifier.addIfConversionFail(
                    COLUMN,
                    SpreadsheetColumnReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                );

                verifier.addIfConversionFail(
                    COLUMN,
                    SpreadsheetColumnRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                    COLUMN.toColumnRange()
                );

                verifier.addIfConversionFail(
                    COLUMN_RANGE,
                    SpreadsheetColumnRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                );

                verifier.addIfConversionFail(
                    ROW,
                    SpreadsheetRowReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                );

                verifier.addIfConversionFail(
                    ROW_RANGE,
                    SpreadsheetRowRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                );
            }

            // spreadsheet-selection-to-text............................................................................

            if (formula || scripting || validation) {
                verifier.addIfConversionFail(
                    Lists.of(
                        CELL,
                        CELL_RANGE,
                        COLUMN,
                        COLUMN_RANGE,
                        ROW,
                        ROW_RANGE,
                        LABEL
                    ),
                    String.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE,
                    IS_STRING
                );
            }

            // spreadsheet-values to text...............................................................................

            if (formula || scripting || validation) {
                verifier.addIfConversionFail(
                    Lists.of(
                        AUDIT_INFO,
                        spreadsheetCell,
                        ERROR,
                        SpreadsheetId.with(1),
                        STORAGE_PATH,
                        StorageValue.with(
                            STORAGE_PATH
                        ).setValue(
                            Optional.of("StorageValue123")
                        ),
                        StorageValueInfo.with(
                            STORAGE_PATH,
                            AUDIT_INFO
                        ),
                        STYLE,
                        ZoneOffset.UTC
                    ),
                    String.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE,
                    IS_STRING
                );
            }

            // text-to-error............................................................................................
            if (validation) {
                verifier.addIfConversionFail(
                    ERROR.text(),
                    SpreadsheetError.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_ERROR
                    IS_ERROR
                );
            }

            // text.....................................................................................................
            verifier.addIfConversionFail(
                ' ',
                Lists.of(
                    Character.class,
                    String.class
                ),
                SpreadsheetConvertersConverterProvider.TEXT, // TEXT
                IS_NOT_NULL
            );

            verifier.addIfConversionFail(
                "A",
                Lists.of(
                    Character.class,
                    String.class
                ),
                SpreadsheetConvertersConverterProvider.TEXT, // TEXT
                IS_NOT_NULL
            );

            verifier.addIfConversionFail(
                charset.toString(),
                Charset.class,
                SpreadsheetConvertersConverterProvider.TEXT,
                charset
            );

            verifier.addIfConversionFail(
                "   ",
                Indentation.class,
                SpreadsheetConvertersConverterProvider.TEXT, // TEXT
                IS_INDENTATION
            );

            verifier.addIfConversionFail(
                Lists.of(
                    "\r\n",
                    "crnl"
                ),
                LineEnding.class,
                SpreadsheetConvertersConverterProvider.TEXT, // TEXT
                IS_LINE_ENDING
            );

            // text-to-lineEnding.......................................................................................
            verifier.addIfConversionFail(
                LineEnding.NL.text(),
                LineEnding.class,
                SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_LINE_ENDING
                IS_LINE_ENDING
            );

            // text-to-spreadsheet-selection............................................................................
            if (formula || scripting || validation) {
                for (final SpreadsheetSelection selection : Lists.of(
                    CELL,
                    CELL_RANGE,
                    COLUMN,
                    COLUMN_RANGE,
                    LABEL
                )) {
                    verifier.addIfConversionFail(
                        selection.toString(),
                        selection.getClass(),
                        SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                        selection // expected
                    );
                }

                verifier.addIfConversionFail(
                    CELL.toString(),
                    SpreadsheetCellReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    CELL
                );

                verifier.addIfConversionFail(
                    CELL.toString(),
                    SpreadsheetCellRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    CELL.toCellRange()
                );

                verifier.addIfConversionFail(
                    CELL_RANGE.toString(),
                    SpreadsheetCellRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    CELL_RANGE
                );

                verifier.addIfConversionFail(
                    LABEL.toString(),
                    SpreadsheetLabelName.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    LABEL
                );

                verifier.addIfConversionFail(
                    COLUMN.toString(),
                    SpreadsheetColumnReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    COLUMN
                );

                verifier.addIfConversionFail(
                    COLUMN.toString(),
                    SpreadsheetColumnRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    COLUMN.toColumnRange()
                );

                verifier.addIfConversionFail(
                    COLUMN.toString(),
                    SpreadsheetColumnReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    COLUMN
                );

                verifier.addIfConversionFail(
                    COLUMN_RANGE.toString(),
                    SpreadsheetColumnRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    COLUMN_RANGE
                );

                verifier.addIfConversionFail(
                    ROW.toString(),
                    SpreadsheetRowReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    ROW
                );

                verifier.addIfConversionFail(
                    ROW.toString(),
                    SpreadsheetRowRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    ROW.toRowRange()
                );

                verifier.addIfConversionFail(
                    ROW.toString(),
                    SpreadsheetRowReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    ROW
                );

                verifier.addIfConversionFail(
                    ROW_RANGE.toString(),
                    SpreadsheetRowRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_SPREADSHEET_SELECTION
                    ROW_RANGE
                );
            }

            // text-to-value-type.......................................................................................
            if (validation) {
                verifier.addIfConversionFail(
                    SpreadsheetValueType.TEXT.value(),
                    ValueType.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_VALUE_TYPE
                    SpreadsheetValueType.TEXT
                );
            }

            // text-to-zone-offset......................................................................................
            if (formula || scripting) {
                final ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutes(12, 59);

                verifier.addIfConversionFail(
                    zoneOffset.toString(),
                    ZoneOffset.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE, // TEXT_TO_ZONE_OFFSET
                    zoneOffset
                );
            }
        }

        // storage......................................................................................................
        {
            if (formula || scripting) {
                final String text = "BinaryTextContent123";

                verifier.addIfConversionFail(
                    BINARY,
                    Binary.class,
                    SpreadsheetConvertersConverterProvider.BINARY,
                    BINARY
                );

                verifier.addIfConversionFail(
                    text,
                    Binary.class,
                    SpreadsheetConvertersConverterProvider.BINARY,
                    IS_BINARY
                );

                verifier.addIfConversionFail(
                    Binary.with(
                        text.getBytes(charset)
                    ),
                    String.class,
                    SpreadsheetConvertersConverterProvider.BINARY,
                    text
                );

                verifier.addIfConversionFail(
                    StorageBinary.with(
                        STORAGE_PATH_BINARY,
                        UNKNOWN_BINARY_FILE
                    ),
                    StorageValue.class,
                    SpreadsheetConvertersConverterProvider.STORAGE_BINARY_TO_STORAGE_VALUE_BINARY,
                    StorageValue.with(
                        STORAGE_PATH_BINARY
                    ).setValue(
                        Optional.of(UNKNOWN_BINARY_FILE)
                    ).setContentType(
                        Optional.of(
                            context.detect(
                                STORAGE_PATH_BINARY.value(),
                                UNKNOWN_BINARY_FILE
                            )
                        )
                    )
                );

                verifier.addIfConversionFail(
                    StorageBinary.with(
                        STORAGE_PATH_CSV,
                        Binary.with(
                            CSV_STRING_LIST.text()
                                .getBytes(charset)
                        )
                    ),
                    StorageValue.class,
                    SpreadsheetConvertersConverterProvider.STORAGE_BINARY_TO_STORAGE_VALUE_CSV,
                    StorageValue.with(
                        STORAGE_PATH_CSV
                    ).setValue(
                        Optional.of(CSV_STRING_LIST)
                    )
                );

                verifier.addIfConversionFail(
                    StorageBinary.with(
                        STORAGE_PATH_EXPRESSION,
                        Binary.with(
                            EXPRESSION_TEXT.getBytes(charset)
                        )
                    ),
                    StorageValue.class,
                    SpreadsheetConvertersConverterProvider.STORAGE_BINARY_TO_STORAGE_VALUE_EXPRESSION,
                    IS_STORAGE_VALUE
                );

                verifier.addIfConversionFail(
                    StorageBinary.with(
                        STORAGE_PATH_TXT,
                        Binary.with(
                            text.getBytes(charset)
                        )
                    ),
                    StorageValue.class,
                    SpreadsheetConvertersConverterProvider.STORAGE_BINARY_TO_STORAGE_VALUE_TXT,
                    StorageValue.with(STORAGE_PATH_TXT)
                        .setValue(
                            Optional.of(text)
                        )
                );

                {
                    verifier.addIfConversionFail(
                        StorageBinary.with(
                            STORAGE_PATH_JSON,
                            Binary.with(
                                JSON_OBJECT.toString().getBytes(charset)
                            )
                        ),
                        StorageValue.class,
                        SpreadsheetConvertersConverterProvider.STORAGE_BINARY_TO_STORAGE_VALUE_JSON,
                        StorageValue.with(STORAGE_PATH_JSON)
                            .setValue(
                                Optional.of(JSON_OBJECT)
                            )
                    );
                }

                verifier.addIfConversionFail(
                    StorageBinary.with(
                        STORAGE_PATH_PROPERTIES,
                        Binary.with(
                            PROPERTIES.text()
                                .getBytes(charset)
                        )
                    ),
                    StorageValue.class,
                    SpreadsheetConvertersConverterProvider.STORAGE_BINARY_TO_STORAGE_VALUE_PROPERTIES,
                    StorageValue.with(STORAGE_PATH_PROPERTIES)
                        .setValue(
                            Optional.of(PROPERTIES)
                        )
                );

                verifier.addIfConversionFail(
                    StorageBinary.with(
                        STORAGE_PATH_TSV,
                        Binary.with(
                            TSV_STRING_LIST.text()
                                .getBytes(charset)
                        )
                    ),
                    StorageValue.class,
                    SpreadsheetConvertersConverterProvider.STORAGE_BINARY_TO_STORAGE_VALUE_TSV,
                    StorageValue.with(STORAGE_PATH_TSV)
                        .setValue(
                            Optional.of(TSV_STRING_LIST)
                        )
                );

                verifier.addIfConversionFail(
                    STORAGE_VALUE_BINARY,
                    StorageValue.class,
                    SpreadsheetConvertersConverterProvider.STORAGE_VALUE_TO_STORAGE_BINARY_BINARY,
                    STORAGE_BINARY_BINARY
                );

                verifier.addIfConversionFail(
                    StorageValueInfoList.EMPTY.concat(
                        StorageValueInfo.with(
                            StoragePath.parse("/path1/file2.txt"),
                            AuditInfo.create(
                                EMAIL_ADDRESS,
                                LocalDateTime.of(
                                    1999,
                                    12,
                                    31,
                                    12,
                                    58
                                )
                            )
                        )
                    ),
                    String.class,
                    SpreadsheetConvertersConverterProvider.STORAGE_VALUE_INFO_LIST_TO_TEXT,
                    IS_STRING
                );

                verifier.addIfConversionFail(
                    "/path1/file2.txt",
                    StoragePath.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_STORAGE_PATH,
                    IS_STORAGE_PATH
                );
            }
        }

        // style-.......................................................................................................
        {
            // to-style-...............................................................................................
            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    Lists.of(
                        STYLE,
                        SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                            .setStyle(STYLE)
                    ),
                    TextStyle.class,
                    SpreadsheetConvertersConverterProvider.STYLE, // TO_STYLE
                    STYLE
                );
            }

            // properties-to-text-style.................................................................................

            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    STYLE.properties(),
                    TextStyle.class,
                    SpreadsheetConvertersConverterProvider.STYLE, // PROPERTIES_TO_TEXT_STYLE
                    STYLE
                );

                verifier.addIfConversionFail(
                    STYLE.properties()
                        .toString(),
                    TextStyle.class,
                    SpreadsheetConvertersConverterProvider.STYLE, // PROPERTIES_TO_TEXT_STYLE
                    STYLE
                );
            }

            // text-to-border.................................................................................
            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    BORDER.text(),
                    Border.class,
                    SpreadsheetConvertersConverterProvider.STYLE, // text-to-border
                    BORDER
                );
            }

            // text-to-margin.................................................................................
            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    MARGIN.text(),
                    Margin.class,
                    SpreadsheetConvertersConverterProvider.STYLE, // text-to-margin
                    MARGIN
                );
            }

            // text-to-padding.................................................................................
            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    PADDING.text(),
                    Padding.class,
                    SpreadsheetConvertersConverterProvider.STYLE, // text-to-padding
                    PADDING
                );
            }
            
            // text-to-spreadsheet-text.................................................................................
            if (formatting || formula || scripting) {
                verifier.addIfConversionFail(
                    SPREADSHEET_TEXT.text(),
                    SpreadsheetText.class,
                    SpreadsheetConvertersConverterProvider.STYLE, // TEXT_TO_SPREADSHEET_TEXT
                    SPREADSHEET_TEXT
                );

                // text-to-textStyle....................................................................................
                verifier.addIfConversionFail(
                    STYLE.text(),
                    TextStyle.class,
                    SpreadsheetConvertersConverterProvider.STYLE, // TEXT_TO_TEXT_STYLE
                    STYLE
                );

                // text-to-text-style-property-name.....................................................................
                verifier.addIfConversionFail(
                    TextStylePropertyName.BACKGROUND_COLOR.text(),
                    TextStylePropertyName.class,
                    SpreadsheetConvertersConverterProvider.STYLE, // TEXT_TO_TEXT_STYLE_PROPERTY_NAME
                    TextStylePropertyName.BACKGROUND_COLOR
                );
            }
        }

        // template.....................................................................................................
        {
            // text-to-template-value-name..............................................................................
            if (formatting) {
                verifier.addIfConversionFail(
                    TemplateValueName.with("TemplateValue123"),
                    TemplateValueName.class,
                    SpreadsheetConvertersConverterProvider.TEMPLATE // TEXT_TO_TEMPLATE_VALUE_NAME
                );
            }
        }

        // text.........................................................................................................
        {
            verifier.addIfConversionFail(
                Lists.of(
                    'A',
                    "Text",
                    Url.parseAbsolute("https://example.com/123"),
                    spreadsheetCell.formula()
                ),
                String.class,
                SpreadsheetConvertersConverterProvider.TEXT,
                IS_STRING
            );

            // text-to-boolean-list.................................................................................
            verifier.addIfConversionFail(
                "TRUE, FALSE, true",
                BooleanList.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_BOOLEAN_LIST,
                IS_BOOLEAN_LIST
            );

            // text-to-csv-string-list..............................................................................
            verifier.addIfConversionFail(
                "apple,banana,\"333 444\"",
                CsvStringList.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_CSV_STRING_LIST,
                IS_CSV_STRING_LIST
            );

            // text-to-csv-string-set..............................................................................
            verifier.addIfConversionFail(
                "apple,banana,\"333 444\"",
                CsvStringSet.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_CSV_STRING_SET,
                IS_CSV_STRING_SET
            );

            // text-to-date-list....................................................................................
            verifier.addIfConversionFail(
                "1999/12/31",
                LocalDateList.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_DATE_LIST,
                LocalDateList.EMPTY.concat(DATE)
            );

            // text-to-date-time-list...............................................................................
            verifier.addIfConversionFail(
                "1999/12/31 12:58",
                LocalDateTimeList.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_DATE_TIME_LIST,
                IS_LOCAL_DATE_TIME_LIST
            );

            if (formatting) {
                verifier.addIfConversionFail(
                    Lists.of(
                        "",
                        "\n",
                        "\r",
                        "\r\n",
                        "CR",
                        "CRLF",
                        "LF",
                        "NL"
                    ),
                    LineEnding.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_LINE_ENDING,
                    IS_LINE_ENDING
                );
            }

            // text-to-number-list..................................................................................
            verifier.addIfConversionFail(
                "1, 22, 333.5",
                NumberList.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_NUMBER_LIST,
                IS_NUMBER_LIST
            );

            // text-to-string-list..................................................................................
            verifier.addIfConversionFail(
                "apple, banana, 333",
                StringList.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_STRING_LIST,
                IS_STRING_LIST
            );

            // text-to-time-list....................................................................................
            verifier.addIfConversionFail(
                "12:58:59", // "12:58" without seconds fails
                LocalTimeList.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_TIME_LIST,
                LocalTimeList.EMPTY.concat(
                    TIME.withSecond(59)
                )
            );

            // text-to-tsv-string-list..............................................................................
            verifier.addIfConversionFail(
                TSV_STRING_LIST.text(),
                TsvStringList.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_TSV_STRING_LIST,
                TSV_STRING_LIST
            );

            // text-to-tsv-string-set..............................................................................
            verifier.addIfConversionFail(
                TSV_STRING_SET.text(),
                TsvStringSet.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_TSV_STRING_SET,
                TSV_STRING_SET
            );
        }

        // text-node....................................................................................................
        {
            // text-to-line-ending......................................................................................
            if (formula || scripting) {
                // text-to-flag.........................................................................................
                verifier.addIfConversionFail(
                    "AU",
                    Flag.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_FLAG, // TEXT_TO_FLAG
                    TextNode.flag("AU")
                );

                // url-to-hyperlink.....................................................................................
                verifier.addIfConversionFail(
                    ABSOLUTE_URL.text(),
                    Hyperlink.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE, // URL_TO_HYPERLINK
                    IS_HYPERLINK
                );

                // url-to-image.........................................................................................
                verifier.addIfConversionFail(
                    ABSOLUTE_URL.text(),
                    Image.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE, // URL_TO_IMAGE
                    IS_IMAGE
                );
            }

            // text-to-url..............................................................................................
            if (formatting || formula || scripting) {
                if (formatting) {
                    verifier.addIfConversionFail(
                        ABSOLUTE_URL.text(),
                        String.class,
                        SpreadsheetConvertersConverterProvider.TEXT_NODE // TEXT_TO_URL
                    );
                }

                // url-to-hyperlink.....................................................................................
                verifier.addIfConversionFail(
                    ABSOLUTE_URL.text(),
                    Hyperlink.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE, // URL_TO_HYPERLINK
                    HYPERLINK
                );

                // url-to-image.........................................................................................
                verifier.addIfConversionFail(
                    ABSOLUTE_URL.text(),
                    Image.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE, // URL_TO_IMAGE
                    IMAGE
                );
            }

            if (formatting || formula || scripting) {
                // has-text-node........................................................................................
                verifier.addIfConversionFail(
                    TextNode.text("Text123").setTextStyle(
                        TextStyle.EMPTY.set(
                            TextStylePropertyName.COLOR,
                            Color.BLACK
                        )
                    ),
                    Styleable.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE // TO_STYLEABLE
                );

                verifier.addIfConversionFail(
                    STYLE,
                    Styleable.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE // TO_STYLEABLE
                );

                verifier.addIfConversionFail(
                    Lists.of(
                        'A',
                        "Text123",
                        TextNode.text("Text123")
                    ),
                    TextNode.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE, // HAS_TEXT_NODE
                    IS_TEXT_NODE
                );

                // text-to-textNode.....................................................................................
                verifier.addIfConversionFail(
                    Lists.of(
                        'A',
                        "Text"
                    ),
                    TextNode.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE, // TEXT_TO_TEXT_NODE
                    IS_TEXT_NODE
                );

                // url-to-hyperlink.....................................................................................
                verifier.addIfConversionFail(
                    ABSOLUTE_URL.text(),
                    Hyperlink.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE, // URL_TO_HYPERLINK
                    HYPERLINK
                );

                // url-to-image.........................................................................................
                verifier.addIfConversionFail(
                    ABSOLUTE_URL.text(),
                    Image.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE, // URL_TO_IMAGE
                    IMAGE
                );
            }
        }

        {
            // validation...............................................................................................
            {
                if (formula || scripting || validation) {
                    if (validation) {
                        verifier.addIfConversionFail(
                            "Form123",
                            FormName.class,
                            SpreadsheetConvertersConverterProvider.FORM_AND_VALIDATION, // TEXT_TO_FORM_NAME
                            IS_FORM_NAME
                        );

                        // text-to-validation-error.....................................................................
                        verifier.addIfConversionFail(
                            SpreadsheetForms.error(CELL)
                                .setMessage("Error message 123")
                                .text(),
                            ValidationError.class,
                            SpreadsheetConvertersConverterProvider.FORM_AND_VALIDATION, // TEXT_TO_VALIDATION_ERROR
                            IS_VALIDATION_ERROR
                        );
                    }
                }

                if (validation) {
                    // to-validation-checkbox.............................................................................
                    verifier.addIfConversionFail(
                        Lists.of(
                            Lists.empty(),
                            Lists.of("true"),
                            Lists.of("true111, false222"),
                            Lists.of(
                                "true111"
                            ),
                            Lists.of(
                                "true111",
                                "false222"
                            )
                        ),
                        ValidationCheckbox.class,
                        SpreadsheetConvertersConverterProvider.FORM_AND_VALIDATION, // TO_VALIDATION_ERROR_LIST
                        IS_VALIDATION_CHECKBOX
                    );

                    // to-validation-choice.............................................................................
                    verifier.addIfConversionFail(
                        Lists.of(
                            111,
                            "Choice1"
                        ),
                        ValidationChoice.class,
                        SpreadsheetConvertersConverterProvider.FORM_AND_VALIDATION, // TO_VALIDATION_ERROR_LIST
                        IS_VALIDATION_CHOICE
                    );

                    // to-validation-choice-list........................................................................
                    verifier.addIfConversionFail(
                        Lists.of(
                            Lists.of(
                                ValidationChoice.with(
                                    "Choice1",
                                    Optional.of(111)
                                ),
                                ValidationChoice.with(
                                    "Choice2",
                                    Optional.of(222
                                    )
                                )
                            ),
                            Lists.of(
                                ValidationChoice.with(
                                    "",
                                    Optional.empty()
                                ),
                                ValidationChoice.with(
                                    "Choice100",
                                    Optional.of(
                                        1000
                                    )
                                ),
                                ValidationChoice.with(
                                    "Choice200",
                                    Optional.of(
                                        2000
                                    )
                                )
                            ),
                            Lists.of(
                                "Choice1000,Choice2000,Choice3000,"
                            )
                        ),
                        ValidationChoiceList.class,
                        SpreadsheetConvertersConverterProvider.FORM_AND_VALIDATION, // TO_VALIDATION_ERROR_LIST
                        IS_VALIDATION_CHOICE_LIST
                    );

                    // to-validation-error-list.........................................................................
                    verifier.addIfConversionFail(
                        Lists.of(
                            "Validation error message 1",
                            SpreadsheetForms.error(SpreadsheetSelection.A1)
                                .setMessage("Validation error message2")
                        ),
                        ValidationErrorList.class,
                        SpreadsheetConvertersConverterProvider.FORM_AND_VALIDATION, // TO_VALIDATION_ERROR_LIST
                        IS_VALIDATION_ERROR_LIST
                    );
                }
            }
        }

        return MissingConverterSet.EMPTY.setElements(
            verifier.missing.values()
        );
    }

    private MissingConverterVerifier(final Converter<SpreadsheetConverterContext> converter,
                                     final SpreadsheetConverterContext context) {
        this.converter = converter;
        this.context = context;

        this.missing = Maps.sorted();
    }

    private void addIfConversionFail(final List<Object> values,
                                     final Class<?> type,
                                     final ConverterName name,
                                     final Object expected) {
        for (final Object value : values) {
            this.addIfConversionFail(
                value,
                type,
                name,
                expected
            );
        }
    }

    private void addIfConversionFail(final List<Object> values,
                                     final Class<?> type,
                                     final ConverterName name,
                                     final Predicate<Object> answerChecker) {
        for (final Object value : values) {
            this.addIfConversionFail(
                value,
                type,
                name,
                answerChecker
            );
        }
    }

    private void addIfConversionFail(final Object value,
                                     final List<Class<?>> types,
                                     final ConverterName name,
                                     final Predicate<Object> answerChecker) {
        for (final Class<?> type : types) {
            this.addIfConversionFail(
                value,
                type,
                name,
                answerChecker
            );
        }
    }

    private void addIfConversionFail(final Object value,
                                     final Class<?> type,
                                     final ConverterName name) {
        this.addIfConversionFail(
            value,
            type,
            name,
            value
        );
    }

    private void addIfConversionFail(final Object value,
                                     final Class<?> type,
                                     final ConverterName name,
                                     final Object expected) {
        this.addIfConversionFail(
            value,
            type,
            name,
            Predicates.is(expected)
        );
    }

    private void addIfConversionFail(final Object value,
                                     final Class<?> type,
                                     final ConverterName name,
                                     final Predicate<Object> answerChecker) {
        boolean failed;

        try {
            final Either<?, String> converted = this.converter.convert(
                value,
                type,
                this.context
            );
            failed = converted.isRight();

            if(false == failed) {
                failed = false == answerChecker.test(
                    converted.leftValue()
                );
            }

        } catch (final UnsupportedOperationException rethrow) {
            throw rethrow;
        } catch (final RuntimeException cause) {
            cause.printStackTrace(); // KEEP!
            failed = true;
        }

        if (failed) {
            this.add(
                value,
                type,
                name
            );
        }
    }

    private void add(final Object value,
                     final Class<?> type,
                     final ConverterName name) {
        final MissingConverterValue missingConverterValue = MissingConverterValue.with(
            value,
            type.getName()
        );

        final MissingConverter missingConverter = this.missing.get(name);

        this.missing.put(
            name,
            null == missingConverter ?
                MissingConverter.with(
                    name,
                    Sets.of(missingConverterValue)
                ) :
                missingConverter.add(missingConverterValue)
        );
    }

    private final Converter<SpreadsheetConverterContext> converter;

    private final SpreadsheetConverterContext context;

    private final Map<ConverterName, MissingConverter> missing;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.missing.toString();
    }
}

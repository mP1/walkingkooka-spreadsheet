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

package walkingkooka.spreadsheet.value;

import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.io.FileExtension;
import walkingkooka.io.HasFileExtension;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.text.CaseKind;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.ValueType;
import walkingkooka.validation.provider.ValidatorSelector;

import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Directive that controls what part of a cell to export or when importing what part of a cell to replace.
 */
public enum SpreadsheetCellValueKind implements HasFileExtension {

    CELL {
        @Override
        public SpreadsheetCell cellValue(final SpreadsheetCell cell) {
            return cell;
        }
    },

    CURRENCY {
        @Override
        public Optional<Currency> cellValue(final SpreadsheetCell cell) {
            return cell.currency();
        }
    },

    DATE_TIME_SYMBOLS {
        @Override
        public Optional<DateTimeSymbols> cellValue(final SpreadsheetCell cell) {
            return cell.dateTimeSymbols();
        }
    },

    DECIMAL_NUMBER_SYMBOLS {
        @Override
        public Optional<DecimalNumberSymbols> cellValue(final SpreadsheetCell cell) {
            return cell.decimalNumberSymbols();
        }
    },

    FORMULA {
        @Override
        public SpreadsheetFormula cellValue(final SpreadsheetCell cell) {
            return cell.formula();
        }
    },

    FORMATTER {
        @Override
        public Optional<SpreadsheetFormatterSelector> cellValue(final SpreadsheetCell cell) {
            return cell.formatter();
        }
    },

    LOCALE {
        @Override
        public Optional<Locale> cellValue(final SpreadsheetCell cell) {
            return cell.locale();
        }
    },

    PARSER {
        @Override
        public Optional<SpreadsheetParserSelector> cellValue(final SpreadsheetCell cell) {
            return cell.parser();
        }
    },

    STYLE {
        @Override
        public TextStyle cellValue(final SpreadsheetCell cell) {
            return cell.style();
        }
    },

    VALIDATOR {
        @Override
        public Optional<ValidatorSelector> cellValue(final SpreadsheetCell cell) {
            return cell.validator();
        }
    },

    VALUE {
        @Override
        public Optional<Object> cellValue(final SpreadsheetCell cell) {
            return cell.formula()
                .value();
        }
    },

    VALUE_TYPE {
        @Override
        public Optional<ValueType> cellValue(final SpreadsheetCell cell) {
            return cell.formula()
                .valueType();
        }
    },

    FORMATTED_VALUE {
        @Override
        public Optional<TextNode> cellValue(final SpreadsheetCell cell) {
            return cell.formattedValue();
        }
    };

    SpreadsheetCellValueKind() {
        final String name = this.name();

        final String fileExtensionText = "CELL".equals(name) ?
            "" :
            CaseKind.SNAKE.change(
                name,
                CaseKind.CAMEL
            );

        this.fileExtensionText = fileExtensionText;

        this.fileExtension = Optional.ofNullable(
            fileExtensionText.isEmpty() ?
                null :
                FileExtension.parse(fileExtensionText)
        );
    }

    public abstract Object cellValue(final SpreadsheetCell cell);

    public static SpreadsheetCellValueKind parse(final String fileExtension) {
        Objects.requireNonNull(fileExtension, "fileExtension");

        final SpreadsheetCellValueKind spreadsheetCellValueKind = fromFileExtensionOrNull(fileExtension);

        if (null == spreadsheetCellValueKind) {
            new IllegalArgumentException("Unknown file extension " + CharSequences.quote(fileExtension));
        }

        return spreadsheetCellValueKind;
    }

    private final String fileExtensionText;

    // fromFileExtension................................................................................................

    /**
     * From a {@link FileExtension} matches the appropriate {@link SpreadsheetCellValueKind}.
     * <pre>
     * a1.json -> {@link SpreadsheetCellValueKind#CELL}
     * a1.style -> {@link SpreadsheetCellValueKind#STYLE}
     * a1.style.json -> {@link SpreadsheetCellValueKind#STYLE}
     * </pre>
     */
    public static SpreadsheetCellValueKind fromFileExtension(final Optional<FileExtension> fileExtension) {
        Objects.requireNonNull(fileExtension, "fileExtension");

        SpreadsheetCellValueKind spreadsheetCellValueKind = null;

        FileExtension fileExtensionOrNull = fileExtension.orElse(null);
        if (null != fileExtensionOrNull) {
            final String fileExtensionText = fileExtensionOrNull.value();

            final int length = fileExtensionText.length();

            int i = 0;
            while (i < length) {
                final int next = fileExtensionText.indexOf(
                    FileExtension.SEPARATOR,
                    i
                );
                if (-1 == next) {
                    spreadsheetCellValueKind = fromFileExtensionOrNull(
                        fileExtensionText.substring(
                            i
                        )
                    );
                    break;
                }

                spreadsheetCellValueKind = fromFileExtensionOrNull(
                    fileExtensionText.substring(
                        i,
                        next
                    )
                );

                if (null != spreadsheetCellValueKind) {
                    break;
                }
                i = next + 1;
            }
        }

        return null != spreadsheetCellValueKind ?
            spreadsheetCellValueKind :
            CELL;
    }

    private static SpreadsheetCellValueKind fromFileExtensionOrNull(final String fileExtension) {
        return Arrays.stream(values())
            .filter((SpreadsheetCellValueKind kind) -> FileExtension.CASE_SENSITIVITY.equals(
                kind.fileExtensionText,
                fileExtension
            )).findFirst()
            .orElse(null);
    }

    // HasFileExtension.................................................................................................

    /**
     * Some examples of file extensions include:
     * <pre>
     * a1 NOT a1.cell
     * a1.style
     * a1.value
     * al.value-type
     * </pre>
     */
    @Override
    public final Optional<FileExtension> fileExtension() {
        return this.fileExtension;
    }

    private final Optional<FileExtension> fileExtension;
}

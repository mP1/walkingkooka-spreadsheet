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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.printer.TreePrintableTesting;

import java.time.LocalDateTime;
import java.util.Optional;

public final class SpreadsheetMetadataTestingTest implements SpreadsheetMetadataTesting,
    TreePrintableTesting {

    @Test
    public void testGeneralConverter() {
        METADATA_EN_AU.generalConverter(
            SPREADSHEET_FORMATTER_PROVIDER,
            SPREADSHEET_PARSER_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testEffectiveStyle() {
        METADATA_EN_AU.effectiveStyle();
    }

    @Test
    public void testFormatter() {
        METADATA_EN_AU.spreadsheetFormatter(
            SPREADSHEET_FORMATTER_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testJsonNodeMarshallContext() {
        METADATA_EN_AU.jsonNodeMarshallContext();
    }

    @Test
    public void testJsonNodeUnmarshallContext() {
        METADATA_EN_AU.jsonNodeUnmarshallContext();
    }

    @Test
    public void testParseFormula() {
        this.checkEquals(
            SpreadsheetFormula.EMPTY.setToken(
                Optional.of(
                    SpreadsheetFormulaParserToken.expression(
                        Lists.of(
                            SpreadsheetFormulaParserToken.equalsSymbol(
                                "=",
                                "="
                            ),
                            SpreadsheetFormulaParserToken.number(
                                Lists.of(
                                    SpreadsheetFormulaParserToken.digits(
                                        "1",
                                        "1"
                                    )
                                ),
                                "1"
                            )
                        ),
                        "=1"
                    )
                )
            ),
            SpreadsheetMetadataTesting.parseFormula(
                "=1"
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterContext() {
        METADATA_EN_AU.spreadsheetFormatterContext(
            SpreadsheetMetadata.NO_CELL,
            (final Optional<Object> value) -> {
                throw new UnsupportedOperationException();
            },
            (label) -> {
                throw new UnsupportedOperationException();
            },
            CONVERTER_PROVIDER,
            SPREADSHEET_FORMATTER_PROVIDER,
            LOCALE_CONTEXT,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParser() {
        METADATA_EN_AU.spreadsheetParser(
            SPREADSHEET_PARSER_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParserContext() {
        METADATA_EN_AU
            .spreadsheetParserContext(
                SpreadsheetMetadata.NO_CELL,
                LOCALE_CONTEXT,
                LocalDateTime::now
            );
    }
}

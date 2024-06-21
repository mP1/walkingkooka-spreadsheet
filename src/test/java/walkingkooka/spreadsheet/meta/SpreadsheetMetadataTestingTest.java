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
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.format.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.printer.TreePrintableTesting;

import java.time.LocalDateTime;
import java.util.Optional;

public final class SpreadsheetMetadataTestingTest implements SpreadsheetMetadataTesting,
        TreePrintableTesting {

    private final static SpreadsheetFormatterProvider SPREADSHEET_FORMATTER_PROVIDER = SpreadsheetFormatterProviders.spreadsheetFormatPattern();
    private final static SpreadsheetParserProvider SPREADSHEET_PARSER_PROVIDER = SpreadsheetParserProviders.spreadsheetParsePattern();

    @Test
    public void testConverter() {
        METADATA_EN_AU.converter(
                SPREADSHEET_FORMATTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER
        );
    }

    @Test
    public void testConverterContext() {
        METADATA_EN_AU.converterContext(
                SPREADSHEET_FORMATTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                        LocalDateTime::now,
                        (label) -> {
                            throw new UnsupportedOperationException();
                        }
                );
    }

    @Test
    public void testEffectiveStyle() {
        METADATA_EN_AU.effectiveStyle();
    }

    @Test
    public void testFormatter() {
        METADATA_EN_AU.formatter(SPREADSHEET_FORMATTER_PROVIDER);
    }

    @Test
    public void testFormatterContext() {
        METADATA_EN_AU.formatterContext(
                SPREADSHEET_FORMATTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                LocalDateTime::now,
                (label) -> {
                    throw new UnsupportedOperationException();
                }
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
    public void testParser() {
        METADATA_EN_AU.parser(SPREADSHEET_PARSER_PROVIDER);
    }

    @Test
    public void testParserContext() {
        METADATA_EN_AU
                .parserContext(LocalDateTime::now);
    }

    @Test
    public void testParseFormula() {
        this.checkEquals(
                SpreadsheetFormula.EMPTY.setToken(
                        Optional.of(
                                SpreadsheetParserToken.expression(
                                        Lists.of(
                                                SpreadsheetParserToken.equalsSymbol(
                                                        "=",
                                                        "="
                                                ),
                                                SpreadsheetParserToken.number(
                                                        Lists.of(
                                                                SpreadsheetParserToken.digits(
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
}

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

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatAmPmParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDayParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDecimalPointParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitSpaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitZeroParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEscapeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGeneralSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatHourParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatMonthOrMinuteParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatQuotedTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSecondParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextLiteralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatWhitespaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatYearParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.function.BiFunction;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that handles fixing the case of any HOURS components (making them lowercase)
 * <a href="https://github.com/mP1/walkingkooka-spreadsheet/issues/1403">#1403</a>
 */
final class SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static <T extends SpreadsheetFormatParentParserToken> T fix(final T parent,
                                                                final BiFunction<List<ParserToken>, String, T> factory) {
        final SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor();
        visitor.accept(parent);
        return factory.apply(visitor.tokens, ParserToken.text(visitor.tokens));
    }

    SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDayParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitSpaceParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatGeneralSymbolParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatHourParserToken token) {
        this.add(
                SpreadsheetFormatParserToken.hour(
                        token.value().toLowerCase(),
                        token.text().toLowerCase()
                )
        );
    }

    @Override
    protected void visit(final SpreadsheetFormatMonthOrMinuteParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatWhitespaceParserToken token) {
        this.add(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatYearParserToken token) {
        this.add(token);
    }

    private void add(final SpreadsheetFormatParserToken token) {
        this.tokens.add(token);
    }

    private final List<ParserToken> tokens = Lists.array();

    @Override
    public String toString() {
        return this.tokens.toString();
    }
}

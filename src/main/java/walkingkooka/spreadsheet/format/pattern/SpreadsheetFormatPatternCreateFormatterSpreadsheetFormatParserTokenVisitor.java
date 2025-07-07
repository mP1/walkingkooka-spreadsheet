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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatter;
import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ConditionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.EqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.FractionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GeneralSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GreaterThanEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GreaterThanSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.LessThanEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.LessThanSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NotEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SeparatorSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TimeSpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that visits a {@link ParserToken} and creates a {@link SpreadsheetFormatter}.
 */
final class SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static SpreadsheetPatternSpreadsheetFormatter createFormatter(final SpreadsheetFormatPattern pattern) {
        final ParserToken token = pattern.value;

        final SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitor(
            token
        );
        visitor.accept(token);

        final List<SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent> components = visitor.components;
        final int count = components.size();
        if (0 == count) {
            throw new IllegalArgumentException("Unable to create formatter parse " + token);
        }

        final List<SpreadsheetPatternSpreadsheetFormatter> formatters = Lists.array();
        int i = 0;
        for (final SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent component : components) {
            component.prepare(
                i,
                count,
                pattern,
                formatters::add
            );
            i++;
        }

        return SpreadsheetFormatters.spreadsheetPatternCollection(formatters);
    }

    SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitor(final ParserToken token) {
        super();
        this.token = token;
    }

    @Override
    protected void endVisit(final DateSpreadsheetFormatParserToken token) {
        this.saveFormatter(
            SpreadsheetFormatters.dateTime(
                SpreadsheetFormatParserToken.dateTime(
                    token.value(),
                    token.text()
                ),
                LocalDate.class
            )
        );
    }

    @Override
    protected void endVisit(final DateTimeSpreadsheetFormatParserToken token) {
        this.saveFormatter(
            SpreadsheetFormatters.dateTime(
                token,
                LocalDateTime.class
            )
        );
    }

    @Override
    protected void endVisit(final EqualsSpreadsheetFormatParserToken token) {
        this.saveCondition(token);
    }

    @Override
    protected void endVisit(final FractionSpreadsheetFormatParserToken token) {
        this.saveFormatter(
            SpreadsheetFormatters.fraction(
                token,
                (bigDecimal -> {
                    throw new UnsupportedOperationException();
                })
            )
        );
    }

    @Override
    protected void endVisit(final GeneralSpreadsheetFormatParserToken token) {
        final SpreadsheetPatternSpreadsheetFormatter generalFormatter = SpreadsheetFormatters.general();

        final Optional<ColorSpreadsheetFormatParserToken> color = SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorGeneralColorSpreadsheetFormatParserTokenVisitor.extractColor(token);

        this.saveFormatter(
            color.map(
                t -> SpreadsheetFormatters.color(
                    t,
                    generalFormatter
                )
            ).orElse(generalFormatter)
        );
    }

    @Override
    protected void endVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
        this.saveCondition(token);
    }

    @Override
    protected void endVisit(final GreaterThanSpreadsheetFormatParserToken token) {
        this.saveCondition(token);
    }

    @Override
    protected void endVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
        this.saveCondition(token);
    }

    @Override
    protected void endVisit(final LessThanSpreadsheetFormatParserToken token) {
        this.saveCondition(token);
    }

    @Override
    protected void endVisit(final NotEqualsSpreadsheetFormatParserToken token) {
        this.saveCondition(token);
    }

    private void saveCondition(final ConditionSpreadsheetFormatParserToken token) {
        this.component().condition = token;
    }

    @Override
    protected void endVisit(final NumberSpreadsheetFormatParserToken token) {
        this.saveFormatter(
            SpreadsheetFormatters.number(token)
        );
    }

    @Override
    protected void endVisit(final TextSpreadsheetFormatParserToken token) {
        this.saveFormatter(
            SpreadsheetFormatters.text(token)
        );
    }

    @Override
    protected void endVisit(final TimeSpreadsheetFormatParserToken token) {
        this.saveFormatter(
            SpreadsheetFormatters.dateTime(
                SpreadsheetFormatParserToken.dateTime(
                    token.value(),
                    token.text()
                ),
                LocalTime.class
            )
        );
    }

    private void saveFormatter(final SpreadsheetPatternSpreadsheetFormatter formatter) {
        this.component().formatter = formatter;
    }

    @Override
    protected void visit(final SeparatorSymbolSpreadsheetFormatParserToken token) {
        this.component = null;
    }

    private SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent component() {
        if (null == this.component) {
            this.component = SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent.create();
            this.components.add(this.component);
        }
        return this.component;
    }

    private final ParserToken token;

    private SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent component;

    /**
     * A {@link SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent} for each pattern within the pattern.
     */
    private final List<SpreadsheetFormatPatternCreateFormatterSpreadsheetFormatParserTokenVisitorComponent> components = Lists.array();

    @Override
    public String toString() {
        return this.token.toString();
    }
}

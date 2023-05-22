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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDayParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatHourParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatYearParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

final class SpreadsheetPatternForEachComponentSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static void traverse(final ParserToken token,
                         final BiConsumer<SpreadsheetFormatParserTokenKind, String> consumer) {
        Objects.requireNonNull(consumer, "consumer");

        new SpreadsheetPatternForEachComponentSpreadsheetFormatParserTokenVisitor(consumer)
                .accept(token);
    }

    SpreadsheetPatternForEachComponentSpreadsheetFormatParserTokenVisitor(final BiConsumer<SpreadsheetFormatParserTokenKind, String> consumer) {
        super();
        this.consumer = consumer;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateParserToken token) {
        this.minute = false;
        return super.startVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateTimeParserToken token) {
        this.minute = false;
        return super.startVisit(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDayParserToken token) {
        this.minute = false;
    }

    @Override
    protected void visit(final SpreadsheetFormatHourParserToken token) {
        this.minute = true;
    }

    @Override
    protected void visit(final SpreadsheetFormatYearParserToken token) {
        this.minute = false;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTimeParserToken token) {
        this.minute = true;
        return super.startVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
        final Optional<SpreadsheetFormatParserTokenKind> kind = token.kind();

        if (kind.isPresent()) {
            this.consumer.accept(
                    kind.get(),
                    token.text()
            );
        }

        return Visiting.CONTINUE;
    }

    private final BiConsumer<SpreadsheetFormatParserTokenKind, String> consumer;

    private boolean minute;

    @Override
    public String toString() {
        return this.consumer.toString();
    }
}

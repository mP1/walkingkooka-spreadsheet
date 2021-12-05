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

package walkingkooka.spreadsheet.engine;

import walkingkooka.NeverError;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParentParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTokenVisitor;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A {@link SpreadsheetParserTokenVisitor} that handles visiting and updating {@link SpreadsheetCellReferenceParserToken}
 * so cell references during a fill operation.
 */
final class BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor extends BasicSpreadsheetEngineSpreadsheetParserTokenVisitor {

    /**
     * Accepts a token tree and updates rows and columns.
     */
    static SpreadsheetParserToken expressionFixReferences(final SpreadsheetParserToken token,
                                                          final int xOffset,
                                                          final int yOffset) {
        return xOffset == 0 && yOffset == 0 ?
                token :
                expressionFixReferences0(token, xOffset, yOffset);
    }

    private static SpreadsheetParserToken expressionFixReferences0(final SpreadsheetParserToken token,
                                                                   final int xOffset,
                                                                   final int yOffset) {
        final BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor visitor = new BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(xOffset, yOffset);
        visitor.accept(token);

        final List<ParserToken> tokens = visitor.children;
        final int count = tokens.size();
        if (1 != count) {
            throw new IllegalStateException("Expected only 1 child but got " + count + "=" + tokens);
        }

        return tokens.get(0).cast(SpreadsheetParserToken.class);
    }

    /**
     * Package private ctor use static method.
     */
    // @VisibleForTesting
    BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(final int xOffset, final int yOffset) {
        super();
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    // leaf ......................................................................................................

    @Override
    Optional<SpreadsheetColumnReferenceParserToken> visitColumn(final SpreadsheetColumnReferenceParserToken token) {
        final SpreadsheetColumnReference reference = token.value();

        SpreadsheetColumnReferenceParserToken replacement;

        final SpreadsheetReferenceKind kind = reference.referenceKind();
        switch (kind) {
            case ABSOLUTE:
                replacement = token;
                break;
            case RELATIVE:
                replacement = this.fixColumnReference(reference);
                break;
            default:
                replacement = NeverError.unhandledEnum(kind, SpreadsheetReferenceKind.values());
                break;
        }

        return Optional.of(replacement);
    }

    private SpreadsheetColumnReferenceParserToken fixColumnReference(final SpreadsheetColumnReference reference) {
        final SpreadsheetColumnReference updated = reference.add(this.xOffset);
        return SpreadsheetParserToken.columnReference(updated, updated.toString());
    }

    private final int xOffset;

    @Override
    Optional<SpreadsheetRowReferenceParserToken> visitRow(final SpreadsheetRowReferenceParserToken token) {
        final SpreadsheetRowReference reference = token.value();

        SpreadsheetRowReferenceParserToken replacement;

        final SpreadsheetReferenceKind kind = reference.referenceKind();
        switch (kind) {
            case ABSOLUTE:
                replacement = token;
                break;
            case RELATIVE:
                replacement = this.fixRowReference(reference);
                break;
            default:
                replacement = NeverError.unhandledEnum(kind, SpreadsheetReferenceKind.values());
                break;
        }

        return Optional.of(replacement);
    }

    private SpreadsheetRowReferenceParserToken fixRowReference(final SpreadsheetRowReference reference) {
        final SpreadsheetRowReference updated = reference.add(this.yOffset);
        return SpreadsheetParserToken.rowReference(updated, updated.toString());
    }

    private final int yOffset;

    // helpers..........................................................................................................

    @Override
    void enter0() {
        // nop
    }

    @Override
    <PP extends SpreadsheetParentParserToken> SpreadsheetParserToken exit0(final PP parent,
                                                                           final List<ParserToken> children,
                                                                           final BiFunction<List<ParserToken>, String, PP> factory) {
        return factory.apply(children, ParserToken.text(children));
    }

    @Override
    void leaf(final Optional<? extends SpreadsheetParserToken> token) {
        if (token.isPresent()) {
            this.add(token.get());
        }
    }

    @Override
    public String toString() {
        return this.xOffset + "," + this.yOffset + " " + this.children + ", " + this.previousChildren;
    }
}

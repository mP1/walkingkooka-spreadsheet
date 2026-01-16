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

package walkingkooka.spreadsheet.environment;

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;

/**
 * A {@link SpreadsheetEnvironmentContext} that wraps a {@link EnvironmentContext}, with guards to stop attempts to
 * set/remove {@link #SERVER_URL}.
 * <br>
 * Note if the given {@link EnvironmentContext} is a {@link SpreadsheetEnvironmentContext} it is not wrapped and returned.
 */
final class BasicSpreadsheetEnvironmentContext implements SpreadsheetEnvironmentContext,
    EnvironmentContextDelegator,
    TreePrintable {

    static SpreadsheetEnvironmentContext with(final EnvironmentContext context) {

        return context instanceof SpreadsheetEnvironmentContext ?
            (SpreadsheetEnvironmentContext) context :
            new BasicSpreadsheetEnvironmentContext(
                Objects.requireNonNull(context, "context")
            );
    }

    private BasicSpreadsheetEnvironmentContext(final EnvironmentContext context) {
        super();
        this.context = context;
    }

    // SpreadsheetEnvironmentContext....................................................................................

    @Override
    public AbsoluteUrl serverUrl() {
        return this.environmentValueOrFail(SERVER_URL);
    }

    @Override
    public SpreadsheetId spreadsheetId() {
        return this.environmentValueOrFail(SPREADSHEET_ID);
    }

    @Override
    public void setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        this.context.setEnvironmentValue(
            SPREADSHEET_ID,
            Objects.requireNonNull(spreadsheetId, "spreadsheetId")
        );
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    public SpreadsheetEnvironmentContext cloneEnvironment() {
        final EnvironmentContext before = this.context;
        final EnvironmentContext after = before.cloneEnvironment();
        return before == after ?
            this :
            new BasicSpreadsheetEnvironmentContext(after);
    }

    @Override
    public SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        return BasicSpreadsheetEnvironmentContext.with(environmentContext);
    }

    @Override
    public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                        final T value) {
        if(SERVER_URL.equals(name)) {
            throw new IllegalArgumentException("Cannot set Read only value: " + name);
        }

        this.context.setEnvironmentValue(
            name,
            value
        );
    }

    @Override
    public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        if(SERVER_URL.equals(name)) {
            throw new IllegalArgumentException("Cannot remove Read only value: " + name);
        }

        this.context.removeEnvironmentValue(name);
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.context;
    }

    private final EnvironmentContext context;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.context.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof BasicSpreadsheetEnvironmentContext &&
                this.equals0((BasicSpreadsheetEnvironmentContext) other));
    }

    private boolean equals0(final BasicSpreadsheetEnvironmentContext other) {
        return this.context.equals(other.context);
    }

    @Override
    public String toString() {
        return this.context.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            TreePrintable.printTreeOrToString(
                this.context,
                printer
            );
        }
        printer.println();
    }
}

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

package walkingkooka.spreadsheet.storage;

import walkingkooka.Either;
import walkingkooka.collect.set.ImmutableSortedSet;
import walkingkooka.collect.set.Sets;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDeltaProperties;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

final class BasicSpreadsheetStorageContext implements SpreadsheetStorageContext,
    SpreadsheetEnvironmentContextDelegator {

    static BasicSpreadsheetStorageContext with(final SpreadsheetEngine spreadsheetEngine,
                                               final SpreadsheetContext spreadsheetContext) {
        return new BasicSpreadsheetStorageContext(
            Objects.requireNonNull(spreadsheetEngine, "spreadsheetEngine"),
            Objects.requireNonNull(spreadsheetContext, "spreadsheetContext")
        );
    }

    private BasicSpreadsheetStorageContext(final SpreadsheetEngine spreadsheetEngine,
                                           final SpreadsheetContext spreadsheetContext) {
        super();
        this.spreadsheetEngine = spreadsheetEngine;
        this.spreadsheetContext = spreadsheetContext;
    }

    @Override
    public Set<SpreadsheetCell> loadCells(final SpreadsheetExpressionReference cellsOrLabel) {
        return this.spreadsheetEngine.loadCells(
            cellsOrLabel,
            SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
            Sets.of(SpreadsheetDeltaProperties.CELLS),
            this.spreadsheetEngineContext()
        ).cells();
    }

    @Override
    public Set<SpreadsheetCell> saveCells(final Set<SpreadsheetCell> cells) {
        return this.spreadsheetEngine.saveCells(
            cells,
            this.spreadsheetEngineContext()
        ).cells();
    }

    @Override
    public void deleteCells(final SpreadsheetExpressionReference cellsOrLabel) {
        this.spreadsheetEngine.deleteCells(
            cellsOrLabel,
            this.spreadsheetEngineContext()
        );
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        final Iterator<SpreadsheetLabelMapping> labels = this.spreadsheetEngine.loadLabel(
                labelName,
                this.spreadsheetEngineContext()
            ).labels()
            .iterator();

        return Optional.ofNullable(
            labels.hasNext() ?
                labels.next() :
                null
        );
    }

    @Override
    public SpreadsheetLabelMapping saveLabel(final SpreadsheetLabelMapping label) {
        return this.spreadsheetEngine.saveLabel(
            label,
            this.spreadsheetEngineContext()
        ).labels()
            .iterator()
            .next();
    }

    @Override
    public void deleteLabel(final SpreadsheetLabelName labelName) {
        this.spreadsheetEngine.deleteLabel(
            labelName,
            this.spreadsheetEngineContext()
        );
    }

    @Override
    public Set<SpreadsheetLabelName> findLabelsByName(final String labelName,
                                                      final int offset,
                                                      final int count) {
        return this.spreadsheetEngine.findLabelsByName(
                labelName,
                offset,
                count,
                this.spreadsheetEngineContext()
            ).labels()
            .stream()
            .map(SpreadsheetLabelMapping::label)
            .collect(ImmutableSortedSet.collector(Comparator.naturalOrder()));
    }

    private final SpreadsheetEngine spreadsheetEngine;

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.spreadsheetEngineContext()
            .canConvert(value, type);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> target) {
        return this.spreadsheetEngineContext()
            .convert(
                value,
                target
            );
    }

    private SpreadsheetEngineContext spreadsheetEngineContext() {
        return this.spreadsheetContext.spreadsheetEngineContext();
    }

    // SpreadsheetContext...............................................................................................

    @Override
    public SpreadsheetMetadata createMetadata(final EmailAddress user,
                                              final Optional<Locale> locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        return this.spreadsheetContext.loadMetadata(id);
    }

    @Override
    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        return this.spreadsheetContext.saveMetadata(metadata);
    }

    @Override
    public void deleteMetadata(final SpreadsheetId id) {
        this.spreadsheetContext.deleteMetadata(id);
    }

    @Override
    public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                   final int offset,
                                                                   final int count) {

        return this.spreadsheetContext.findMetadataBySpreadsheetName(
            name,
            offset,
            count
        );
    }

    // SpreadsheetEnvironmentContextDelegator...........................................................................

    @Override
    public SpreadsheetStorageContext cloneEnvironment() {
        return new BasicSpreadsheetStorageContext(
            this.spreadsheetEngine,
            this.spreadsheetContext.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetStorageContext setEnvironmentContext(final EnvironmentContext context) {
        final SpreadsheetContext before = this.spreadsheetContext;
        final SpreadsheetContext after = before.setEnvironmentContext(context);

        return before == after ?
            this :
            new BasicSpreadsheetStorageContext(
                this.spreadsheetEngine,
                after
            );
    }

    @Override
    public SpreadsheetStorageContext setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        this.spreadsheetContext.setSpreadsheetId(spreadsheetId);
        return this;
    }

    @Override
    public SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.spreadsheetContext;
    }

    private final SpreadsheetContext spreadsheetContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.spreadsheetContext.toString();
    }
}

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

package walkingkooka.spreadsheet.meta.store;

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.store.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStoreAction;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public final class SpreadsheetCellStoreActionSpreadsheetMetadataStoreTest extends SpreadsheetMetadataStoreTestCase<SpreadsheetCellStoreActionSpreadsheetMetadataStore> {

    @Test
    public void testSaveSameMetadataTwice() {
        final SpreadsheetCellStoreActionSpreadsheetMetadataStore store = this.createStore(
            SpreadsheetCellStores.fake() // interactions will throw UOE
        );

        final SpreadsheetMetadata metadata = store.save(
            this.metadata()
        );
        store.save(metadata);
    }

    @Test
    public void testSaveSameMetadataTwice2() {
        final SpreadsheetCellStoreActionSpreadsheetMetadataStore store = this.createStore(
            SpreadsheetCellStores.fake() // interactions will throw UOE
        );

        final SpreadsheetMetadata metadata = store.save(this.metadata()
            .set(
                SpreadsheetMetadataPropertyName.STYLE,
                TextStyle.EMPTY
                    .set(TextStylePropertyName.COLOR, Color.BLACK)
            )
        );
        store.save(metadata);
    }

    @Test
    public void testSaveOnlyTimestampsUpdated() {
        final SpreadsheetCellStoreActionSpreadsheetMetadataStore store = this.createStore(
            SpreadsheetCellStores.fake() // interactions will throw UOE
        );

        final SpreadsheetMetadata metadata = store.save(
            this.metadata()
        );

        store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                metadata.getOrFail(SpreadsheetMetadataPropertyName.AUDIT_INFO)
                    .setModifiedTimestamp(LocalDateTime.now())
            )
        );
    }

    // parse pattern....................................................................................................

    @Test
    public void testSaveParsePattern() {
        final AtomicInteger cleared = new AtomicInteger();

        final SpreadsheetCellStoreActionSpreadsheetMetadataStore store = SpreadsheetCellStoreActionSpreadsheetMetadataStore.with(
            SpreadsheetMetadataStores.treeMap(
                CREATE_TEMPLATE,
                LocalDateTime::now
            ),
            (id) -> new FakeSpreadsheetCellStore() {
                @Override
                public void clearParsedFormulaExpressions() {
                    cleared.incrementAndGet();
                }
            }
        );

        SpreadsheetMetadata metadata = store.save(
            this.metadata()
        );

        this.checkEquals(
            0,
            cleared.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("0.00").spreadsheetParserSelector()
            )
        );

        this.checkEquals(
            1,
            cleared.get()
        );
    }

    @Test
    public void testSaveParsePatternSameTwice() {
        final AtomicInteger cleared = new AtomicInteger();

        final SpreadsheetCellStoreActionSpreadsheetMetadataStore store = SpreadsheetCellStoreActionSpreadsheetMetadataStore.with(
            createTreeMap(),
            (id) -> new FakeSpreadsheetCellStore() {
                @Override
                public void clearParsedFormulaExpressions() {
                    cleared.incrementAndGet();
                }
            }
        );

        SpreadsheetMetadata metadata = store.save(
            this.metadata()
        );

        this.checkEquals(
            0,
            cleared.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .spreadsheetParserSelector()
            )
        );

        this.checkEquals(
            1,
            cleared.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .spreadsheetParserSelector()
            )
        );

        this.checkEquals(
            1,
            cleared.get()
        );
    }

    @Test
    public void testSaveParsePatternSameTwice2() {
        final AtomicInteger cleared = new AtomicInteger();

        final SpreadsheetCellStoreActionSpreadsheetMetadataStore store = SpreadsheetCellStoreActionSpreadsheetMetadataStore.with(
            createTreeMap(),
            (id) -> new FakeSpreadsheetCellStore() {
                @Override
                public void clearParsedFormulaExpressions() {
                    cleared.incrementAndGet();
                }
            }
        );

        SpreadsheetMetadata metadata = store.save(
            this.metadata()
        );

        this.checkEquals(
            0,
            cleared.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .spreadsheetParserSelector()
            ).set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    EmailAddress.parse("different@example.com"),
                    LocalDateTime.MIN,
                    EmailAddress.parse("different@example.com"),
                    LocalDateTime.MAX
                )
            )
        );

        this.checkEquals(
            1,
            cleared.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .spreadsheetParserSelector()
            )
        );

        this.checkEquals(
            1,
            cleared.get()
        );
    }

    @Test
    public void testSaveParsePatternDifferent() {
        final AtomicInteger cleared = new AtomicInteger();

        final SpreadsheetCellStoreActionSpreadsheetMetadataStore store = SpreadsheetCellStoreActionSpreadsheetMetadataStore.with(
            createTreeMap(),
            (id) -> new FakeSpreadsheetCellStore() {
                @Override
                public void clearParsedFormulaExpressions() {
                    cleared.incrementAndGet();
                }
            }
        );

        SpreadsheetMetadata metadata = store.save(
            this.metadata()
        );

        this.checkEquals(
            0,
            cleared.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .spreadsheetParserSelector()
            )
        );

        this.checkEquals(
            1,
            cleared.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("0.000")
                    .spreadsheetParserSelector()
            )
        );

        this.checkEquals(
            2,
            cleared.get()
        );
    }

    @Test
    public void testSaveParsePatternThenTextFormat() {
        final AtomicInteger clearParsed = new AtomicInteger();
        final AtomicInteger clearFormatted = new AtomicInteger();

        final SpreadsheetCellStoreActionSpreadsheetMetadataStore store = SpreadsheetCellStoreActionSpreadsheetMetadataStore.with(
            createTreeMap(),
            (id) -> new FakeSpreadsheetCellStore() {
                @Override
                public void clearParsedFormulaExpressions() {
                    clearParsed.incrementAndGet();
                }

                @Override
                public void clearFormatted() {
                    clearFormatted.incrementAndGet();
                }
            }
        );

        SpreadsheetMetadata metadata = store.save(
            this.metadata()
        );

        this.checkEquals(
            0,
            clearParsed.get()
        );
        this.checkEquals(
            0,
            clearFormatted.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .spreadsheetParserSelector()
            )
        );

        this.checkEquals(
            1,
            clearParsed.get()
        );
        this.checkEquals(
            0,
            clearFormatted.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
                SpreadsheetPattern.parseTextFormatPattern("@@")
                    .spreadsheetFormatterSelector()
            )
        );

        this.checkEquals(
            1,
            clearParsed.get()
        );
        this.checkEquals(
            1,
            clearFormatted.get()
        );
    }

    @Test
    public void testSaveParsePatternThenTextFormatTwice() {
        final AtomicInteger clearParsed = new AtomicInteger();
        final AtomicInteger clearFormatted = new AtomicInteger();

        final SpreadsheetCellStoreActionSpreadsheetMetadataStore store = SpreadsheetCellStoreActionSpreadsheetMetadataStore.with(
            createTreeMap(),
            (id) -> new FakeSpreadsheetCellStore() {
                @Override
                public void clearParsedFormulaExpressions() {
                    clearParsed.incrementAndGet();
                }

                @Override
                public void clearFormatted() {
                    clearFormatted.incrementAndGet();
                }
            }
        );

        SpreadsheetMetadata metadata = store.save(
            this.metadata()
        );

        this.checkEquals(
            0,
            clearParsed.get()
        );
        this.checkEquals(
            0,
            clearFormatted.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .spreadsheetParserSelector()
            )
        );

        this.checkEquals(
            1,
            clearParsed.get()
        );
        this.checkEquals(
            0,
            clearFormatted.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
                SpreadsheetPattern.parseTextFormatPattern("@@")
                    .spreadsheetFormatterSelector()
            )
        );

        this.checkEquals(
            1,
            clearParsed.get()
        );
        this.checkEquals(
            1,
            clearFormatted.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
                SpreadsheetPattern.parseTextFormatPattern("@@@")
                    .spreadsheetFormatterSelector()
            )
        );

        this.checkEquals(
            1,
            clearParsed.get()
        );
        this.checkEquals(
            2,
            clearFormatted.get()
        );
    }

    @Test
    public void testSaveParsePatternThenTextFormatTwice2() {
        final AtomicInteger clearParsed = new AtomicInteger();
        final AtomicInteger clearFormatted = new AtomicInteger();

        final SpreadsheetCellStoreActionSpreadsheetMetadataStore store = SpreadsheetCellStoreActionSpreadsheetMetadataStore.with(
            createTreeMap(),
            (id) -> new FakeSpreadsheetCellStore() {
                @Override
                public void clearParsedFormulaExpressions() {
                    clearParsed.incrementAndGet();
                }

                @Override
                public void clearFormatted() {
                    clearFormatted.incrementAndGet();
                }
            }
        );

        SpreadsheetMetadata metadata = store.save(
            this.metadata()
        );

        this.checkEquals(
            0,
            clearParsed.get()
        );
        this.checkEquals(
            0,
            clearFormatted.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .spreadsheetParserSelector()
            )
        );

        this.checkEquals(
            1,
            clearParsed.get()
        );
        this.checkEquals(
            0,
            clearFormatted.get()
        );

        metadata = store.save(
            metadata.set(
                SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
                SpreadsheetPattern.parseTextFormatPattern("@@")
                    .spreadsheetFormatterSelector()
            )
        );

        this.checkEquals(
            1,
            clearParsed.get()
        );
        this.checkEquals(
            1,
            clearFormatted.get()
        );

        metadata = store.save(
            metadata.remove(
                SpreadsheetMetadataPropertyName.TEXT_FORMATTER
            )
        );

        this.checkEquals(
            1,
            clearParsed.get()
        );
        this.checkEquals(
            2,
            clearFormatted.get()
        );
    }

    private SpreadsheetMetadata metadata() {
        final LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        return SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.with(
                EmailAddress.parse("creator@example.com"),
                yesterday,
                EmailAddress.parse("creator@example.com"),
                yesterday
            )
        ).set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.ENGLISH
        );
    }

    @Override
    public SpreadsheetCellStoreActionSpreadsheetMetadataStore createStore() {
        return this.createStore(
            SpreadsheetCellStores.treeMap()
        );
    }

    private SpreadsheetCellStoreActionSpreadsheetMetadataStore createStore(final SpreadsheetCellStore cellStore) {
        return SpreadsheetCellStoreActionSpreadsheetMetadataStore.with(
            createTreeMap(),
            (id) -> cellStore
        );
    }

    private SpreadsheetMetadataStore createTreeMap() {
        return SpreadsheetMetadataStores.treeMap(
            CREATE_TEMPLATE,
            LocalDateTime::now
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCellStoreActionSpreadsheetMetadataStore> type() {
        return SpreadsheetCellStoreActionSpreadsheetMetadataStore.class;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetCellStoreAction.class.getSimpleName();
    }
}

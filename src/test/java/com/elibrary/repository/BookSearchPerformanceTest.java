package com.elibrary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.domain.PageRequest;

@DataJpaTest(properties = {
        "logging.level.root=WARN",
        "logging.level.org.hibernate.SQL=OFF"
})
class BookSearchPerformanceTest {

    private static final int BOOK_COUNT = 10_000;
    private static final int SEARCH_COUNT = 100;

    @Autowired private BookRepository bookRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    void catalogueSearchMeetsTheTwoSecondP95TargetForTenThousandTitles() {
        insertBooks();

        // Warm up the query and database caches before collecting the sample.
        for (int i = 0; i < 10; i++) {
            assertThat(bookRepository.searchActive("benchmark", PageRequest.of(0, 24)).getContent())
                    .hasSize(24);
        }

        List<Long> elapsedNanos = new ArrayList<>(SEARCH_COUNT);
        for (int i = 0; i < SEARCH_COUNT; i++) {
            long startedAt = System.nanoTime();
            var result = bookRepository.searchActive("benchmark", PageRequest.of(i % 5, 24));
            elapsedNanos.add(System.nanoTime() - startedAt);
            assertThat(result.getTotalElements()).isEqualTo(BOOK_COUNT);
        }

        elapsedNanos.sort(Long::compareTo);
        long p95Millis = elapsedNanos.get((int) Math.ceil(SEARCH_COUNT * 0.95) - 1) / 1_000_000;
        System.out.printf("Catalogue search p95: %d ms (%d titles, %d measured searches, H2)%n",
                p95Millis, BOOK_COUNT, SEARCH_COUNT);
        assertThat(p95Millis).isLessThan(2_000);
    }

    private void insertBooks() {
        jdbcTemplate.batchUpdate(
                "insert into books (title, isbn, author, category, description, active) values (?, ?, ?, ?, ?, ?)",
                new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(java.sql.PreparedStatement statement, int index)
                            throws java.sql.SQLException {
                        statement.setString(1, "Benchmark Title " + index);
                        statement.setString(2, "BENCHMARK-ISBN-" + index);
                        statement.setString(3, "Benchmark Author " + index);
                        statement.setString(4, "Benchmark Category");
                        statement.setString(5, "Performance fixture");
                        statement.setBoolean(6, true);
                    }

                    @Override
                    public int getBatchSize() {
                        return BOOK_COUNT;
                    }
                });
    }
}

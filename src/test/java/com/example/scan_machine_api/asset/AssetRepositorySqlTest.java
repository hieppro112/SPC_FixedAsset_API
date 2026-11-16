package com.example.scan_machine_api.asset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/** Checks the SQL text actually sent to SQL Server (text blocks strip trailing spaces). */
class AssetRepositorySqlTest {

	// A keyword glued to the next token, e.g. "ANDf.PositionAA" or "''GROUP".
	private static final Pattern GLUED = Pattern.compile("(?i)\\b(AND|WHERE|ON|BY)[A-Za-z]|''[A-Za-z]");

	private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
	private final AssetRepository repo = new AssetRepository(jdbc);

	private static void assertWellFormed(String sql) {
		assertFalse(GLUED.matcher(sql).find(), () -> "keyword glued to next token in:\n" + sql);
	}

	@Test
	void findAreas_sqlIsWellFormed() {
		repo.findAreas("Factory 2");
		ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
		verify(jdbc).query(sql.capture(), any(RowMapper.class), eq("Factory 2"));
		assertWellFormed(sql.getValue());
		assertTrue(sql.getValue().contains("GROUP BY f.Factory, f.PositionAA"));
	}

	@Test
	void findTags_sqlIsWellFormed() {
		repo.findTags();
		ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
		verify(jdbc).query(sql.capture(), any(RowMapper.class));
		assertWellFormed(sql.getValue());
	}

	@Test
	void findMachines_sqlIsWellFormed() {
		repo.findMachines("Factory 2", "A1-1");
		ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
		verify(jdbc).query(sql.capture(), any(RowMapper.class), eq("Factory 2"), eq("A1-1"));
		assertWellFormed(sql.getValue());
	}

	@Test
	void findFactories_sqlIsWellFormed() {
		repo.findFactories();
		ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
		verify(jdbc).queryForList(sql.capture(), eq(String.class));
		assertWellFormed(sql.getValue());
	}
}

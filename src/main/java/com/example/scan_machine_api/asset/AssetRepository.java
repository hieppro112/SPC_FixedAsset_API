package com.example.scan_machine_api.asset;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Read-only queries. The DataSource points at F2Database; the RFID table lives in
 * the KVH database on the same server, so it is referenced by its full name.
 */
@Repository
public class AssetRepository {

	private static final String HAS_AREA = "f.PositionAA IS NOT NULL AND LTRIM(RTRIM(f.PositionAA)) <> ''";

	private final JdbcTemplate jdbc;

	public AssetRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<String> findFactories() {
		return jdbc.queryForList(
				"SELECT DISTINCT f.Factory FROM dbo.F2_FIXED_ASSET f WHERE " + HAS_AREA + " ORDER BY f.Factory",
				String.class);
	}

	public List<AreaDto> findAreas(String factory) {
		return jdbc.query("""
				SELECT f.Factory, f.PositionAA, COUNT(*) AS Total, COUNT(r.ID) AS Tagged
				FROM dbo.F2_FIXED_ASSET f
				LEFT JOIN KVH.dbo.FixedAssetRFID r ON r.CodeMachine = f.MachineCode
				WHERE f.Factory = ? AND """ + HAS_AREA + """
				 GROUP BY f.Factory, f.PositionAA ORDER BY f.PositionAA""",
				(rs, i) -> new AreaDto(rs.getString("Factory"), rs.getString("PositionAA"),
						rs.getInt("Total"), rs.getInt("Tagged")),
				factory);
	}

	public List<MachineDto> findMachines(String factory, String positionAA) {
		return jdbc.query("""
				SELECT f.MachineCode, f.FAName, f.Floor, r.CodeRFID
				FROM dbo.F2_FIXED_ASSET f
				LEFT JOIN KVH.dbo.FixedAssetRFID r ON r.CodeMachine = f.MachineCode
				WHERE f.Factory = ? AND f.PositionAA = ?
				ORDER BY f.MachineCode""",
				(rs, i) -> new MachineDto(rs.getString("MachineCode"), rs.getString("FAName"),
						rs.getString("Floor"), rs.getString("CodeRFID")),
				factory, positionAA);
	}

	// Return the asset table's MachineCode: the join ignores case/trailing spaces, but the app
	// matches tags to the machine list by this exact value.
	public List<TagDto> findTags() {
		return jdbc.query("""
				SELECT r.CodeRFID, f.MachineCode AS CodeMachine, f.Factory, f.PositionAA
				FROM KVH.dbo.FixedAssetRFID r
				JOIN dbo.F2_FIXED_ASSET f ON f.MachineCode = r.CodeMachine
				WHERE """ + HAS_AREA,
				(rs, i) -> new TagDto(rs.getString("CodeRFID"), rs.getString("CodeMachine"),
						rs.getString("Factory"), rs.getString("PositionAA")));
	}
}

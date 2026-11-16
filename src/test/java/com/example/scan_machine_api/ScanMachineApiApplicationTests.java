package com.example.scan_machine_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.scan_machine_api.asset.AssetRepository;

// Runs without a database: DataSource is excluded and the repository is mocked.
@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration")
class ScanMachineApiApplicationTests {

	@MockitoBean
	private AssetRepository assetRepository;

	@Test
	void contextLoads() {
	}

}

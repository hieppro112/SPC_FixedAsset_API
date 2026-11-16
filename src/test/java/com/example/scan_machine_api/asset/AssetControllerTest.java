package com.example.scan_machine_api.asset;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AssetController.class)
class AssetControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private AssetRepository repo;

	@Test
	void factories_returnsList() throws Exception {
		when(repo.findFactories()).thenReturn(List.of("Factory 2"));

		mvc.perform(get("/api/factories"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0]").value("Factory 2"));
	}

	@Test
	void areas_passesFactory() throws Exception {
		when(repo.findAreas("Factory 2")).thenReturn(List.of(new AreaDto("Factory 2", "A1-1", 21, 3)));

		mvc.perform(get("/api/areas").param("factory", "Factory 2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].positionAA").value("A1-1"))
				.andExpect(jsonPath("$[0].total").value(21))
				.andExpect(jsonPath("$[0].tagged").value(3));
		verify(repo).findAreas("Factory 2");
	}

	@Test
	void machines_requiresBothParams() throws Exception {
		mvc.perform(get("/api/areas/machines").param("factory", "Factory 2"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void machines_nullTagSerializedAsNull() throws Exception {
		when(repo.findMachines("Factory 2", "A1-1"))
				.thenReturn(Collections.singletonList(new MachineDto("A-1508", "CNC Auto Lathe", "1F", null)));

		mvc.perform(get("/api/areas/machines").param("factory", "Factory 2").param("positionAA", "A1-1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].machineCode").value("A-1508"))
				// jsonPath().exists() treats a null value as missing, so check the raw body
				.andExpect(content().string(containsString("\"codeRFID\":null")));
	}

	@Test
	void tags_returnsAll() throws Exception {
		when(repo.findTags()).thenReturn(List.of(new TagDto("E200001D350B12181240C5C9", "A-1039", "Factory 2", "A1-1")));

		mvc.perform(get("/api/tags"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].codeRFID").value("E200001D350B12181240C5C9"))
				.andExpect(jsonPath("$[0].machineCode").value("A-1039"))
				.andExpect(jsonPath("$[0].factory").value("Factory 2"))
				.andExpect(jsonPath("$[0].positionAA").value("A1-1"));
	}
}

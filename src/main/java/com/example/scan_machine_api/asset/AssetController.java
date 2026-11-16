package com.example.scan_machine_api.asset;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Read-only endpoints used by the C72 app. An area is identified by (factory, positionAA). */
@RestController
@RequestMapping("/api")
public class AssetController {

	private final AssetRepository repo;

	public AssetController(AssetRepository repo) {
		this.repo = repo;
	}

	@GetMapping("/factories")
	public List<String> factories() {
		return repo.findFactories();
	}

	@GetMapping("/areas")
	public List<AreaDto> areas(@RequestParam String factory) {
		return repo.findAreas(factory);
	}

	@GetMapping("/areas/machines")
	public List<MachineDto> machines(@RequestParam String factory, @RequestParam String positionAA) {
		return repo.findMachines(factory, positionAA);
	}

	@GetMapping("/tags")
	public List<TagDto> tags() {
		return repo.findTags();
	}
}

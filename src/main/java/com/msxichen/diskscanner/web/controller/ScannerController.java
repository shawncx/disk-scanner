package com.msxichen.diskscanner.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.msxichen.diskscanner.core.DiskScannerAsync;
import com.msxichen.diskscanner.core.model.ScanConfiguration;
import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.core.model.ScanProgress;
import com.msxichen.diskscanner.core.model.ScanResult;
import com.msxichen.diskscanner.io.ScanConfigurationReader;
import com.msxichen.diskscanner.web.entity.GetDirectoryInfoResponse;
import com.msxichen.diskscanner.web.entity.GetFileInfoResponse;
import com.msxichen.diskscanner.web.entity.GetScanProgressResponse;
import com.msxichen.diskscanner.web.entity.GetSummaryInfoResponse;
import com.msxichen.diskscanner.web.entity.StartScanResponse;

@RestController
@RequestMapping("/scanner")
public class ScannerController {

	private static final Logger LOGGER = LogManager.getLogger();

	@GetMapping("reeting")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hello " + name;
	}

	@PostMapping("start")
	public ResponseEntity<StartScanResponse> scan(@RequestBody ScanConfiguration config) {
		ScanConfigurationReader reader = new ScanConfigurationReader();
		try {
			LOGGER.info("Get scan start request");
			ScanContext context = reader.buildScanContext(config);
			DiskScannerAsync scanner = new DiskScannerAsync();
			scanner.scan(context);

			String uuid = ScannerManager.INSTANCE.addScanner(scanner);
			LOGGER.info("Scann started with uuid " + uuid);
			StartScanResponse resp = new StartScanResponse(true, uuid, null);
			return ResponseEntity.accepted().body(resp);
		} catch (Exception e) {
			LOGGER.error("Start scan failed. ", e);
			StartScanResponse resp = new StartScanResponse(false, null, e.getMessage());
			return ResponseEntity.badRequest().body(resp);
		}
	}

	@GetMapping("progress")
	public ResponseEntity<GetScanProgressResponse> getProgress(@RequestParam(value = "uuid") String uuid) {
		LOGGER.info("Get progress with uuid " + uuid);
		DiskScannerAsync scanner = ScannerManager.INSTANCE.getScanner(uuid);
		if (scanner == null) {
			LOGGER.info("Scanner with uuid: " + uuid + " does not exist.");
			return ResponseEntity.notFound().build();
		} else {
			ScanProgress progress = scanner.getProgress();
			GetScanProgressResponse resp = new GetScanProgressResponse(progress);
			return ResponseEntity.ok(resp);
		}
	}

	@GetMapping("summary-info")
	public ResponseEntity<GetSummaryInfoResponse> getSummaryInfo(@RequestParam(value = "uuid") String uuid) {
		LOGGER.info("Get summary info with uuid " + uuid);
		DiskScannerAsync scanner = ScannerManager.INSTANCE.getScanner(uuid);
		if (scanner == null) {
			LOGGER.info("Scanner with uuid: " + uuid + " does not exist.");
			return ResponseEntity.notFound().build();
		} else {
			ScanResult result = scanner.getScanResult();
			GetSummaryInfoResponse resp = new GetSummaryInfoResponse(result.getSummaryInfo());
			return ResponseEntity.ok(resp);
		}
	}

	@GetMapping("directory-info")
	public ResponseEntity<GetDirectoryInfoResponse> getDirectoryInfo(@RequestParam(value = "uuid") String uuid) {
		LOGGER.info("Get directory info with uuid " + uuid);
		DiskScannerAsync scanner = ScannerManager.INSTANCE.getScanner(uuid);
		if (scanner == null) {
			LOGGER.info("Scanner with uuid: " + uuid + " does not exist.");
			return ResponseEntity.notFound().build();
		} else {
			ScanResult result = scanner.getScanResult();
			GetDirectoryInfoResponse resp = new GetDirectoryInfoResponse(result.getDirectoryInfo());
			return ResponseEntity.ok(resp);
		}
	}

	@GetMapping("file-info")
	public ResponseEntity<GetFileInfoResponse> getFileInfo(@RequestParam(value = "uuid") String uuid) {
		LOGGER.info("Get directory info with uuid " + uuid);
		DiskScannerAsync scanner = ScannerManager.INSTANCE.getScanner(uuid);
		if (scanner == null) {
			LOGGER.info("Scanner with uuid: " + uuid + " does not exist.");
			return ResponseEntity.notFound().build();
		} else {
			ScanResult result = scanner.getScanResult();
			GetFileInfoResponse resp = new GetFileInfoResponse(result.getFileInfo());
			return ResponseEntity.ok(resp);
		}
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

}

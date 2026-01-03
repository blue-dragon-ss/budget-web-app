package com.example.minimal.item;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.minimal.common.constants.ApiPaths;
import com.example.minimal.common.constants.Regexes;
import com.example.minimal.common.constants.ValidationConstraints;
import com.example.minimal.common.exception.error.ErrorMessage;
import com.example.minimal.item.dto.P201Response;
import com.example.minimal.item.dto.P203Request;
import com.example.minimal.item.dto.P203Response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping(ApiPaths.ITEMS_BASE)
@Validated
public class ItemController {

	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	// 明細一覧取得
	@GetMapping
	public ResponseEntity<P201Response> getItems(
			@RequestParam("yearMonth") @NotBlank(message = ErrorMessage.VAL_YEAR_MONTH_NOT_BLANK)
			@Size(min = ValidationConstraints.YEAR_MONTH_LENGTH, max = ValidationConstraints.YEAR_MONTH_LENGTH, message = ErrorMessage.VAL_YEAR_MONTH_SIZE)
			@Pattern(regexp = Regexes.YEAR_MONTH, message = ErrorMessage.VAL_YEAR_MONTH_PATTERN) String yearMonth) {
		P201Response res = itemService.getItems(yearMonth);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	// 明細CSV読込
	@PostMapping(path = ApiPaths.IMPORT_CSV, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<P203Response> importCsv(@Valid @ModelAttribute P203Request req) {
		P203Response res = itemService.importCsv(req);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}
}

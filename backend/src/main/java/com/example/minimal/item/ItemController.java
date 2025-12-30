package com.example.minimal.item;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.minimal.common.constants.ApiPaths;
import com.example.minimal.item.dto.P203Request;
import com.example.minimal.item.dto.P203Response;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiPaths.ITEMS_BASE)
public class ItemController {

	private final ItemService itemService;;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	@PostMapping(path = ApiPaths.IMPORT_CSV, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<P203Response> importCsv(@Valid @ModelAttribute P203Request req) {

		P203Response res = itemService.importCsv(req);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}
}

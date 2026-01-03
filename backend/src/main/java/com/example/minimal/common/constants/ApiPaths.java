package com.example.minimal.common.constants;

public final class ApiPaths {
	private ApiPaths() {
	}

	public static final String API_V1 = "/api/v1";

	// Members
	public static final String MEMBERS_BASE = API_V1 + "/members";

	// Items
	public static final String ITEMS_BASE = API_V1 + "/items";

	// Categories
	public static final String CATEGORIES_BASE = API_V1 + "/categories";

	// Common actions
	public static final String CREATE = "/create";
	public static final String IMPORT_CSV = "/import/csv";

}

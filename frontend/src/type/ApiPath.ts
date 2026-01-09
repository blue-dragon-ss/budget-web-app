const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "";
const BASE_PATH = API_BASE + "/api/v1";

export const CATEGORY_PATH = {
    MEMBERS_BASE_PATH : BASE_PATH + "/members",
    ITEMS_BASE_PATH : BASE_PATH + "/items",
    CATEGORIES_BASE_PATH : BASE_PATH + "/categories",
}

export const COMMAND_PATH = {
    CREATE_PATH : "/create",
    UPDATE_PATH : "/update",
    IMPORT_CSV_PATH : "/import/csv",
}
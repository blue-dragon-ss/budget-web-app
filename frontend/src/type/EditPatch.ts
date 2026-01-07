export type EditableField = "title" | "categoryId" | "memo"; 

export type EditPatch = {
    itemId: string;
    title?: string;
    categoryId? :number;
    memo?: string;
}
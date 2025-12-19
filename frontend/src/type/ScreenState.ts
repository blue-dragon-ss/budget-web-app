export type ScreenState =
 | "OK"
 | "ERROR"
 | "Loading"
 | "ForceUpdate"
 | "checking";

export const isLoadingScreen = (s: ScreenState) => s === "Loading" || s === "checking";
export const isOkScreen = (s: ScreenState) => s === "OK";
export const isErrorScreen = (s: ScreenState) => s === "ERROR";
export const isForceUpdateScreen = (s: ScreenState) => s === "ForceUpdate";
export const isOkOrForceUpdateScreen = (s: ScreenState) => isOkScreen(s) || isForceUpdateScreen(s);
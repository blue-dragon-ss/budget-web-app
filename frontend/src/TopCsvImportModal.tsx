import { useEffect, useRef} from "react";
import "./CsvImportButton";
import { CsvImportButton } from "./CsvImportButton";
import { type P203ItemsCsvImportResponse } from "./type/P203ItemsCsvImportResponse.ts";

type Props = {
    open: boolean;
    csvLoading: boolean;
    csvImportResult: P203ItemsCsvImportResponse | null;
    selectedFile: File | null;
    errorMessage203Api: string;
    onClose: () => void;
    onFileSelected: (file: File) => void;
}

export const TopCsvImportModal = ({
    open, 
    csvLoading, 
    csvImportResult, 
    selectedFile, 
    errorMessage203Api,
    onClose, 
    onFileSelected}: Props) => {
    const TopCsvImportModalRef = useRef<HTMLDialogElement | null>(null);

    // 親Compのモーダル管理フラグとモーダルの状態を合わせる
    useEffect(() => {
        const modal = TopCsvImportModalRef.current;
        // モーダル自体が無ければ何もしない
        if(!modal) return;
        
        // 親Compで管理するモーダルフラグがtrueモーダルが開いてなければ開く
        if(open && !modal.open) {
            modal.showModal();
        } else if(!open && modal.open) {
            // 親Compで管理するモーダルフラグがfalseでモーダルが開いていれば閉じる
            modal.close();
        }
    }, [open]);

    // <dialog> 側で閉じられたときに親の状態も閉じる
    useEffect(() => {
        const modal = TopCsvImportModalRef.current;
        // モーダル自体が無ければ何もしない
        if (!modal) return;

        const handleClose = () => {
            // すでに close 済みなら何もしなくてよいが、
            // 親とズレないよう念のため onClose を呼ぶ
            onClose();
        };

        modal.addEventListener("close", handleClose);
        return () => modal.removeEventListener("close", handleClose);
    }, [onClose]);

    // キャンセルをクリックしたときのハンドラ
    const handleCancelClick = () => {
        // 直接 modalRef.current?.close() を呼ぶ代わりに、
        // 親の状態更新を通して閉じる
        onClose();
    };

    // OKをクリックしたときのハンドラ
    const handleOkClick = () => {
        onClose();
    };

    // 読み込み結果のエラーリスト
    const errorList = csvImportResult?.errors;

    const isApiError = errorMessage203Api !== "";

    // ファイル読込前管理フラグ
    const isPrevLoading = csvImportResult === null && !csvLoading && !isApiError;
    // 結果表示の管理フラグ
    const isAfterLoading = csvImportResult !== null && !csvLoading && !isApiError;
    // エラー結果フラグ
    const isError = errorList !== null && errorList !== undefined;
    

    return (
        <dialog ref={TopCsvImportModalRef} closedby="none" className="modal csvImportModal">
            <h3 className="modal__title">CSV読込</h3>
            {isPrevLoading && (
                <>
                    <p className="modal__text">CSVファイルをアップロードしてください</p>
                    <div className="modal__section">
                        <CsvImportButton 
                            onFileSelected={onFileSelected} />
                    </div>
                    <div className="modal__actions">
                        <button id="importCancel" className="modal__btn modal__btn--secondary" onClick={handleCancelClick}>キャンセル</button>
                    </div>
                </>
            )}
            {selectedFile && (<p className="modal__text modal__fileName">{selectedFile.name}</p>)}
            {csvLoading && (
                <>
                    <p className="modal__text">CSVファイルを読み込み中です</p>
                </>
            )}
            {isAfterLoading && (
                <>
                    {!isError ? (
                        <>
                            <p className="modal__text">全 {csvImportResult.total} 件 読み込み完了しました</p>
                        </>
                    ) : (
                        <>
                            <p  className="modal__text">
                                {csvImportResult.total} 件中 {csvImportResult.failed} 件にエラーがあるため、取り込みを中止しました<br/>
                                お手数ですが、CSVを修正してから再度取り込んでください
                            </p>
                            <div  className="modal__errorBox">
                                <div className="modal__errorTitle">エラー一覧</div>
                                <div className="modal__errorList">
                                    {errorList.map((e) => (
                                        <div className="modal__errorItem" key={e.line}>
                                            {e.line} 行目：{e.message}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </>
                    )}

                    <div className="modal__actions">
                        <button
                            id="importOk"
                            className="modal__btn modal__btn--primary"
                            onClick={handleOkClick}
                        >
                            OK
                        </button>
                    </div>
                </>   
            )}
            {isApiError && (
                <>
                    <p className="modal__text modal__apiError">{errorMessage203Api}</p>
                    <div className="modal__actions">
                        <button
                            id="importOk"
                            className="modal__btn modal__btn--primary"
                            onClick={handleOkClick}
                        >
                            OK
                        </button>
                    </div>
                </>
            )}
        </dialog>
    )
}

export default TopCsvImportModal;
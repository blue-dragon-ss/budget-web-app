import { useEffect, useRef} from "react";
import "./TopUpdateModal.css"

type Props = {
    open: boolean;
    updateNum : number;
    onClose: () => void;
    onUpdate: () => void;
}

export const TopUpdateModal = ({open, updateNum, onClose, onUpdate}: Props) => {
    const topUpdateModalRef = useRef<HTMLDialogElement | null>(null);

    // 親Compのモーダル管理フラグとモーダルの状態を合わせる
    useEffect(() => {
        const modal = topUpdateModalRef.current;
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
        const modal = topUpdateModalRef.current;
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

    const handleCancelClick = () => {
        // 直接 modalRef.current?.close() を呼ぶ代わりに、
        // 親の状態更新を通して閉じる
        onClose();
    };

    return (
        <dialog ref={topUpdateModalRef} closedby="none">
            <h3>更新確認</h3>
            <p>{updateNum} 件を更新します。よろしいですか？</p>
            <button id="updateCancel" onClick={handleCancelClick}>キャンセル</button>
            <button autoFocus id="updateOk" onClick={onUpdate}>更新確定</button>
        </dialog>
    )
}

export default TopUpdateModal;
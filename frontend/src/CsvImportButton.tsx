import React, { useRef } from "react";

type FileUploadButtonProps = {
  // 親に選択されたファイルを渡すためのコールバック
  onFileSelected: (file: File) => void;
};

export function CsvImportButton(props: FileUploadButtonProps) {
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const handleClick = () => {
    if (fileInputRef.current != null) {
      fileInputRef.current.click();
    }
  };

  const handleChange: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    const files = event.target.files;
    if (files == null || files.length === 0) {
      return;
    }

    const file = files[0];

    // ここでは accept 属性で csv に絞っているので、そのまま渡しています
    // 必要なら拡張子チェックなども追加できます
    props.onFileSelected(file);
  };

  return (
    <div>
      <button type="button" onClick={handleClick}>
        PCから選択
      </button>

      <input
        ref={fileInputRef}
        type="file"
        // csv のみに限定
        accept=".csv,text/csv"
        style={{ display: "none" }}
        onChange={handleChange}
      />
    </div>
  );
}

package com.example.minimal.common.util;

import java.util.UUID;

/**
 * ID 生成ユーティリティ（テスト容易性のためラップ）。
 * ULID を使う場合はライブラリ導入 or 実装をここに集約してください。
 */
public class IdGenerator {

    /**
     * 現在は UUID を返します。ULID へ置換する場合はここを差し替え。
     */
    public static String newId() {
        return UUID.randomUUID().toString();
    }
}

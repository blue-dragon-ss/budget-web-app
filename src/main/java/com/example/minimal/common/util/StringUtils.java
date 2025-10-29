package com.example.minimal.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.hibernate.exception.ConstraintViolationException;

import com.example.minimal.common.constants.SQLState;

public class StringUtils {
	
	private static final String SHA_256 = "SHA-256";
	
	  /**
	   * 文字列のトリム（null 安全版）
	   * @param s
	   * @return
	   */
	  public static String trim(String s) { return s == null ? null : s.trim(); }
	  /**
	   * メールアドレスの正規化（トリム＋小文字化、null/空文字は null に）
	   * @param s
	   * @return
	   */
	  public static String normalizeEmail(String s) {
	    if (s == null || s.isBlank()) return null;
	    return s.trim().toLowerCase();
	  }
	  /**
	   * 文字列の安全な取得（null を空文字に変換）
	   * @param s
	   * @return 
	   */
	  public static String safe(String s) { return s == null ? "" : s; }

	  /**
	   * 文字列の SHA-256 ハッシュ値を取得
	   * @param s
	   * @return SHT-256 ハッシュ値の 16 進文字列
	   */
	  public static String sha256(String s) {
	    try {
	      MessageDigest md = MessageDigest.getInstance(SHA_256);
	      byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));
	      StringBuilder sb = new StringBuilder();
	      for (byte b : dig) sb.append(String.format("%02x", b));
	      return sb.toString();
	    } catch (Exception e) {
	      throw new RuntimeException(e);
	    }
	  }
}

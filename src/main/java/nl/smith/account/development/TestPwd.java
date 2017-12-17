package nl.smith.account.development;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class TestPwd {

	public static void main(String[] args) {

		String password = "Geheim";
		String salt = "$QW4Py/hP";
		String r = md5(password + salt);

		System.out.println("MD5 in hex: " + r);
		for (int i = 0; i < r.length(); i = i + 2) {
			System.out.print(Integer.valueOf(r.substring(i, i + 2), 16).intValue() + " ");
		}
		System.out.println();
		for (int i = 0; i < r.length(); i = i + 2) {

			System.out.print((char) Integer.valueOf(r.substring(i, i + 2), 16).intValue() + " ");
		}
		System.out.println();
		salt = "$1$QW4Py/hP";
		r = md5(password + salt);

		System.out.println("MD5 in hex: " + r);
		for (int i = 0; i < r.length(); i = i + 2) {
			System.out.print(Integer.valueOf(r.substring(i, i + 2), 16).intValue() + " ");
		}
		System.out.println();
		for (int i = 0; i < r.length(); i = i + 2) {
			System.out.print((char) Integer.valueOf(r.substring(i, i + 2), 16).intValue() + " ");
		}
	}

	public static String md5(String input) {
		String md5 = null;

		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			System.out.println(digest.getAlgorithm());
			digest.update(input.getBytes(), 0, input.length());
			md5 = new BigInteger(1, digest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return md5;
	}
}

package nl.smith.account.development;

import java.io.IOException;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public abstract class TestPwd {

	public static void main(String[] args) throws IOException {
		int i = 0;
		while (i < 10) {
			String password = "25Colisa labiosa";
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(password);

			System.out.println(hashedPassword);
			i++;
		}

	}

	public static void main2(String[] args) {
		String rawPasword = "Nowayin1";

		String salt = "";
		// byte[] salt = salt1.getBytes(Charset.forName("UTF-8"));

		MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder("MD5");
		String encodePassword = encoder.encodePassword(rawPasword, salt);

		System.out.println(encodePassword);
		// System.out.println(getAsString(encodePasswordal));
	}

	private static String getAsString(String v) {
		return v.length() == 0 ? "" : Character.valueOf((char) Integer.valueOf(v.substring(0, 2), 16).intValue()) + getAsString(v.substring(2));
	}

}

package uk.co.stikman.stikbot.util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Iterator;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import uk.co.stikman.tokeniser.TokenList;
import uk.co.stikman.tokeniser.Tokeniser;

public class Utils {
	
	
	

	public static String hashPassword(String pass, String salt) {
		try {
			byte[] bsalt = new byte[16];
			byte[] src = salt.getBytes();
			for (int i = 0; i < bsalt.length; ++i) {
				if (i < src.length)
					bsalt[i] = src[i];
				else
					bsalt[i] = 0;
			}
			KeySpec spec = new PBEKeySpec(pass.toCharArray(), bsalt, 65536, 128);
			SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = f.generateSecret(spec).getEncoded();
			return new BigInteger(1, hash).toString(16);
		} catch (NoSuchAlgorithmException ex1) {
			throw new RuntimeException(ex1);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}

	}

	public static String makeLength(String s, int len, boolean padright) {
		int i = len - s.length();
		if (i <= 0)
			return s;
		
		StringBuilder sb = new StringBuilder();
		while (i > 0) {
			sb.append(" ");
			--i;
		}
		if (padright)
			return s + sb;
		else
			return sb + s;
	}

	public static String join(Iterable<?> lst, String joinwith) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<?> i = lst.iterator(); i.hasNext();) {
			sb.append(i.next().toString());
			if (i.hasNext())
				sb.append(joinwith);				
		}
		return sb.toString();
	}

	
	public static TokenList tokenise(String text) {
		return Tokeniser.tokenise(text);
	}

	public static String join(List<?> tmp, String joinwith, String joinwithfinal) {
		StringBuilder sb = new StringBuilder();
		if (tmp.size() == 0)
			return "";
		if (tmp.size() == 1)
			return tmp.get(0).toString();
		if (tmp.size() == 2) 
			return sb.append(tmp.get(0).toString()).append(joinwithfinal).append(tmp.get(1).toString()).toString();
		for (int i = 0; i < tmp.size(); ++i) {
			sb.append(tmp.get(i).toString());
			if (i < tmp.size() - 2)
				sb.append(joinwith);
			else if (i == tmp.size() - 2)
				sb.append(joinwithfinal);
		}
		return sb.toString();
	}	
}

package iit.pc.javainterface.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;

/**
 * Key generator generates a pair of Public/Private keys for the given algorithm
 * @author Caterina Lazaro
 * @version 1.0, Feb 2017
 *
 */
public class KeyGenerator {
	
	private int keyLength;
	private String keyAlgorithm;
	private String randomAlgorithm = "SHA1";
	private String provider = "SUN";
	private KeyPair pair;
	
	KeyPairGenerator keyGen;
	
	public KeyGenerator(String alg, int length){
		//Create an instance of generic generator
		keyLength = length;
		//Pseudo random numbers generator algorithm
		randomAlgorithm = "SHA1PRNG";
		//randomAlgorithm = "SHA1"; //For RSA key generation
		//Encrypt/Decrypt algorithms:
		//	- DSA 'Faster to decrypt, slower to encrypt' --> to do signature
		//	- RSA 'Slower to decrypt, faster to encrypt' --> to encrypt data
		keyAlgorithm  = alg; //RSA/ECB/PKCS1Padding"; //RSA
		provider = "SUN";
		try {
			 keyGen = KeyPairGenerator.getInstance(keyAlgorithm);
			 SecureRandom random = SecureRandom.getInstance(randomAlgorithm, provider);
			 //SecureRandom random = SecureRandom.getInstanceStrong("SHA1PRNG", "SUN");

			 keyGen.initialize(keyLength, random);
			 
			 generatePairKeys();

		} catch (NoSuchAlgorithmException e) {
			for (Provider p : Security.getProviders()) System.out.println(p.getName());

			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	
	private void generatePairKeys(){
		pair = keyGen.generateKeyPair();
	}
	

	
	public PrivateKey getPrivateKey(){
		if (pair != null)
			return pair.getPrivate();
		else
			return null;
	}
	
	public PublicKey getPublicKey(){
		if (pair != null)
			return pair.getPublic();
		else
			return null;
	}

}

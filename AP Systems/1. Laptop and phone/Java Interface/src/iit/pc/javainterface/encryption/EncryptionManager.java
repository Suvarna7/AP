package iit.pc.javainterface.encryption;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Ecnryption manager class: Encrypts and Decrypts messages
 * 	- It stores public/private key of the devices
 *  - It sotres public keys of other connected devices
 *  - It encrypts and decrypts messages
 *  - It performs some signature check
 * @author Caterina Lazaro
 * @version 1.0, Feb 2017
 *
 */
public class EncryptionManager {

	KeyGenerator keyGen_message;
	KeyGenerator keyGen_signature;
	int keySize = 1024;
	int signSize = 512;
	
	//Cipher and Decipher
	Cipher cipherEnc;
	Cipher cipherDec;
	Cipher signEnc;
	
	//Wrap keys to be shared
	Cipher keySharing;
	
	//Encrypt algorithm:
	//	- DSA 'Faster to decrypt, slower to encrypt' --> to do signature
	//	- RSA 'Slower to decrypt, faster to encrypt' --> to encrypt data
	
	//DES Modes: DES/CFB8/NoPadding" and "DES/OFB32/PKCS5Padding
	private String cipherAlg = "RSA"; // "RSA/ECB/PKCS1Padding"; //RSA
	private static String signAlg = "RSA"; // "RSA/ECB/PKCS1Padding"; //RSA

	//private String keyAlg = "RSA";
	private String extraParamKey = "BS";
	
	private Cipher[] signatureKeys;
	private String[] signatureDevices;
	
	//Encryption modes
	
	//Byte encoders and decoder
	private Encoder byteEncoder =Base64.getEncoder();
	private Decoder byteDecoder =Base64.getDecoder();

	//Signature separator
	private String separator = "--";
	
	/**
	 * 
	 */
	public EncryptionManager (){
		//Get DEVICE keys
		keyGen_message = new KeyGenerator(cipherAlg, keySize);
		keyGen_signature = new KeyGenerator(signAlg, signSize);
		
		try {
			cipherEnc = Cipher.getInstance(cipherAlg);
			cipherEnc.init(Cipher.ENCRYPT_MODE, keyGen_message.getPublicKey());

			cipherDec = Cipher.getInstance(cipherAlg);			
			cipherDec.init(Cipher.DECRYPT_MODE, keyGen_message.getPrivateKey());
			
			signEnc = Cipher.getInstance(signAlg);
			signEnc.init(Cipher.ENCRYPT_MODE, keyGen_signature.getPrivateKey());

			
			//keySharing = Cipher.getInstance(keyAlg, extraParamKey);
			//cipherEnc.init(Cipher.WRAP_MODE);


		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Initialize
		signatureKeys  = new Cipher[0];
		signatureDevices  = new String[0];
		
	}
	
	public EncryptionManager (PublicKey pk){
		//Get DEVICE keys
		keyGen_signature = new KeyGenerator(signAlg, signSize);
		try {
			//Encrypt with the other device public key
			cipherEnc = Cipher.getInstance(cipherAlg);
			cipherEnc.init(Cipher.ENCRYPT_MODE, pk);

			//cipherDec = Cipher.getInstance(cipherAlg);			
			//cipherDec.init(Cipher.DECRYPT_MODE, keyGen.getPrivateKey());
			
			signEnc = Cipher.getInstance(signAlg);
			signEnc.init(Cipher.ENCRYPT_MODE, keyGen_signature.getPrivateKey());

			
			//keySharing = Cipher.getInstance(keyAlg, extraParamKey);
			//cipherEnc.init(Cipher.WRAP_MODE);


		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Initialize
		signatureKeys  = new Cipher[0];
		signatureDevices  = new String[0];
		
	}
	
	public PublicKey getPublicKey(boolean signature){
		if (signature)
			return keyGen_signature.getPublicKey();
		else
			return keyGen_message.getPublicKey();
	}
	/**
	 * Convert the public key into String
	 * @param key public key
	 * @return encoded stirng of key
	 */
	public static String getPublicKeyString(PublicKey key){
		try {
			return new String(key.getEncoded(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Convert the encoded public key string into PublicKey
	 * @param key - encoded public key
	 * @return publicKey
	 */
	public static PublicKey recoverPublicKeyString (String key){
		try {
			byte[] pKey = key.getBytes("ISO-8859-1");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pKey);
			KeyFactory kFactory = KeyFactory.getInstance(signAlg);
			return kFactory.generatePublic(keySpec);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * Encrypt the given message with private_key
	 * @param inputMessage
	 * @return
	 */
	public String encryptMessage(String inputMessage){
		
		return encrypt(inputMessage, cipherEnc);
	}
	
	private String encrypt(String message, Cipher c){
		try {
			return new String(c.doFinal(message.getBytes("ISO-8859-1")), "ISO-8859-1");
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String decryptMessage (String receivedMessage){
			
		return decrypt(receivedMessage, cipherDec);
	}
	
	private String decrypt(String message, Cipher c){
		try {
			return new String(c.doFinal(message.getBytes("ISO-8859-1")), "ISO-8859-1");

		} catch (IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Add a new device to communicate with: its signature and name
	 * @param pk public key
	 * @param id name
	 */
	public void newSignature (PublicKey pk, String id){
		//Create a new decoder for the given key
		Cipher newDec;
		try {
			newDec = Cipher.getInstance(signAlg);
			newDec.init(Cipher.DECRYPT_MODE, pk);
			//Key update
			Cipher[] temp = new Cipher[signatureKeys.length+1];
			temp = Arrays.copyOf(signatureKeys, signatureKeys.length+1);
			temp[temp.length-1]= newDec;
			signatureKeys = new Cipher[temp.length];
			signatureKeys = Arrays.copyOf(temp, temp.length);
			
			//New device update
			String[] old = new String[signatureDevices.length +1];
			old = Arrays.copyOf(signatureDevices, signatureDevices.length +1);
			old[old.length -1]=id;
			signatureDevices = Arrays.copyOf(old, old.length);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		
	}
	
	public String addSignature(String message, String signature){
		
		return message+separator+encrypt(signature, signEnc);
	}
	
	/**
	 * Verify that the claimed device sender is actually the one signing
	 * @param sign encrypted signature
	 * @param dev sender device
	 * @return
	 */
	public boolean verifySender (String message, String dev){
		//Extract signature form message

		String [] parts = (message.split(separator));
		String sign = parts[1];
		//Check if the device is included in the current ones
		for (int i =0; i < signatureDevices.length ; i ++){
			if (signatureDevices[i].equals(dev)){
				//Decrypt signature and check whether they match
				if (decrypt(sign, signatureKeys[i]).equals(dev))
					return true;
			}
		}
		return false;
		
	}
	
	

	
	
}

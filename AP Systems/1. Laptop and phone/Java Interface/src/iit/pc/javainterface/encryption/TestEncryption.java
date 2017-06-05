package iit.pc.javainterface.encryption;

public class TestEncryption {
	private EncryptionManager em;
	
	public TestEncryption(){
		em = new EncryptionManager();
	}
	
	public void testED(){
		//First, check public key
		System.out.println("PUBLIC KEY: "+em.getPublicKey(false).toString());
		
		//Encrypt a sample message
		String helloEncryption = "Oh! Suprise, I have prezel for you";
		String messageSend = em.encryptMessage(helloEncryption);
		
		System.out.println("Sending message: "+helloEncryption+ " , "+messageSend);
		
		//Decrypt a sample message
		System.out.println("Decripting message: "+ em.decryptMessage(messageSend));
	}
	
	public void testSignatureEncrypted(){
		
		//SENDER
		String receivedPublic = EncryptionManager.getPublicKeyString(em.getPublicKey(false));
		EncryptionManager senderGenerator = new EncryptionManager(EncryptionManager.recoverPublicKeyString(receivedPublic));
		//New sender
		String sender = "TuperMan";
		
		//RECEIVER UPDATE
		String sentPublic = EncryptionManager.getPublicKeyString(senderGenerator.getPublicKey(true));
		em.newSignature(EncryptionManager.recoverPublicKeyString(sentPublic), sender);
		
		//SignMessage
		String helloEncryption = "Oh! Suprise, I have prezel for you";
		String helloSigned = senderGenerator.addSignature(helloEncryption, sender);
		
		//Encrypt a sample message
		String messageSend = senderGenerator.encryptMessage(helloSigned);
		System.out.println("Sending message: "+helloSigned+ " , "+messageSend);

								
		//Decrypt a sample message
		String decrypted = em.decryptMessage(messageSend);
		System.out.println("Decripting message: "+ decrypted);
		//Check signature
		if ( em.verifySender(decrypted, sender))
			System.out.println("Legit sender :) ");
	}

}

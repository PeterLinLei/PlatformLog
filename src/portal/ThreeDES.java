// 2009-06-04
// HWStatistic, LicenseStatistic, PeraLS, ThreeDES, Portal.
package portal;
//import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import org.apache.commons.codec.binary.*;

/*
	public static byte[] encryptMode(byte[] keybyte, byte[] src) 
	public static byte[] decryptMode(byte[] keybyte, byte[] src)
	public static String byte2hex(byte[] b) 
	public static String encryptKeyToFile( String key, String filePathName )
	public static String decryptKeyFromFile( String filePathName )
*/
public class ThreeDES {

    private static final String Algorithm = "DESede";  // Algorithm: DES, DESede, Blowfish. 
	public static final byte[] keyBytes = {(byte)0x11, (byte)0x22, (byte)0x4F, (byte)0x57, (byte)0x88, (byte)0x10, (byte)0x40, (byte)0x38, (byte)0x28, (byte)0x25, (byte)0x79, (byte)0x51, (byte)0xCB, (byte)0xDD, (byte)0x55, (byte)0x66, (byte)0x77, (byte)0x29, (byte)0x74, (byte)0x98, (byte)0x31, (byte)0x40, (byte)0x36, (byte)0xE2}; 
	
	public static byte[] encryptMode(byte[] keybyte, byte[] src) 
	{
       try {
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

	public static byte[] decryptMode(byte[] keybyte, byte[] src) 
	{
        try {
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

	public static String byte2hex(byte[] b) 
	{
        String hs="";
        String stmp="";
        for (int n=0;n<b.length;n++) {
            stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) hs=hs+"0"+stmp;
            else hs=hs+stmp;
            if (n<b.length-1)  hs=hs+":";
        }
        return hs.toUpperCase();
    }

	public static String encryptKeyToFile( byte[] key, String filePathName )
	{
		RandomAccessFile file = null;
		try{
			File file2 = new File( filePathName );
			if (file2.exists()) {
				file2.delete();
				file2.createNewFile();
			}	
			file = new RandomAccessFile(file2,"rw");
			file.seek(0);
			byte[] encryptKey = encryptMode(keyBytes,key);
			for(int i=0;i<encryptKey.length;i++)
			{
				file.writeByte(encryptKey[i]);
			}
			file.close();
		}catch(IOException e){ 
			System.out.println(e);
			return "fail";
		}
		return "success";					
	}
	
	public static String decryptKeyFromFile( String filePathName )
	{
		String key = new String("NULL");
		RandomAccessFile file = null;
		try{
			file = new RandomAccessFile( filePathName , "r" );
			file.seek(0);
			byte[] key2 = new byte[(int)file.length()];
			file.read(key2);
			key = new String(decryptMode(keyBytes,key2));
			file.close();
		}catch(IOException e){ 
			System.out.println("public static String decryptKeyFromFile( String filePathName )"+e);
		}		
		return key ;
	}

	public static void base64EncryptFile(String source,String destination)
	{
    	try{
    		File classFile = new File(source);
    		FileInputStream classFileInputStream = new FileInputStream(classFile);
    		long classFileLength = classFile.length();
    		byte classFileByte[] = new byte[(int) classFileLength];
    		classFileInputStream.read(classFileByte);
    		// Create encrypt file.
        	RandomAccessFile encryptFile = null;
			File encryptFile2 = new File(destination);
			if (encryptFile2.exists()) {
				encryptFile2.delete();
				encryptFile2.createNewFile();
			}	
			encryptFile = new RandomAccessFile(encryptFile2,"rw");
			encryptFile.seek(0);
			byte[] encryptFileByte = Base64.encodeBase64(classFileByte);
			for(int i=0;i<encryptFileByte.length;i++)
			{
				encryptFile.writeByte(encryptFileByte[i]);
			}
			encryptFile.close();		        	
    	}catch(Exception e){
    		System.out.println(e);
    	} 		
	}
	
	public static void base64DecryptFile(String source,String destination)
	{
    	try{
    		File encryptFile = new File(source);
    		FileInputStream encryptFileInputStream = new FileInputStream(encryptFile);
    		long encryptFileLength = encryptFile.length();
    		byte encryptFileByte[] = new byte[(int) encryptFileLength];
    		encryptFileInputStream.read(encryptFileByte);
    		// Create encrypt file.
        	RandomAccessFile classFile = null;
			File classFile2 = new File(destination);
			if (classFile2.exists()) {
				classFile2.delete();
				classFile2.createNewFile();
			}	
			classFile = new RandomAccessFile(classFile2,"rw");
			classFile.seek(0);
			byte[] classFileByte = Base64.decodeBase64(encryptFileByte);
			for(int i=0;i<classFileByte.length;i++)
			{
				classFile.writeByte(classFileByte[i]);
			}
			classFile.close();		        	
    	}catch(Exception e){
    		System.out.println(e);
    	}    		
	}
	
	public static void DESEncryptFile(String source,String destination)
	{
    	try{
    		File classFile = new File(source);
    		FileInputStream classFileInputStream = new FileInputStream(classFile);
    		long classFileLength = classFile.length();
    		byte classFileByte[] = new byte[(int) classFileLength];
    		classFileInputStream.read(classFileByte);
        	ThreeDES.encryptKeyToFile(classFileByte,destination);
    	}catch(Exception e){
    		System.out.println(e);
    	}		
	}

	public static void DESDecryptFile(String source,String destination)
	{
    	try{
    		File encryptFile = new File(source);
    		FileInputStream encryptFileInputStream = new FileInputStream(encryptFile);
    		long encryptFileLength = encryptFile.length();
    		byte encryptFileByte[] = new byte[(int) encryptFileLength];
    		encryptFileInputStream.read(encryptFileByte);
        	RandomAccessFile classFile = null;
			File classFile2 = new File(destination);
			if (classFile2.exists()) {
				classFile2.delete();
				classFile2.createNewFile();
			}	
			classFile = new RandomAccessFile(classFile2,"rw");
			classFile.seek(0);
			byte[] decryptKey = ThreeDES.decryptMode(ThreeDES.keyBytes, encryptFileByte);
			for(int i=0;i<decryptKey.length;i++)
			{
				classFile.writeByte(decryptKey[i]);
			}
			classFile.close();		
    	}catch(Exception e){
    		System.out.println(e);
    	}
		
	}
	
    public static void main(String[] args)
    {

/*
 * 		
 *		// Create password file
        String dbPassword = new String("pera2008");
        ThreeDES.encryptKeyToFile(dbPassword.getBytes(),"D:/WorkSpace/Portal/WebRoot/password");
		System.out.println(ThreeDES.decryptKeyFromFile("D:/WorkSpace/Portal/WebRoot/password"));
*/ 
		System.out.println("Hello ThressDES!");
    	//ThreeDES.base64EncryptFile("D:\\WorkSpace\\Portal\\WebRoot\\WEB-INF\\classes\\portal\\JobSubmitBean.class", "D:\\WorkSpace\\Portal\\WebRoot\\WEB-INF\\classes\\portal\\JobSubmitBean.class");
//    	ThreeDES.base64DecryptFile("D:\\WorkSpace\\Portal\\WebRoot\\WEB-INF\\classes\\portal\\JobSubmitBean.encrypt", "D:\\WorkSpace\\Portal\\WebRoot\\WEB-INF\\classes\\portal\\JobSubmitBean.class2");
	}
}


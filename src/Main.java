import java.util.*;

public class Main {
	
	public static void main(String[] args) throws Exception {
        String handleText = 
            "--_{{_}}\r\n" +
            "Content-Disposition: form-data; name=\"req\"\r\n\r\n" +
            "I7\r\n" +
            "--_{{_}}\r\n" +
            "Content-Disposition: form-data; name=\"e\"\r\n\r\n" +
            "FzwfdO83-OywrEoqgZUaL1r6kPD2VMDa7FdEHAr_LVYoNbot3Cwv1dT7OglwaPh7XQE8Q5fCe7lq52dE41mBRbKdN80oIXDz\r\n" +
            "--_{{_}}\r\n" +
            "Content-Disposition: form-data; name=\"ev\"\r\n\r\n" +
            "1\r\n" +
            "--_{{_}}--";
        
        String handleTextb = "{\"i\":\"I7\",\"r\":0,\"e\":\"0t29dfFMwn70wxlx3xzlLQX8rbkcwzQOn4ele38HI_ETWhAnfEZ4IpaMwZqXXAbxC0rcn32GS6YnLJbSn-zAS42F6XRxPfJl8zD6oI8m7HTWpvAKlpBHSDfS2TDMpYichbr7I2UHS4yn_Cn5aSlv_ZSLOF_SmepH\"}";
        
        String outputText = NetCryptoCall.detectDecryptDataType(handleText);
        String outputText2 = NetCryptoCall.detectDecryptDataType(handleTextb);
        
		System.out.println(outputText);
        System.out.println(outputText2);
    }
    
}

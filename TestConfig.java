import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;

public class TestConfig {
    public static void main(String[] args) {
        try {
            System.out.println("Testing WeChat Pay SDK configuration...");
            
            // Configuration parameters
            String merchantId = "1683311162";
            String privateKeyPath = "api_Certificate/apiclient_key.pem";
            String merchantSerialNumber = "35E63A455062980FA300C820CB2E0AF03E3F6A45";
            String apiV3Key = "O7pL9kQ2rS5uV8wX3yZ4aB6cD1eF0gHj";
            
            System.out.println("Merchant ID: " + merchantId);
            System.out.println("Private Key Path: " + privateKeyPath);
            System.out.println("Certificate Serial Number: " + merchantSerialNumber);
            System.out.println("APIv3 Key: " + apiV3Key.substring(0, 8) + "...");
            
            // Create configuration
            Config config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(merchantId)
                    .privateKeyFromPath(privateKeyPath)
                    .merchantSerialNumber(merchantSerialNumber)
                    .apiV3Key(apiV3Key)
                    .build();
            
            System.out.println("SUCCESS: Configuration created successfully!");
            System.out.println("WeChat Pay SDK is ready to use.");
            
        } catch (Exception e) {
            System.err.println("ERROR: Configuration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
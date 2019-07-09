package Seeder;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;

import java.io.*;
import java.net.URL;
import java.util.Properties;


public class KbPropertyValues {

    private Properties _prop;
    private String _configurationFileName;

    public KbPropertyValues(String configurationFileName) {
        InputStream inputStream;
        _configurationFileName = configurationFileName;
        _prop = new Properties();
        inputStream = getClass().getClassLoader().getResourceAsStream(_configurationFileName);

        ApplicationTokenCredentials credentials =
                new ApplicationTokenCredentials(System.getenv("AZURE_CLIENT_ID"),
                        System.getenv("AZURE_TENANT_ID"),
                        System.getenv("AZURE_CLIENT_SECRET"), AzureEnvironment.AZURE);
        KeyVaultClient kvc = new KeyVaultClient(credentials);


        try {
            if (inputStream != null) {
                _prop.load(inputStream);
                inputStream.close();
            }
            else {
                throw new FileNotFoundException(
                        String.format("Property file %s not found in the classpath.", _configurationFileName));
            }
        }
        catch (IOException e ) {
            System.out.println("IOException: " + e);
        }
        SecretBundle subscriptionSecret = kvc.getSecret(_prop.getProperty("KeyVaultEndpoint"), "SubscriptionKey" );
        this.setPropertyValues("SubscriptionKey",subscriptionSecret.value());
    }

    public String getPropertyValue(String key) {
        return _prop.getProperty(key);
    }

    public void setPropertyValues(String key, String value) {
        _prop.setProperty(key, value);
    }

    public void storePropertyValues(String path) {
       try {
            if (path != null) {
                OutputStream outputStream = new FileOutputStream(path);
                _prop.store(outputStream, null);
                outputStream.close();
            } else {
                throw new FileNotFoundException(
                        String.format("Property file %s not found in the classpath.", _configurationFileName));
            }
       } catch (IOException e ) {
            System.out.println(String.format(
                    "Unable to save %s file. New properties will not be saved on disk: %s", _configurationFileName, e));
       }
    }
}
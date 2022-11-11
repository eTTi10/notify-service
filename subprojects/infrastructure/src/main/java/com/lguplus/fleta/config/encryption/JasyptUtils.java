package com.lguplus.fleta.config.encryption;


import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JasyptUtils {

    private static final String PREFIX = "ENC(";

    private final StringEncryptor jasyptStringEncryptor;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    public String decryptInOnlyLocalEnv(String encryptedText) {
        if ("local".equalsIgnoreCase(this.activeProfile) && StringUtils.hasText(encryptedText) && encryptedText.startsWith(PREFIX)) {
            return this.jasyptStringEncryptor.decrypt(encryptedText.substring(PREFIX.length()));
        }

        return encryptedText;
    }
}

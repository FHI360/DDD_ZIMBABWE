package org.fhi360.plugins.impilo.services;

import io.github.jbella.snl.core.api.services.MailService;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    @Override
    public void sendEmail(String from, String to, String subject, String content, boolean isMultipart, boolean isHtml) {

    }

    @Override
    public void sendEmail(MimeMessageHelper message) {

    }

    @Override
    public MimeMessageHelper getMimeMessageHelper(boolean isMultipart) {
        return null;
    }
}

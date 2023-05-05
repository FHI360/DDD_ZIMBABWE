package org.fhi360.plugins.impilo.services;

import io.github.jbella.snl.core.api.services.MailService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MailServiceImpl implements MailService {
    @Override
    public void sendEmail(String from, String to, String subject, String content, boolean isMultipart, boolean isHtml) {

    }

    @Override
    public void sendEmailFromTemplate(String email, String templateName, Map<String, Object> variables) {

    }
}

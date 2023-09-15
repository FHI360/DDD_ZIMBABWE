package org.fhi360.plugins.impilo.config;

import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.services.ExtensionService;
import lombok.RequiredArgsConstructor;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JaversAuthorProvider implements AuthorProvider {
    private final ExtensionService extensionService;

    @Override
    public String provide() {
        return extensionService.getPriorityExtension(AuthenticationServiceExtension.class)
                .getCurrentUsername().orElse("System");
    }
}

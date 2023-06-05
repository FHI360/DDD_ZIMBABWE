package org.fhi360.plugins.impilo.services;

import io.github.jbella.snl.core.api.domain.Configuration;
import io.github.jbella.snl.core.api.services.ConfigurationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
    @Override
    public List<Configuration.CreateView> list(String category, String key) {
        return null;
    }

    @Override
    public Optional<String> getValueAsStringForKey(String category, String key) {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> getValueAsBooleanForKey(String category, String key) {
        return Optional.empty();
    }

    @Override
    public Optional<Double> getValueAsNumericForKey(String category, String key) {
        return Optional.empty();
    }

    @Override
    public Optional<LocalDate> getValueAsDateForKey(String category, String key) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

    }
}

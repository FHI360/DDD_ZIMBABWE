package org.fhi360.plugins.impilo.services;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.jbella.snl.core.api.domain.Preference;
import io.github.jbella.snl.core.api.services.PreferenceService;
import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PreferenceServiceImpl implements PreferenceService {
    @Override
    public Preference save(Preference preference) {
        return null;
    }

    @Override
    public void delete(Preference preference) {

    }

    @Override
    public Optional<Preference> getPreference(@NotNull String category, @Nullable String key) {
        return Optional.empty();
    }

    @Override
    public Optional<Double> getNumericValue(@NotNull String category, @Nullable String key) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getStringValue(@NotNull String category, @Nullable String key) {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> getBoolValue(@NotNull String category, @Nullable String key) {
        return Optional.empty();
    }

    @Override
    public Optional<LocalDate> getDateValue(@NotNull String category, @Nullable String key) {
        return Optional.empty();
    }

    @Override
    public Optional<JsonNode> getJsonValue(@NotNull String category, @Nullable String key) {
        return Optional.empty();
    }
}

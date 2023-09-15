package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import io.github.jbella.snl.core.api.domain.Organisation;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The OutletService class is a Java service that lists Organisation entities with a specific type and optional keyword
 * search.
 */
@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class OutletService {
    private final EntityManager em;
    private final EntityViewManager evm;
    private final CriteriaBuilderFactory cbf;

    /**
     * The function returns a list of Organisation.ShortView objects based on a keyword search, with the type set to
     * "OUTLET".
     *
     * @param keyword The "keyword" parameter is a string that represents a search term. It is used to filter the list of
     * organisations based on their name.
     * @return The method is returning a list of objects of type `Organisation.ShortView`.
     */
    @Transactional
    public List<Organisation.ShortView> list(String keyword) {
        var settings = EntityViewSetting.create(Organisation.ShortView.class);
        var cb = cbf.create(em, Organisation.class)
            .where("type").eq("OUTLET");
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + keyword + "%";
            //@formatter:off
            cb.whereOr()
                .where("name").like(false).value(keyword).noEscape()
            .endOr();
            //@formatter:on
        }
        return evm.applySetting(settings, cb).getResultList();
    }
}

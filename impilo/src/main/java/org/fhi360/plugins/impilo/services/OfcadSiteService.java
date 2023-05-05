package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import io.github.jbella.snl.core.api.domain.Organisation;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfcadSiteService {
    private final EntityManager em;
    private final EntityViewManager evm;
    private final CriteriaBuilderFactory cbf;

    @Transactional
    public List<Organisation.ShortView> list(String keyword) {
        var settings = EntityViewSetting.create(Organisation.ShortView.class);
        var cb = cbf.create(em, Organisation.class)
            .where("type").eq("OFCAD Site");
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + keyword + "%";
            //@formatter:off
            cb.whereOr()
                .where("name").like(false).value(keyword).noEscape()
                .where("party.identifiers.value").like(false).value(keyword).noEscape()
            .endOr();
            //@formatter:on
        }
        return evm.applySetting(settings, cb).getResultList();
    }
}

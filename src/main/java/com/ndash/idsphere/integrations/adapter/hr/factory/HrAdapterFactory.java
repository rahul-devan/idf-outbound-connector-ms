package com.ndash.idsphere.integrations.adapter.hr.factory;

import com.ndash.idsphere.integrations.adapter.hr.HrAdapter;
import com.ndash.idsphere.integrations.domain.enums.ExternalSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HrAdapterFactory {

    private final Map<ExternalSource, HrAdapter> adapters;

    public HrAdapterFactory(List<HrAdapter> list) {
        this.adapters = list.stream()
                .collect(Collectors.toMap(HrAdapter::getSource, a -> a));
    }

    public HrAdapter get(ExternalSource source) {
        return adapters.get(source);
    }
}

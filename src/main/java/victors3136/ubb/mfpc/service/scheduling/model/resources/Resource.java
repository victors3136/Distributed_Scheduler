package victors3136.ubb.mfpc.service.scheduling.model.resources;

import victors3136.ubb.mfpc.service.scheduling.model.enums.Table;

sealed public interface Resource permits ResolvedResource, FutureResource {
    Table table();

    String resourceId();

    ResolvedResource get();
}

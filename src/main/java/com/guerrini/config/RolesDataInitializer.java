package com.guerrini.config;

import com.guerrini.model.RolesType;
import com.guerrini.model.RolesTypeEntity;
import com.guerrini.repository.RolesTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RolesDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RolesDataInitializer.class);

    private final RolesTypeRepository rolesTypeRepository;

    public RolesDataInitializer(RolesTypeRepository rolesTypeRepository) {
        this.rolesTypeRepository = rolesTypeRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing roles_type table with enum values");
        for (RolesType rt : RolesType.values()) {
            String name = rt.name();
            if (!rolesTypeRepository.existsByName(name)) {
                RolesTypeEntity entity = new RolesTypeEntity(name);
                rolesTypeRepository.save(entity);
                log.info("Inserted role: {}", name);
            } else {
                log.debug("Role already exists: {}", name);
            }
        }
    }
}


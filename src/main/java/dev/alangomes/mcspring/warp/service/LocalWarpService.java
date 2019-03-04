package dev.alangomes.mcspring.warp.service;

import dev.alangomes.mcspring.hook.security.Audict;
import dev.alangomes.mcspring.hook.security.Authorize;
import dev.alangomes.mcspring.warp.model.Warp;
import dev.alangomes.mcspring.warp.model.WarpDTO;
import dev.alangomes.mcspring.warp.repository.WarpRepository;
import org.bukkit.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("!web")
class LocalWarpService implements WarpService {

    @Autowired
    private WarpRepository warpRepository;

    @Audict
    @Authorize("warp.create")
    @Transactional
    @Override
    public WarpDTO create(String name, Location location) {
        Warp warp = new Warp();
        warp.setName(name);
        warp.setLocation(location.clone());
        return warpRepository.save(warp).toDTO();
    }

    @Override
    public WarpDTO getWarp(String name) {
        return warpRepository.findByName(name).toDTO();
    }

}

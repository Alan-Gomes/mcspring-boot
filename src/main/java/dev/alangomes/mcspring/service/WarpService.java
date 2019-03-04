package dev.alangomes.mcspring.service;

import dev.alangomes.mcspring.hook.security.Audict;
import dev.alangomes.mcspring.hook.security.Authorize;
import dev.alangomes.mcspring.model.Warp;
import dev.alangomes.mcspring.repository.WarpRepository;
import org.bukkit.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarpService {

    @Autowired
    private WarpRepository warpRepository;

    @Audict
    @Authorize("warp.create")
    @Transactional
    public Warp create(String name, Location location) {
        Warp warp = new Warp();
        warp.setName(name);
        warp.setLocation(location.clone());
        return warpRepository.save(warp);
    }

    public Warp getWarp(String name) {
        return warpRepository.findByName(name);
    }

}

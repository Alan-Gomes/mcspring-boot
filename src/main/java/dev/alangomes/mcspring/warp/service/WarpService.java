package dev.alangomes.mcspring.warp.service;

import dev.alangomes.mcspring.warp.model.WarpDTO;
import org.bukkit.Location;

public interface WarpService {

    WarpDTO create(String name, Location location);

    WarpDTO getWarp(String name);

}

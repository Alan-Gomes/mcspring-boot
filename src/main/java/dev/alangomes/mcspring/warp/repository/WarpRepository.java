package dev.alangomes.mcspring.warp.repository;

import dev.alangomes.mcspring.warp.model.Warp;
import org.springframework.data.repository.CrudRepository;

public interface WarpRepository extends CrudRepository<Warp, Integer> {

    Warp findByName(String name);

}

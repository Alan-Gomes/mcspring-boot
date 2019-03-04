package dev.alangomes.mcspring.repository;

import dev.alangomes.mcspring.model.Warp;
import org.springframework.data.repository.CrudRepository;

public interface WarpRepository extends CrudRepository<Warp, Integer> {

    Warp findByName(String name);

}

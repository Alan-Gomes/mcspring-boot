package dev.alangomes.mcspring.warp.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class WarpDTO {

    private Integer id;

    private String name;

    private Location location;

    public Warp toEntity() {
        Warp warp = new Warp();
        BeanUtils.copyProperties(this, warp);
        return warp;
    }

}

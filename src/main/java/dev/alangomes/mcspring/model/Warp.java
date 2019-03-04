package dev.alangomes.mcspring.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import javax.persistence.*;

@Entity
@Table(name = "warp")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Warp {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "location", nullable = false)
    private Location location;

}

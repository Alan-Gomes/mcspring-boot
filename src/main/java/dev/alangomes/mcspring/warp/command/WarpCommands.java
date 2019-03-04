package dev.alangomes.mcspring.warp.command;

import dev.alangomes.mcspring.hook.exception.CommandException;
import dev.alangomes.mcspring.warp.model.WarpDTO;
import dev.alangomes.mcspring.warp.service.WarpService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@Scope("prototype")
public class WarpCommands {

    @Autowired
    private WarpService warpService;

    @Component
    @Scope("prototype")
    @CommandLine.Command(name = "warp")
    public class WarpCommand implements Runnable {

        @Autowired
        private Player player;

        @CommandLine.Parameters(index = "0")
        private String name;

        @Override
        public void run() {
            WarpDTO warp = warpService.getWarp(name);
            if (warp == null) {
                throw new CommandException("Warp não encontrado");
            }
            player.teleport(warp.getLocation());
        }
    }

    @Component
    @Scope("prototype")
    @CommandLine.Command(name = "setwarp")
    public class SetWarpCommand implements Runnable {

        @Autowired
        private Player player;

        @CommandLine.Parameters(index = "0")
        private String name;

        @Override
        public void run() {
            WarpDTO warp = warpService.create(name, player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Warp " + warp.getName() + " criado!");
        }
    }

}

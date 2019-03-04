package dev.alangomes.mcspring.command;

import dev.alangomes.mcspring.hook.exception.CommandException;
import dev.alangomes.mcspring.model.Warp;
import dev.alangomes.mcspring.service.WarpService;
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
            Warp warp = warpService.getWarp(name);
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
            Warp warp = warpService.create(name, player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Warp " + warp.getName() + " criado!");
        }
    }

}

package dev.alangomes.mcspring;

import dev.alangomes.mcspring.hook.security.Audict;
import dev.alangomes.mcspring.hook.security.Authorize;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command
@Scope("prototype")
public class Greeting {

    @Component
    @CommandLine.Command(name = "teste")
    @Authorize("teste")
    @Scope("prototype")
    @Audict
    public class TesteCommand implements Runnable {

        @Autowired
        private Player player;

        @Value("${teste.abc}")
        private String config;

        @Override
        public void run() {
            player.sendMessage("Ola mundo! " + config);
        }
    }
}

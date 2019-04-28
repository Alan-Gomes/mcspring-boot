package dev.alangomes.test.util;

import dev.alangomes.springspigot.SpringSpigotInitializer;

public class SpringSpigotTestInitializer extends SpringSpigotInitializer {

    public SpringSpigotTestInitializer() {
        super(IntegrationTestUtil.mockPlugin());
    }

}

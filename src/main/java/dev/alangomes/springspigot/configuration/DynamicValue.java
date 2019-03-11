package dev.alangomes.springspigot.configuration;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Autowired
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicValue {

    String value();

}
